package main.controllers;


import mo.capture.CaptureProvider;
import mo.communication.streaming.capture.PluginCaptureListener;
import mo.core.I18n;
import mo.core.plugin.Extends;
import mo.core.plugin.Extension;
import mo.organization.Configuration;
import mo.organization.ProjectOrganization;
import mo.organization.StagePlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
        return null;
    }

    @Override
    public List<Configuration> getConfigurations() {
        return this.configurations;
    }

    @Override
    public StagePlugin fromFile(File file) {
        return null;
    }

    @Override
    public File toFile(File file) {
        return null;
    }

}
