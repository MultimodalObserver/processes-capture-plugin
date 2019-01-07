package mo.views;

import mo.models.CustomComboBoxItem;
import mo.core.I18n;

import javax.swing.*;
import java.util.Vector;

public class ProcessCaptureConfigurationDialog {

    private JFrame mainFrame;
    private JLabel filterLabel;
    private JComboBox filtersComboBox;
    private JLabel filterErrorLabel;
    private JLabel snapshotRepeatTimeLabel;
    private JTextField snapshotRepeatTimeEditText;
    private JLabel snapshotRepeatTimeErrorLabel;
    public static final int ALL_PROCESSES = 0;
    public static final int ONLY_RUNNING_PROCESSES = 1;
    public static final int ONLY_NOT_RUNNING_PROCESSES = 2;
    private I18n i18n;

    public ProcessCaptureConfigurationDialog(){
        this.i18n = new I18n(ProcessCaptureConfigurationDialog.class);
        this.initFiltersComboBox();
        this.mainFrame = new JFrame(this.i18n.s("configurationFrameTitleText"));
        this.filterLabel = new JLabel(this.i18n.s("selectFilterLabelText"));
    }

    private void initFiltersComboBox(){
        Vector model = new Vector();
        model.addElement(new CustomComboBoxItem(ALL_PROCESSES, this.i18n.s("allProcessesFilterComboBoxItemText")));
        model.addElement(new CustomComboBoxItem(ONLY_RUNNING_PROCESSES, this.i18n.s("onlyRunningProcessesFilterComboBoxItemText")));
        model.addElement(new CustomComboBoxItem(ONLY_NOT_RUNNING_PROCESSES, this.i18n.s("onlyNotRunningProcessesFilterComboBoxItemText")));
        this.filtersComboBox = new JComboBox(model);
        this.filtersComboBox.setSelectedIndex(0);
    }

}
