package mo.capture.process.plugin.views;

import mo.capture.process.plugin.models.CaptureThread;
import mo.core.ui.Utils;
import mo.capture.process.plugin.models.CaptureConfiguration;
import mo.core.I18n;
import mo.organization.Configuration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ConfigurationDialog extends JDialog{

    private CaptureConfiguration temporalConfig;
    private boolean accepted;
    private JLabel configurationNameLabel;
    private JTextField configurationNameTextField;
    private JLabel configurationNameErrorLabel;
    private JLabel snapshotCaptureTimeLabel;
    private JTextField snapshotCaptureTimeTextField;
    private JLabel snapshotCaptureTimeErrorLabel;
    private JButton saveConfigButton;
    private JLabel formatLabel;
    private JComboBox<String> formatComboBox;
    private I18n i18n;

    public ConfigurationDialog(){
        super(null,"", Dialog.ModalityType.APPLICATION_MODAL);
        this.temporalConfig = null;
        this.accepted = false;
        this.i18n = new I18n(ConfigurationDialog.class);
        this.setTitle(this.i18n.s("configurationFrameTitleText"));
        this.initComponents();
        this.addComponents();
        this.addActionListeners();
    }

    private void initComponents(){
        this.configurationNameLabel = new JLabel(this.i18n.s("configurationNameLabelText"));
        this.configurationNameTextField = new JTextField();
        this.configurationNameErrorLabel = new JLabel();
        this.configurationNameErrorLabel.setVisible(false);
        this.configurationNameErrorLabel.setForeground(Color.RED);
        this.saveConfigButton = new JButton(this.i18n.s("saveConfigButtonText"));
        this.snapshotCaptureTimeLabel = new JLabel(this.i18n.s("snapshotCaptureTimeLabelText"));
        this.snapshotCaptureTimeLabel.setToolTipText(this.i18n.s("snapshotCaptureTooltip"));
        this.snapshotCaptureTimeTextField = new JTextField();
        this.snapshotCaptureTimeErrorLabel = new JLabel();
        this.snapshotCaptureTimeErrorLabel.setVisible(false);
        this.snapshotCaptureTimeErrorLabel.setForeground(Color.RED);
        this.formatLabel = new JLabel(this.i18n.s("formatLabelText"));
        this.formatComboBox = new JComboBox<>();
        this.formatComboBox.addItem(CaptureThread.JSON_FORMAT);
        this.formatComboBox.addItem(CaptureThread.CSV_FORMAT);
    }

    private void addComponents(){
        this.setLayout(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        Container contentPane = this.getContentPane();

        /* Config name label*/
        constraints.gridx = 0;
        constraints.gridy = 0;
        this.setConstraintsForLeftSide(constraints, true);
        constraints.insets = new Insets(10,10,5,5);
        contentPane.add(this.configurationNameLabel, constraints);

        /*Config name Text Field*/
        constraints = new GridBagConstraints();
        constraints.gridx=1;
        constraints.gridy=0;
        this.setConstraintsForRightSide(constraints, false);
        constraints.insets = new Insets(10,5,5,10);
        contentPane.add(this.configurationNameTextField, constraints);

        /* Config name error label */
        constraints = new GridBagConstraints();
        constraints.gridx=1;
        constraints.gridy=1;
        this.setConstraintsForRightSide(constraints, true);
        contentPane.add(this.configurationNameErrorLabel, constraints);

        /* snapshot capture Time Label*/
        constraints = new GridBagConstraints();
        constraints.gridx=0;
        constraints.gridy=2;
        this.setConstraintsForLeftSide(constraints, true);
        contentPane.add(this.snapshotCaptureTimeLabel, constraints);

        /* Snapshot capture Time Text field*/
        constraints = new GridBagConstraints();
        constraints.gridx=1;
        constraints.gridy=2;
        this.setConstraintsForRightSide(constraints, false);
        contentPane.add(this.snapshotCaptureTimeTextField, constraints);

        /* Snapshot capture Time Error Label*/
        constraints = new GridBagConstraints();
        constraints.gridx=1;
        constraints.gridy=3;
        this.setConstraintsForRightSide(constraints, true);
        contentPane.add(this.snapshotCaptureTimeErrorLabel, constraints);

        /* Format Label*/
        constraints = new GridBagConstraints();
        constraints.gridx=0;
        constraints.gridy=4;
        this.setConstraintsForLeftSide(constraints, false);
        contentPane.add(this.formatLabel, constraints);

        /* Format combo Box*/
        constraints = new GridBagConstraints();
        constraints.gridx = 1;
        constraints.gridy = 4;
        this.setConstraintsForRightSide(constraints, false);
        contentPane.add(this.formatComboBox, constraints);

        /* Save button*/
        constraints = new GridBagConstraints();
        this.setConstraintsForSaveButton(constraints);
        contentPane.add(this.saveConfigButton, constraints);
    }

    private void setConstraintsForLeftSide(GridBagConstraints constraints, boolean hasErrorLabel){
        constraints.gridwidth=1;
        constraints.gridheight= hasErrorLabel ? 2 : 1;
        constraints.weighty=1.0;
        constraints.insets= new Insets(5,10,5,5);
        constraints.anchor=GridBagConstraints.FIRST_LINE_START;
    }

    private void setConstraintsForRightSide(GridBagConstraints constraints, boolean errorLabel){
        constraints.gridheight=1;
        constraints.gridwidth=GridBagConstraints.REMAINDER;
        constraints.weightx=1.0;
        constraints.fill= GridBagConstraints.HORIZONTAL;
        int topInset = errorLabel ? 0 : 5;
        int bottomInset = errorLabel ? 5 : 0;
        constraints.insets= new Insets(topInset,5,bottomInset,10);
        constraints.anchor = GridBagConstraints.FIRST_LINE_START;
    }

    private void setConstraintsForSaveButton(GridBagConstraints constraints){
        constraints.gridx= 0;
        constraints.gridy=5;
        constraints.gridheight=1;
        constraints.gridwidth=2;
        constraints.weightx=0.0;
        constraints.weighty=0.0;
        constraints.fill=GridBagConstraints.HORIZONTAL;
        constraints.insets= new Insets(-10,10,10,10);
    }

    public void showDialog(){
        this.setMinimumSize(new Dimension(500, 300));
        this.setPreferredSize(new Dimension(500, 300));
        this.pack();
        this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        Utils.centerOnScreen(this);
        this.setVisible(true);
    }

    private void addActionListeners(){
        this.saveConfigButton.addActionListener(e -> {
            this.configurationNameErrorLabel.setVisible(false);
            this.configurationNameErrorLabel.setText("");
            this.snapshotCaptureTimeErrorLabel.setVisible(false);
            this.snapshotCaptureTimeErrorLabel.setText("");
            String configurationName = this.configurationNameTextField.getText();
            String snapshotCaptureTime = this.snapshotCaptureTimeTextField.getText();
            boolean invalidTime = snapshotCaptureTime.isEmpty() || !this.containsOnlyNumbers(snapshotCaptureTime)
                    || Integer.parseInt(snapshotCaptureTime) < 0;
            String selectedOutputFormat = (String) this.formatComboBox.getSelectedItem();
            if(configurationName.isEmpty() || invalidTime){
                if(configurationName.isEmpty()){
                    this.configurationNameErrorLabel.setText(this.i18n.s("emptyConfig"));
                    this.configurationNameErrorLabel.setVisible(true);
                }
                if(invalidTime){
                    this.snapshotCaptureTimeErrorLabel.setText(this.i18n.s("invalidTime"));
                    this.snapshotCaptureTimeErrorLabel.setVisible(true);

                }
                return;
            }
            this.temporalConfig = new CaptureConfiguration(configurationName, Integer.parseInt(snapshotCaptureTime), selectedOutputFormat);
            this.accepted = true;
            this.setVisible(false);
            this.dispose();
        });
    }

    public CaptureConfiguration getTemporalConfig() {
        return temporalConfig;
    }

    public boolean isAccepted() {
        return accepted;
    }

    private boolean containsOnlyNumbers(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i)))
                return false;
        }
        return true;
    }
}
