package mo.capture.process.plugin.views;

import mo.core.ui.Utils;
import mo.capture.process.plugin.models.CaptureConfiguration;
import mo.core.I18n;

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
        this.configurationNameErrorLabel = new JLabel(this.i18n.s("configurationNameErrorLabelText"));
        this.configurationNameErrorLabel.setVisible(false);
        this.saveConfigButton = new JButton(this.i18n.s("saveConfigButtonText"));
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
                String configurationName = ConfigurationDialog.this.configurationNameTextField.getText();
                if(configurationName.isEmpty()){
                    ConfigurationDialog.this.configurationNameErrorLabel.setVisible(true);
                    return;
                }
                ConfigurationDialog.this.setVisible(false);
                ConfigurationDialog.this.dispose();
                ConfigurationDialog.this.temporalConfig = new CaptureConfiguration(configurationName);
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
