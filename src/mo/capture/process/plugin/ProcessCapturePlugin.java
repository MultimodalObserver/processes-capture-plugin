package mo.capture.process.plugin;


import bibliothek.util.xml.XElement;
import bibliothek.util.xml.XIO;
import mo.capture.CaptureProvider;
import mo.communication.streaming.capture.PluginCaptureListener;
import mo.core.I18n;
import mo.core.plugin.Extends;
import mo.core.plugin.Extension;
import mo.organization.Configuration;
import mo.organization.ProjectOrganization;
import mo.organization.StagePlugin;
import mo.capture.process.plugin.view.ConfigurationDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Extension(
        xtends = {
                @Extends(extensionPointId = "mo.capture.CaptureProvider")
        }
)

public class ProcessCapturePlugin implements CaptureProvider {

    private static final Logger logger = Logger.getLogger(ProcessCapturePlugin.class.getName());
    private I18n i18n;
    List<Configuration> configurations;
    List<PluginCaptureListener> dataListeners;

    public ProcessCapturePlugin(){
        this.configurations = new ArrayList<>();
        this.i18n = new I18n(ProcessCapturePlugin.class);
        this.dataListeners = new ArrayList<>();
    }

    @Override
    public String getName() {
        return this.i18n.s("processCapturePluginDisplayedName");
    }


    @Override
    public Configuration initNewConfiguration(ProjectOrganization projectOrganization) {
        /* Aqui debemos mostrar la ventana de configuracion, obtener la configuracion
        ingresada por el usuario y agregarla a las configuraciones del plugin */
        ConfigurationDialog configDialog = new ConfigurationDialog();
        configDialog.showDialog();
        if(!configDialog.isAccepted()){
            return null;
        }
        ProcessCaptureConfiguration configuration = new ProcessCaptureConfiguration(configDialog.getTemporalConfig());
        this.configurations.add(configuration);
        return configuration;
    }

    @Override
    public List<Configuration> getConfigurations() {
        return this.configurations;
    }

    @Override
    public StagePlugin fromFile(File file) {
        if (file.isFile()) {
            try {
                ProcessCapturePlugin processCapturePlugin = new ProcessCapturePlugin();
                XElement root = XIO.readUTF(new FileInputStream(file));
                XElement[] pathsX = root.getElements("path");
                for (XElement pathX : pathsX) {
                    String path = pathX.getString();
                    File archive = new File(file.getParentFile(), path);
                    Configuration config = new ProcessCaptureConfiguration();
                    config = config.fromFile(archive);
                    processCapturePlugin.configurations.add(config);
                }
                return processCapturePlugin;
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }

    @Override
    public File toFile(File parent) {
        File file = new File(parent, "processes-capture-plugin.xml");
        if (!file.isFile()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
        XElement root = new XElement("capturers");
        for (Configuration config : configurations) {
            File p = new File(parent, "processes-capture-configurations");
            p.mkdirs();
            File f = config.toFile(p);
            XElement path = new XElement("path");
            Path parentPath = parent.toPath();
            Path configPath = f.toPath();
            path.setString(parentPath.relativize(configPath).toString());
            root.addElement(path);
        }
        try {
            XIO.writeUTF(root, new FileOutputStream(file));
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return file;
    }

}
