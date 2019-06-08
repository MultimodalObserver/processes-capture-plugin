package mo.capture.process.plugin.views;

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

    private I18n i18n;

    public ConfigurationDialog(){
        super(null,"", Dialog.ModalityType.APPLICATION_MODAL);
        this.temporalConfig = null;
        this.accepted = false;
        this.i18n = new I18n(ConfigurationDialog.class);
        this.setTitle(this.i18n.s("configurationFrameTitleText"));
        this.configurationNameLabel = new JLabel(this.i18n.s("configurationNameLabelText"));
        this.configurationNameTextField = new JTextField();
        this.configurationNameErrorLabel = new JLabel();
        this.configurationNameErrorLabel.setVisible(false);
        this.saveConfigButton = new JButton(this.i18n.s("saveConfigButtonText"));
        this.snapshotCaptureTimeLabel = new JLabel("snapshotCaptureTimeLabelText");
        this.snapshotCaptureTimeTextField = new JTextField();
        this.snapshotCaptureTimeErrorLabel = new JLabel();
        this.snapshotCaptureTimeErrorLabel.setVisible(false);
        this.centerComponents();
        this.addComponents();
        this.addActionListeners();
    }

    private void centerComponents(){
        this.configurationNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.configurationNameTextField.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.configurationNameErrorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.saveConfigButton.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private void addComponents(){
        Container contentPane = this.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
        contentPane.add(this.configurationNameLabel);
        contentPane.add(this.configurationNameTextField);
        contentPane.add(this.configurationNameErrorLabel);
        contentPane.add(this.snapshotCaptureTimeLabel);
        contentPane.add(this.snapshotCaptureTimeTextField);
        contentPane.add(this.snapshotCaptureTimeErrorLabel);
        contentPane.add(this.saveConfigButton);
    }

    public void showDialog(){
        setMinimumSize(new Dimension(400, 150));
        setPreferredSize(new Dimension(400, 300));
        pack();
        Utils.centerOnScreen(this);
        this.setVisible(true);
    }

    private void addActionListeners(){
        this.saveConfigButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ConfigurationDialog.this.configurationNameErrorLabel.setVisible(false);
                ConfigurationDialog.this.configurationNameErrorLabel.setText("");
                ConfigurationDialog.this.snapshotCaptureTimeErrorLabel.setVisible(false);
                ConfigurationDialog.this.snapshotCaptureTimeErrorLabel.setText("");
                String configurationName = ConfigurationDialog.this.configurationNameTextField.getText();
                String snapshotCaptureTime = ConfigurationDialog.this.snapshotCaptureTimeTextField.getText();
                if(configurationName.isEmpty()){
                    ConfigurationDialog.this.configurationNameErrorLabel.setText(ConfigurationDialog.this.i18n.s("emptyConfig"));
                    ConfigurationDialog.this.configurationNameErrorLabel.setVisible(true);
                    return;
                }
                else if(configurationName.contains("_")){
                    ConfigurationDialog.this.configurationNameErrorLabel.setText(ConfigurationDialog.this.i18n.s("invalidConfigName"));
                    ConfigurationDialog.this.configurationNameErrorLabel.setVisible(true);
                    return;
                }
                if(snapshotCaptureTime.isEmpty()){
                    ConfigurationDialog.this.snapshotCaptureTimeErrorLabel.setText(ConfigurationDialog.this.i18n.s("emptySnapshotCaptureTime"));
                    ConfigurationDialog.this.snapshotCaptureTimeErrorLabel.setVisible(true);
                    return;

                }
                else if(Integer.parseInt(snapshotCaptureTime) <= 1){
                    ConfigurationDialog.this.snapshotCaptureTimeErrorLabel.setText(ConfigurationDialog.this.i18n.s("invalidSnapshotCaptureTime"));
                    ConfigurationDialog.this.snapshotCaptureTimeErrorLabel.setVisible(true);
                    return;
                }
                ConfigurationDialog.this.setVisible(false);
                ConfigurationDialog.this.dispose();
                ConfigurationDialog.this.temporalConfig = new CaptureConfiguration(configurationName, Integer.parseInt(snapshotCaptureTime));
                ConfigurationDialog.this.accepted = true;
                ConfigurationDialog.this.setVisible(false);
                ConfigurationDialog.this.dispose();
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
