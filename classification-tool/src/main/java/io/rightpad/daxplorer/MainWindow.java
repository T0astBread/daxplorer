package io.rightpad.daxplorer;

import io.rightpad.daxplorer.data.datapoints.absolute.IndexDataPoint;
import io.rightpad.daxplorer.data.datapoints.absolute.TimeSeriesDataPoint;
import io.rightpad.daxplorer.data.serialization.CsvKt;
import io.rightpad.daxplorer.utils.FileIO;
import io.rightpad.daxplorer.utils.SwingUtils;
import io.rightpad.daxplorer.utils.UtilsKt;
import io.rightpad.daxplorer.uxsugar.SelectionChangeMouseListener;
import io.rightpad.daxplorer.uxsugar.ViewportOffsetChangeMouseListener;
import io.rightpad.daxplorer.uxsugar.ViewportSpanYChangeMouseListener;
import io.rightpad.daxplorer.visualization.PointF;
import io.rightpad.daxplorer.visualization.VisualizationPanel;
import io.rightpad.daxplorer.visualization.visualizers.AverageVisualizer;
import io.rightpad.daxplorer.visualization.visualizers.DateSelectionVisualizer;
import io.rightpad.daxplorer.visualization.visualizers.IndexVisualizer;
import io.rightpad.daxplorer.visualization.visualizers.Visualizer;
import kotlin.jvm.functions.Function0;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class MainWindow
{
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

    private JFrame frame;
    private JMenuBar menuBar;
    private JMenuItem[] fileDependentMenuItems;
    private Border defaultTextFieldBorder;
    private DefaultListModel<Visualizer> visualizerListModel;
    private List<IndexDataPoint> indexData;
    private List<IndexDataPoint> selectedIndexData;
    private DateSelectionVisualizer selectionVisualizer;

    private FileIO fileIO;

    public MainWindow()
    {
        setIndexData(new ArrayList<>(0));

        initFileIO();
        initMenuBar();

        updateFileDependentControls();

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

        this.visualizationPanel.visualize();
    }

    private void setIndexData(List<IndexDataPoint> indexData)
    {
        this.indexData = indexData;
        this.visualizationPanel.setIndexData(indexData);

        adjustViewportSettings();
    }

    private void adjustViewportSettings()
    {
        float minValue = this.indexData.stream()
                .map(IndexDataPoint::getMin)
                .min(Float::compareTo)
                .orElse(0f);
        float maxValue = this.indexData.stream()
                .map(IndexDataPoint::getMax)
                .max(Float::compareTo)
                .orElse(0f);
        float range = maxValue - minValue;
        float padding = range * .1f;
        this.visualizationPanel.setChartHeight(range + 2 * padding);
        this.visualizationPanel.setPosition(new PointF(0, minValue - padding));

        LocalDateTime firstDataPoint = this.indexData.stream()
                .map(TimeSeriesDataPoint::getTimestamp)
                .min(LocalDateTime::compareTo)
                .orElseGet(LocalDateTime::now);
        this.visualizationPanel.setTimeOffset(firstDataPoint);
    }

    private void initFileIO()
    {
        Function0<String> chooseFile = () -> {
            JFileChooser fileChooser = new JFileChooser();
            int response = fileChooser.showOpenDialog(this.mainPanel);
            switch(response) {
                case JFileChooser.CANCEL_OPTION:
                    return null;
                case JFileChooser.APPROVE_OPTION:
                    return fileChooser.getSelectedFile().getAbsolutePath();
            }
            return null;
        };

        Function0<Boolean> shouldSaveUnsavedChanges = () -> {
            int response = JOptionPane.showConfirmDialog(
                    this.mainPanel,
                    "There are unsaved changes! Save before quitting?",
                    "Unsaved changes",
                    JOptionPane.YES_NO_CANCEL_OPTION
            );
            switch(response) {
                case JOptionPane.YES_OPTION:
                    return true;
                case JOptionPane.NO_OPTION:
                    return false;
            }
            return null;
        };

        this.fileIO = new FileIO(chooseFile, shouldSaveUnsavedChanges, CsvKt.getCsvCollector());
    }

    private void loadData()
    {
        if(this.fileIO.getOpenFile() == null)
            return;

        setIndexData(UtilsKt.useLinesAsStream(this.fileIO.getOpenFile(),
                lines -> lines
                        .map(CsvKt::fromCSVToIndexDataPoint)
                        .collect(Collectors.toList())
        ));

        updateFileDependentStuff();
    }

    private void updateFileDependentStuff()
    {
        updateMenuItems();
        clearControlsState();
        updateFileDependentControls();
    }

    private void updateFileDependentControls()
    {
        boolean fileIsLoaded = this.fileIO.getOpenFile() != null;
        if(!fileIsLoaded)
            setTrendButtonsEnabled(false);
        for(JComponent control : new JComponent[]{
                this.viewportSpanXTextField,
                this.viewportSpanYTextField,
                this.viewportOffsetXTextField,
                this.viewportOffsetYTextField,
                this.selectionStartTextField,
                this.selectionEndTextField
        }) {
            control.setEnabled(fileIsLoaded);
        }
    }

    private void clearControlsState()
    {
        this.setSelectionStart(null);
        this.setSelectionEnd(null);
        deselectAllTrendButtons();
    }

    private void updateMenuItems()
    {
        for(JMenuItem fileDependentMenuItem : this.fileDependentMenuItems) {
            fileDependentMenuItem.setEnabled(this.fileIO.getOpenFile() != null);
        }
    }

    private void initMenuBar()
    {
        this.menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        this.menuBar.add(fileMenu);

        JMenuItem openFileMenuItem = new JMenuItem("Open...");
        openFileMenuItem.addActionListener(e -> {
            if(this.fileIO.open(this.indexData))
                loadData();
        });
        fileMenu.add(openFileMenuItem);

        fileMenu.addSeparator();

        JMenuItem saveFileMenuItem = new JMenuItem("Save");
        saveFileMenuItem.addActionListener(e -> this.fileIO.save(this.indexData));
        fileMenu.add(saveFileMenuItem);
        saveFileMenuItem.setEnabled(false);

        JMenuItem saveFileAsMenuItem = new JMenuItem("Save as...");
        saveFileAsMenuItem.addActionListener(e -> this.fileIO.saveAs(this.indexData));
        fileMenu.add(saveFileAsMenuItem);
        saveFileAsMenuItem.setEnabled(false);

        fileMenu.addSeparator();

        JMenuItem closeMenuItem = new JMenuItem("Close");
        closeMenuItem.addActionListener(e -> {
            if(this.fileIO.canClose(this.indexData))
                this.frame.dispose();
        });
        fileMenu.add(closeMenuItem);

        this.fileDependentMenuItems = new JMenuItem[]{
                saveFileMenuItem,
                saveFileAsMenuItem
        };
    }

    private void initVisualizerList()
    {
        this.visualizerListModel = new DefaultListModel<>();
        this.visualizerList.setModel(this.visualizerListModel);

        addVisualizer(new IndexVisualizer());
        this.selectionVisualizer = new DateSelectionVisualizer();
        addVisualizer(this.selectionVisualizer);
        addVisualizer(new AverageVisualizer(50, Color.blue));
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
        this.viewportOffsetXTextField.setText(dateOffset
                .plusDays(1) // needed, so that it displays the start of the next day, not the end of the previous
                .format(ConstKt.getDATE_FORMAT())
        );

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
                    LocalDateTime offset = LocalDate.parse(textField.getText(), ConstKt.getDATE_FORMAT())
                            .atTime(0, 0)
                            .minusDays(1); // needed, so that it displays the start of the next day, not the end of the previous
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
        viewportOffsetChangeMouseListener.setOnDrag(this::updateViewportTextFields);
        this.visualizationPanel.addMouseMotionListener(viewportOffsetChangeMouseListener);
        this.visualizationPanel.addMouseListener(viewportOffsetChangeMouseListener);
    }

    private void initMouseSpanYListeners()
    {
        ViewportSpanYChangeMouseListener viewportSpanYChangeMouseListener = new ViewportSpanYChangeMouseListener(this.visualizationPanel);
        viewportSpanYChangeMouseListener.setOnScroll(this::updateViewportTextFields);
        this.visualizationPanel.addMouseWheelListener(viewportSpanYChangeMouseListener);
    }

    public void show()
    {
        this.frame = new JFrame("daxplorer");
        initWindowClosingInterceptor();
        this.frame.setPreferredSize(new Dimension(1000, 600));
        this.frame.setContentPane(this.mainPanel);
        this.frame.setJMenuBar(this.menuBar);
        this.frame.pack();
        this.frame.setVisible(true);
    }

    private void initWindowClosingInterceptor()
    {
        this.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.frame.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                if(fileIO.canClose(indexData))
                    frame.dispose();
            }
        });
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
            LocalDateTime date = LocalDate.parse(input, ConstKt.getDATE_FORMAT()).atTime(0, 0).minusDays(2);
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
        updateSelectionList();
        updateSelectionTextFields();
        updateTrendButtons();
        this.visualizationPanel.visualize();
    }

    private void updateSelectionTextFields()
    {
        if(this.selectedIndexData == null)
            return;

        updateSelectionTextField(this.selectionStartTextField, this.selectedIndexData.get(0).getTimestamp());
        updateSelectionTextField(this.selectionEndTextField, this.selectedIndexData.get(this.selectedIndexData.size() - 1).getTimestamp().plusDays(1));
    }

    private void updateSelectionTextField(JTextField textField, LocalDateTime selection)
    {
        if(selection == null)
            return;

        String text = textField.getText();
        String selectionText = selection.format(ConstKt.getDATE_FORMAT());

        if(!text.equals(selectionText)) {
            textField.setText(selectionText);
        }
    }

    private void updateSelectionList()
    {
        this.selectedIndexData = null;
        if(getSelectionStart() == null || getSelectionEnd() == null)
            return;

        LocalDateTime selectionStart = getSelectionStart(),
                selectionEnd = getSelectionEnd();

        this.selectedIndexData = new LinkedList<>();
        for(IndexDataPoint dataPoint : this.indexData) {
            if(dataPoint.getTimestamp().isBefore(selectionStart)) {
                continue;
            }

            this.selectedIndexData.add(dataPoint);

            if(dataPoint.getTimestamp().isAfter(selectionEnd))
                break;
        }
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
            this.fileIO.markAsChanged();
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
