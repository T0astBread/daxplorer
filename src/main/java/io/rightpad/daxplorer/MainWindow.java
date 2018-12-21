package io.rightpad.daxplorer;

import io.rightpad.daxplorer.data.IndexDataPoint;
import io.rightpad.daxplorer.visualization.PointF;
import io.rightpad.daxplorer.visualization.VisualizationPanel;
import io.rightpad.daxplorer.visualization.visualizers.DateSelectionVisualizer;
import io.rightpad.daxplorer.visualization.visualizers.IndexVisualizer;
import io.rightpad.daxplorer.visualization.visualizers.Visualizer;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MainWindow
{
    private static final byte NO_TREND_BUTTONS_VALUE = -2;
    private static final Border INVALID_INPUT_BORDER = BorderFactory.createLineBorder(Color.RED, 2);

    private JPanel mainPanel;
    private VisualizationPanel visualizationPanel;
    private JList visualizerList;
    private JTextField selectionStartTextField;
    private JTextField selectionEndTextField;
    private JRadioButton downRadioButton;
    private JRadioButton stallingRadioButton;
    private JRadioButton upRadioButton;
    private JTextField textField1;
    private JTextField textField2;

    private Border defaultTextFieldBorder;
    private DefaultListModel<Visualizer> visualizerListModel;
    private List<IndexDataPoint> indexData;
    private List<IndexDataPoint> selectedIndexData;
    private LocalDateTime selectionStart, selectionEnd;

    public MainWindow()
    {
        loadData();

        initVisualizerList();
        this.visualizationPanel.setChartWidth(25);
        this.visualizationPanel.setChartHeight(2000);

        this.defaultTextFieldBorder = this.selectionStartTextField.getBorder();
        initSelectionTextFieldListeners();

        setTrendButtonsEnabled(false);
    }

    private void loadData()
    {
        this.indexData = new ArrayList<>();
        this.indexData.add(new IndexDataPoint(
                LocalDateTime.now().minusDays(1),
                1000,
                1200,
                980,
                1400,
                230,
                (byte) 1
        ));
        this.indexData.add(new IndexDataPoint(
                LocalDateTime.now().minusDays(2),
                700,
                1000,
                700,
                1010,
                130,
                (byte) 1
        ));
        this.indexData.add(new IndexDataPoint(
                LocalDateTime.now().minusDays(3),
                1000,
                1200,
                980,
                1400,
                230,
                (byte) 1
        ));
    }

    private void initVisualizerList()
    {
        this.visualizerListModel = new DefaultListModel<>();
        this.visualizerList.setModel(this.visualizerListModel);

        addVisualizer(new IndexVisualizer());
        addVisualizer(new DateSelectionVisualizer());
    }

    private void initSelectionTextFieldListeners()
    {
        KeyListener selectionTextFieldKeyListener = new KeyListener()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {
            }

            @Override
            public void keyPressed(KeyEvent e)
            {
            }

            @Override
            public void keyReleased(KeyEvent e)
            {
                onSelectionTextFieldChange(e);
            }
        };
        this.selectionStartTextField.addKeyListener(selectionTextFieldKeyListener);
        this.selectionEndTextField.addKeyListener(selectionTextFieldKeyListener);
    }

    public void show()
    {
        JFrame frame = new JFrame("daxplorer");
        frame.setPreferredSize(new Dimension(1000, 600));
        frame.setContentPane(new MainWindow().mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    private void addVisualizer(Visualizer visualizer)
    {
        this.visualizationPanel.addVisualizers(visualizer);
        this.visualizerListModel.addElement(visualizer);
    }

    private void onSelectionTextFieldChange(AWTEvent e)
    {
        JTextField textField = (JTextField) e.getSource();
        try {
            String input = textField.getText();
            LocalDateTime date = LocalDate.parse(input, DateTimeFormatter.ofPattern("dd.MM.yyyy")).atTime(0, 0);
            textField.setBorder(this.defaultTextFieldBorder);

            if(e.getSource() == this.selectionStartTextField)
                setSelectionStart(date);
            else
                setSelectionEnd(date);
        }
        catch(DateTimeParseException parseException) {
            textField.setBorder(INVALID_INPUT_BORDER);
        }
    }

    private void setSelectionStart(LocalDateTime selectionStart)
    {
        this.selectionStart = selectionStart;
        updateSelection();
    }

    private void setSelectionEnd(LocalDateTime selectionEnd)
    {
        this.selectionEnd = selectionEnd;
        updateSelection();
    }

    private void updateSelection()
    {
        updateSelectionList();
        updateTrendButtons();
        updateVisualization();
    }

    private void updateSelectionList()
    {
        this.selectedIndexData = null;
        if(this.selectionStart == null || this.selectionEnd == null)
            return;

        this.selectedIndexData = this.indexData.stream()
                .filter(dataPoint ->
                        dataPoint.getTimestamp().isAfter(this.selectionStart) &&
                                dataPoint.getTimestamp().isBefore(this.selectionEnd)
                )
                .collect(Collectors.toList());
    }

    private void updateTrendButtons()
    {
        boolean selectionIsInvalid = this.selectionStart == null || this.selectionEnd == null;
        setTrendButtonsEnabled(!selectionIsInvalid);
        if(selectionIsInvalid)
            return;

        List<Byte> distinctTrendsInSelection = this.selectedIndexData.stream()
                .map(dataPoint -> dataPoint.getTrend())
                .distinct()
                .collect(Collectors.toList());
        boolean hasUniformTrend = distinctTrendsInSelection.size() == 1;

        if(hasUniformTrend)
            setTrendButtonsValue(distinctTrendsInSelection.get(0));
        else
            setTrendButtonsValue(NO_TREND_BUTTONS_VALUE);
    }

    private void setTrendButtonsEnabled(boolean enabled)
    {
        this.downRadioButton.setEnabled(enabled);
        this.stallingRadioButton.setEnabled(enabled);
        this.upRadioButton.setEnabled(enabled);
    }

    private void setTrendButtonsValue(byte trend)
    {
        this.downRadioButton.setSelected(trend == -1);
        this.stallingRadioButton.setSelected(trend == 0);
        this.upRadioButton.setSelected(trend == 1);
    }

    private void updateVisualization()
    {
        int startDays = UtilsKt.daysSinceEpoch(this.selectionStart, ZoneOffset.UTC);
        int endDays = UtilsKt.daysSinceEpoch(this.selectionEnd, ZoneOffset.UTC);
        int span = endDays - startDays;
        this.visualizationPanel.setPosition(new PointF(startDays, this.visualizationPanel.getPosition().getY()));
        this.visualizationPanel.setChartWidth(span);
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$()
    {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        final JScrollPane scrollPane1 = new JScrollPane();
        scrollPane1.setPreferredSize(new Dimension(0, 0));
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 2;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.ipadx = 250;
        mainPanel.add(scrollPane1, gbc);
        visualizerList = new JList();
        scrollPane1.setViewportView(visualizerList);
        final JPanel panel1 = new JPanel();
        panel1.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridheight = 3;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(panel1, gbc);
        panel1.setBorder(BorderFactory.createTitledBorder("Viewport"));
        final JLabel label1 = new JLabel();
        label1.setText("Span X");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label1, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(spacer1, gbc);
        textField1 = new JTextField();
        textField1.setColumns(5);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 50;
        panel1.add(textField1, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Offset X");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label2, gbc);
        textField2 = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(textField2, gbc);
        final JLabel label3 = new JLabel();
        label3.setText("Span Y");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label3, gbc);
        final JLabel label4 = new JLabel();
        label4.setText("Offset Y");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label4, gbc);
        final JTextField textField3 = new JTextField();
        textField3.setColumns(5);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 50;
        panel1.add(textField3, gbc);
        final JTextField textField4 = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(textField4, gbc);
        final JLabel label5 = new JLabel();
        label5.setText("days");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label5, gbc);
        final JLabel label6 = new JLabel();
        label6.setText("days");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label6, gbc);
        final JPanel panel2 = new JPanel();
        panel2.setLayout(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 5;
        gbc.weightx = 3.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        mainPanel.add(panel2, gbc);
        visualizationPanel = new VisualizationPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 3;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(visualizationPanel, gbc);
        final JPanel panel3 = new JPanel();
        panel3.setLayout(new GridBagLayout());
        Font panel3Font = this.$$$getFont$$$(null, -1, -1, panel3.getFont());
        if(panel3Font != null) panel3.setFont(panel3Font);
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.BOTH;
        panel2.add(panel3, gbc);
        panel3.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(), null));
        final JLabel label7 = new JLabel();
        Font label7Font = this.$$$getFont$$$(null, -1, 16, label7.getFont());
        if(label7Font != null) label7.setFont(label7Font);
        label7.setText("From");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(label7, gbc);
        selectionStartTextField = new JTextField();
        Font selectionStartTextFieldFont = this.$$$getFont$$$(null, -1, 16, selectionStartTextField.getFont());
        if(selectionStartTextFieldFont != null) selectionStartTextField.setFont(selectionStartTextFieldFont);
        selectionStartTextField.setText("");
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 50;
        gbc.insets = new Insets(0, 5, 0, 5);
        panel3.add(selectionStartTextField, gbc);
        final JLabel label8 = new JLabel();
        Font label8Font = this.$$$getFont$$$(null, -1, 16, label8.getFont());
        if(label8Font != null) label8.setFont(label8Font);
        label8.setText("to");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(label8, gbc);
        selectionEndTextField = new JTextField();
        Font selectionEndTextFieldFont = this.$$$getFont$$$(null, -1, 16, selectionEndTextField.getFont());
        if(selectionEndTextFieldFont != null) selectionEndTextField.setFont(selectionEndTextFieldFont);
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 50;
        gbc.insets = new Insets(0, 5, 0, 5);
        panel3.add(selectionEndTextField, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 4;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel3.add(spacer2, gbc);
        downRadioButton = new JRadioButton();
        Font downRadioButtonFont = this.$$$getFont$$$(null, -1, 16, downRadioButton.getFont());
        if(downRadioButtonFont != null) downRadioButton.setFont(downRadioButtonFont);
        downRadioButton.setText("Down (-1)");
        gbc = new GridBagConstraints();
        gbc.gridx = 5;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(downRadioButton, gbc);
        stallingRadioButton = new JRadioButton();
        Font stallingRadioButtonFont = this.$$$getFont$$$(null, -1, 16, stallingRadioButton.getFont());
        if(stallingRadioButtonFont != null) stallingRadioButton.setFont(stallingRadioButtonFont);
        stallingRadioButton.setText("Stalling (0)");
        gbc = new GridBagConstraints();
        gbc.gridx = 6;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(stallingRadioButton, gbc);
        upRadioButton = new JRadioButton();
        Font upRadioButtonFont = this.$$$getFont$$$(null, -1, 16, upRadioButton.getFont());
        if(upRadioButtonFont != null) upRadioButton.setFont(upRadioButtonFont);
        upRadioButton.setText("Up (1)");
        gbc = new GridBagConstraints();
        gbc.gridx = 7;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(upRadioButton, gbc);
        ButtonGroup buttonGroup;
        buttonGroup = new ButtonGroup();
        buttonGroup.add(upRadioButton);
        buttonGroup.add(upRadioButton);
        buttonGroup.add(downRadioButton);
        buttonGroup.add(stallingRadioButton);
    }

    /**
     * @noinspection ALL
     */
    private Font $$$getFont$$$(String fontName, int style, int size, Font currentFont)
    {
        if(currentFont == null) return null;
        String resultName;
        if(fontName == null) {resultName = currentFont.getName();}
        else {
            Font testFont = new Font(fontName, Font.PLAIN, 10);
            if(testFont.canDisplay('a') && testFont.canDisplay('1')) {resultName = fontName;}
            else {resultName = currentFont.getName();}
        }
        return new Font(resultName, style >= 0 ? style : currentFont.getStyle(), size >= 0 ? size : currentFont.getSize());
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() { return mainPanel; }
}
