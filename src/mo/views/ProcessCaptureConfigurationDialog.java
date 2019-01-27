package mo.views;

import mo.controllers.ProcessRecorder;
import mo.core.ui.Utils;
import mo.models.CaptureConfiguration;
import mo.models.CustomComboBoxItem;
import mo.core.I18n;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

public class ProcessCaptureConfigurationDialog extends JDialog implements DocumentListener {

    private CaptureConfiguration temporalConfig;
    private boolean accepted;
    private JLabel configurationNameLabel;
    private JTextField configurationNameTextField;
    private JLabel configurationNameErrorLabel;
    private JLabel filterLabel;
    private JComboBox filtersComboBox;
    private JLabel filterErrorLabel;
    private JLabel snapshotRepeatTimeLabel;
    private JTextField snapshotRepeatTimeTextField;
    private JLabel snapshotRepeatTimeErrorLabel;
    private JButton saveConfigButton;

    private I18n i18n;

    public ProcessCaptureConfigurationDialog(){
        super(null,"", Dialog.ModalityType.APPLICATION_MODAL);
        this.temporalConfig = null;
        this.accepted = false;
        this.i18n = new I18n(ProcessCaptureConfigurationDialog.class);
        this.setTitle(this.i18n.s("configurationFrameTitleText"));
        this.initFiltersComboBox();
        this.configurationNameLabel = new JLabel(this.i18n.s("configurationNameLabelText"));
        this.configurationNameTextField = new JTextField();
        this.configurationNameErrorLabel = new JLabel(this.i18n.s("configurationNameErrorLabelText"));
        this.filterLabel = new JLabel(this.i18n.s("selectFilterLabelText"));
        this.filterErrorLabel = new JLabel(this.i18n.s("filterErrorLabelText"));
        this.snapshotRepeatTimeLabel = new JLabel(this.i18n.s("snapshotRepeatTimeLabelText"));
        this.snapshotRepeatTimeTextField = new JTextField();
        this.snapshotRepeatTimeErrorLabel = new JLabel(this.i18n.s("snapshotRepeatTimeErrorLabelText"));
        this.saveConfigButton = new JButton(this.i18n.s("saveConfigButtonText"));
        this.centerComponents();
        this.addComponents();
        this.watchFormErrors();
        this.addActionListeners();
    }

    private void initFiltersComboBox(){
        Vector model = new Vector();
        model.addElement(new CustomComboBoxItem(ProcessRecorder.ALL_PROCESSES, this.i18n.s("allProcessesFilterComboBoxItemText")));
        model.addElement(new CustomComboBoxItem(ProcessRecorder.ONLY_RUNNING_PROCESSES, this.i18n.s("onlyRunningProcessesFilterComboBoxItemText")));
        model.addElement(new CustomComboBoxItem(ProcessRecorder.ONLY_NOT_RUNNING_PROCESSES, this.i18n.s("onlyNotRunningProcessesFilterComboBoxItemText")));
        this.filtersComboBox = new JComboBox(model);
        this.filtersComboBox.setSelectedIndex(0);
    }

    private void centerComponents(){
        this.configurationNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.configurationNameTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.configurationNameErrorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.filterLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.filtersComboBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.filterErrorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.snapshotRepeatTimeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.snapshotRepeatTimeTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.snapshotRepeatTimeErrorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.saveConfigButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private void addComponents(){
        Container contentPane = this.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.add(this.configurationNameLabel);
        contentPane.add(this.configurationNameTextField);
        contentPane.add(this.configurationNameErrorLabel);
        contentPane.add(this.filterLabel);
        contentPane.add(this.filtersComboBox);
        contentPane.add(this.filterErrorLabel);
        contentPane.add(this.snapshotRepeatTimeLabel);
        contentPane.add(this.snapshotRepeatTimeTextField);
        contentPane.add(this.snapshotRepeatTimeErrorLabel);
        contentPane.add(this.saveConfigButton);
    }

    public void showDialog(){
        setMinimumSize(new Dimension(400, 150));
        setPreferredSize(new Dimension(400, 300));
        pack();
        Utils.centerOnScreen(this);
        this.setVisible(true);
    }

    /**
     * Gives notification that there was an insert into the document.  The
     * range given by the DocumentEvent bounds the freshly inserted region.
     *
     * @param e the document event
     */
    @Override
    public void insertUpdate(DocumentEvent e) {
        this.watchFormErrors();
    }

    /**
     * Gives notification that a portion of the document has been
     * removed.  The range is given in terms of what the view last
     * saw (that is, before updating sticky positions).
     *
     * @param e the document event
     */
    @Override
    public void removeUpdate(DocumentEvent e) {
        this.watchFormErrors();
    }

    /**
     * Gives notification that an attribute or set of attributes changed.
     *
     * @param e the document event
     */
    @Override
    public void changedUpdate(DocumentEvent e) {
        this.watchFormErrors();
    }

    private void watchFormErrors(){
        CustomComboBoxItem selectedFilter = (CustomComboBoxItem) this.filtersComboBox.getSelectedItem();
        if(this.configurationNameTextField.getText().isEmpty()){
            this.configurationNameErrorLabel.setVisible(true);
        }else{
            this.configurationNameErrorLabel.setVisible(false);
        }
        if(selectedFilter.getValue().isEmpty()){
            this.filterErrorLabel.setVisible(true);
        }
        else{
            this.filterErrorLabel.setVisible(false);
        }
        if(this.snapshotRepeatTimeTextField.getText().isEmpty()){
            this.snapshotRepeatTimeErrorLabel.setVisible(true);
        }
        else{
            this.snapshotRepeatTimeErrorLabel.setVisible(false);
        }
    }

    private void addActionListeners(){
        this.saveConfigButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String configurationName = ProcessCaptureConfigurationDialog.this.configurationNameTextField.getText();
                CustomComboBoxItem selectedFilter = (CustomComboBoxItem) ProcessCaptureConfigurationDialog.this.filtersComboBox.getSelectedItem();
                String captureSnapshotRepeatTime = ProcessCaptureConfigurationDialog.this.snapshotRepeatTimeTextField.getText();
                ProcessCaptureConfigurationDialog.this.setVisible(false);
                ProcessCaptureConfigurationDialog.this.dispose();
                ProcessCaptureConfigurationDialog.this.temporalConfig = new CaptureConfiguration(configurationName, selectedFilter.getId(), Integer.parseInt(captureSnapshotRepeatTime));
                ProcessCaptureConfigurationDialog.this.accepted = true;
                ProcessCaptureConfigurationDialog.this.setVisible(false);
                ProcessCaptureConfigurationDialog.this.dispose();
            }
        });
    }

    public CaptureConfiguration getTemporalConfig() {
        return temporalConfig;
    }

    public boolean isAccepted() {
        return accepted;
    }
}
