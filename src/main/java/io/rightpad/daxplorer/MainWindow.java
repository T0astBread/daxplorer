package io.rightpad.daxplorer;

import io.rightpad.daxplorer.data.IndexDataPoint;
import io.rightpad.daxplorer.uxsugar.SelectionChangeMouseListener;
import io.rightpad.daxplorer.uxsugar.ViewportOffsetChangeMouseListener;
import io.rightpad.daxplorer.uxsugar.ViewportSpanYChangeMouseListener;
import io.rightpad.daxplorer.visualization.PointF;
import io.rightpad.daxplorer.visualization.VisualizationPanel;
import io.rightpad.daxplorer.visualization.visualizers.DateSelectionVisualizer;
import io.rightpad.daxplorer.visualization.visualizers.IndexVisualizer;
import io.rightpad.daxplorer.visualization.visualizers.Visualizer;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MainWindow
{
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private JPanel mainPanel;
    private VisualizationPanel visualizationPanel;
    private JList visualizerList;
    private JTextField selectionStartTextField;
    private JTextField selectionEndTextField;
    private JRadioButton downRadioButton;
    private JRadioButton stallingRadioButton;
    private JRadioButton upRadioButton;
    private JTextField viewportSpanXTextField;
    private JTextField viewportOffsetXTextField;
    private JTextField viewportSpanYTextField;
    private JTextField viewportOffsetYTextField;

    private Border defaultTextFieldBorder;
    private DefaultListModel<Visualizer> visualizerListModel;
    private List<IndexDataPoint> indexData;
    private List<IndexDataPoint> selectedIndexData;
    private DateSelectionVisualizer selectionVisualizer;

    public MainWindow()
    {
        loadData();

        initVisualizerList();
        this.visualizationPanel.setChartWidth(25);
        this.visualizationPanel.setChartHeight(2000);

        this.defaultTextFieldBorder = this.selectionStartTextField.getBorder();

        initSelectionTextFieldListeners();
        initMouseSelectionListeners();

        initTrendButtons();

        updateViewportTextFields();
        initViewPortTextFieldListeners();
        initMouseOffsetListeners();

        initMouseSpanYListeners();

        setSelectionStart(LocalDateTime.now().minusDays(6));
        setSelectionEnd(LocalDateTime.now().plusDays(1));

        this.visualizationPanel.visualize();
    }

    private void loadData()
    {
        this.indexData = new ArrayList<>();
        this.visualizationPanel.setIndexData(this.indexData);

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
                (byte) -1
        ));
        this.indexData.add(new IndexDataPoint(
                LocalDateTime.now().minusDays(3),
                1000,
                1600,
                980,
                1400,
                230,
                (byte) 0
        ));
        this.indexData.add(new IndexDataPoint(
                LocalDateTime.now().minusDays(4),
                1600,
                1600,
                980,
                1400,
                230,
                (byte) 0
        ));

        this.visualizationPanel.setPosition(new PointF(
                UtilsKt.daysSinceEpoch(LocalDateTime.now().minusDays(5), ZoneOffset.UTC),
                this.visualizationPanel.getPosition().getY()
        ));
    }

    private void initVisualizerList()
    {
        this.visualizerListModel = new DefaultListModel<>();
        this.visualizerList.setModel(this.visualizerListModel);

        addVisualizer(new IndexVisualizer());
        this.selectionVisualizer = new DateSelectionVisualizer();
        addVisualizer(this.selectionVisualizer);
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

    private void initTrendButtons()
    {
        setTrendButtonsEnabled(false);

        this.upRadioButton.addActionListener(this::onTrendButtonClick);
        this.stallingRadioButton.addActionListener(this::onTrendButtonClick);
        this.downRadioButton.addActionListener(this::onTrendButtonClick);
    }

    private void initMouseSelectionListeners()
    {
        SelectionChangeMouseListener selectionChangeMouseListener = new SelectionChangeMouseListener(this.visualizationPanel, this.selectionVisualizer);
        selectionChangeMouseListener.setOnMouseDrag(() -> {
            setTrendButtonsEnabled(false);
            updateSelectionTextFields();
        });
        selectionChangeMouseListener.setOnMouseRelease(this::updateSelection);

        this.visualizationPanel.addMouseListener(selectionChangeMouseListener);
        this.visualizationPanel.addMouseMotionListener(selectionChangeMouseListener);
    }

    private void updateViewportTextFields()
    {
        this.viewportSpanXTextField.setText(Float.toString(this.visualizationPanel.getChartWidth()));
        this.viewportSpanYTextField.setText(Float.toString(this.visualizationPanel.getChartHeight()));

        int posX = (int) Math.floor(this.visualizationPanel.getPosition().getX());
        LocalDateTime dateOffset = UtilsKt.asEpochDays(posX, ZoneOffset.UTC);
        this.viewportOffsetXTextField.setText(dateOffset.format(DATE_FORMAT));

        this.viewportOffsetYTextField.setText(Float.toString(this.visualizationPanel.getPosition().getY()));
    }

    private void initViewPortTextFieldListeners()
    {
        this.viewportSpanXTextField.addActionListener(viewportSpanTextFieldListener(
                this.viewportSpanXTextField,
                this.visualizationPanel::setChartWidth
        ));
        this.viewportSpanYTextField.addActionListener(viewportSpanTextFieldListener(
                this.viewportSpanYTextField,
                this.visualizationPanel::setChartHeight
        ));

        this.viewportOffsetXTextField.addActionListener(SwingUtils.validatedListener(
                this.viewportOffsetXTextField,
                (textField, actionEvent) -> {
                    LocalDateTime offset = LocalDate.parse(textField.getText(), DATE_FORMAT).atTime(0, 0);
                    this.visualizationPanel.setTimeOffset(offset);
                }
        ));
        this.viewportOffsetYTextField.addActionListener(SwingUtils.validatedListener(
                this.viewportOffsetYTextField,
                (textField, actionEvent) ->
                        this.visualizationPanel.setPositionY(Float.parseFloat(textField.getText()))
        ));
    }

    private ActionListener viewportSpanTextFieldListener(JTextField textField, Consumer<Float> setter)
    {
        return SwingUtils.validatedListener(textField, (textField1, actionEvent) ->
                setter.accept(Float.parseFloat(textField1.getText()))
        );
    }

    private void initMouseOffsetListeners()
    {
        ViewportOffsetChangeMouseListener viewportOffsetChangeMouseListener = new ViewportOffsetChangeMouseListener(this.visualizationPanel);
        this.visualizationPanel.addMouseMotionListener(viewportOffsetChangeMouseListener);
        this.visualizationPanel.addMouseListener(viewportOffsetChangeMouseListener);
    }

    private void initMouseSpanYListeners()
    {
        ViewportSpanYChangeMouseListener viewportSpanYChangeMouseListener = new ViewportSpanYChangeMouseListener(this.visualizationPanel);
        this.visualizationPanel.addMouseWheelListener(viewportSpanYChangeMouseListener);
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
            LocalDateTime date = LocalDate.parse(input, DATE_FORMAT).atTime(0, 0);
            textField.setBorder(this.defaultTextFieldBorder);

            if(e.getSource() == this.selectionStartTextField)
                setSelectionStart(date);
            else
                setSelectionEnd(date);
        }
        catch(DateTimeParseException parseException) {
            textField.setBorder(SwingUtils.INVALID_INPUT_BORDER);
        }
    }

    private LocalDateTime getSelectionStart()
    {
        return this.selectionVisualizer.getSelectionStart();
    }

    private void setSelectionStart(LocalDateTime selectionStart)
    {
        this.selectionVisualizer.setSelectionStart(selectionStart);
        updateSelection();
    }

    private LocalDateTime getSelectionEnd()
    {
        return this.selectionVisualizer.getSelectionEnd();
    }

    private void setSelectionEnd(LocalDateTime selectionEnd)
    {
        this.selectionVisualizer.setSelectionEnd(selectionEnd);
        updateSelection();
    }

    private void updateSelection()
    {
        updateSelectionTextFields();
        updateSelectionList();
        updateTrendButtons();
        this.visualizationPanel.visualize();
    }

    private void updateSelectionTextFields()
    {
        updateSelectionTextField(this.selectionStartTextField, UtilsKt.floor(getSelectionStart()));
        updateSelectionTextField(this.selectionEndTextField, UtilsKt.ceil(getSelectionEnd()));
    }

    private void updateSelectionTextField(JTextField textField, LocalDateTime selection)
    {
        if(selection == null)
            return;

        String text = textField.getText();
        String selectionText = selection.format(DATE_FORMAT);

        if(!text.equals(selectionText)) {
            textField.setText(selectionText);
        }
    }

    private void updateSelectionList()
    {
        this.selectedIndexData = null;
        if(getSelectionStart() == null || getSelectionEnd() == null)
            return;

        LocalDateTime selectionStart = UtilsKt.floor(getSelectionStart()),
                selectionEnd = UtilsKt.ceil(getSelectionEnd());
        this.selectedIndexData = this.indexData.stream()
                .filter(dataPoint ->
                        dataPoint.getTimestamp().isAfter(selectionStart) &&
                                dataPoint.getTimestamp().isBefore(selectionEnd)
                )
                .collect(Collectors.toList());
    }

    private void updateTrendButtons()
    {
        boolean selectionIsInvalid = getSelectionStart() == null || getSelectionEnd() == null;
        setTrendButtonsEnabled(!selectionIsInvalid);
        if(selectionIsInvalid)
            return;

        List<Byte> distinctTrendsInSelection = this.selectedIndexData.stream()
                .map(IndexDataPoint::getTrend)
                .distinct()
                .collect(Collectors.toList());
        boolean hasUniformTrend = distinctTrendsInSelection.size() == 1;

        if(hasUniformTrend)
            setTrendButtonsValue(distinctTrendsInSelection.get(0));
        else
            deselectAllTrendButtons();
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

    private void deselectAllTrendButtons()
    {
        ((DefaultButtonModel) this.downRadioButton.getModel()).getGroup().clearSelection();
    }

    private void onTrendButtonClick(ActionEvent e)
    {
        if(this.selectedIndexData == null)
            return;

        byte newTrendValue = 0; // stalling by default
        if(e.getSource() == this.upRadioButton)
            newTrendValue = 1;
        else if(e.getSource() == this.downRadioButton)
            newTrendValue = -1;
        final byte _newTrendValue = newTrendValue; // thanks Java

        this.selectedIndexData.forEach(dataPoint -> {
            dataPoint.setTrend(_newTrendValue);
        });

        this.visualizationPanel.visualize();
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
        viewportSpanXTextField = new JTextField();
        viewportSpanXTextField.setColumns(5);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 50;
        panel1.add(viewportSpanXTextField, gbc);
        final JLabel label2 = new JLabel();
        label2.setText("Offset X");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label2, gbc);
        viewportOffsetXTextField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(viewportOffsetXTextField, gbc);
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
        viewportSpanYTextField = new JTextField();
        viewportSpanYTextField.setColumns(5);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.ipadx = 50;
        panel1.add(viewportSpanYTextField, gbc);
        viewportOffsetYTextField = new JTextField();
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        panel1.add(viewportOffsetYTextField, gbc);
        final JLabel label5 = new JLabel();
        label5.setText("days");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label5, gbc);
        final JLabel label6 = new JLabel();
        label6.setText("EUR");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 2;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label6, gbc);
        final JLabel label7 = new JLabel();
        label7.setText("EUR");
        gbc = new GridBagConstraints();
        gbc.gridx = 3;
        gbc.gridy = 3;
        gbc.anchor = GridBagConstraints.WEST;
        panel1.add(label7, gbc);
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
        final JLabel label8 = new JLabel();
        Font label8Font = this.$$$getFont$$$(null, -1, 16, label8.getFont());
        if(label8Font != null) label8.setFont(label8Font);
        label8.setText("From");
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(label8, gbc);
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
        final JLabel label9 = new JLabel();
        Font label9Font = this.$$$getFont$$$(null, -1, 16, label9.getFont());
        if(label9Font != null) label9.setFont(label9Font);
        label9.setText("to");
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        panel3.add(label9, gbc);
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
