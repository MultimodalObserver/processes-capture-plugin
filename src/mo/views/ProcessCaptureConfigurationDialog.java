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

public class ProcessCaptureConfigurationDialog extends JDialog{

    private CaptureConfiguration temporalConfig;
    private boolean accepted;
    private JLabel configurationNameLabel;
    private JTextField configurationNameTextField;
    private JLabel configurationNameErrorLabel;
    private JButton saveConfigButton;

    private I18n i18n;

    public ProcessCaptureConfigurationDialog(){
        super(null,"", Dialog.ModalityType.APPLICATION_MODAL);
        this.temporalConfig = null;
        this.accepted = false;
        this.i18n = new I18n(ProcessCaptureConfigurationDialog.class);
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
                ProcessCaptureConfigurationDialog.this.configurationNameErrorLabel.setVisible(false);
                String configurationName = ProcessCaptureConfigurationDialog.this.configurationNameTextField.getText();
                if(configurationName.isEmpty()){
                    ProcessCaptureConfigurationDialog.this.configurationNameErrorLabel.setVisible(true);
                    return;
                }
                ProcessCaptureConfigurationDialog.this.setVisible(false);
                ProcessCaptureConfigurationDialog.this.dispose();
                ProcessCaptureConfigurationDialog.this.temporalConfig = new CaptureConfiguration(configurationName);
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
