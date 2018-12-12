package main;


import mo.capture.CaptureProvider;
import mo.core.plugin.Extends;
import mo.core.plugin.Extension;
import mo.organization.Configuration;
import mo.organization.ProjectOrganization;
import mo.organization.StagePlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import bibliothek.util.xml.XElement;

@Extension(
        xtends = {
                @Extends(extensionPointId = "mo.capture.CaptureProvider")
        }
)

public class ProcessCapturePlugin implements CaptureProvider {

    private static final Logger logger = Logger.getLogger(ProcessCapturePlugin.class.getName());
    List<Configuration> configurations;

    public ProcessCapturePlugin(){
        configurations = new ArrayList<>();
    }

    @Override
    public String getName() {
        return "Processes";
    }

    @Override
    public Configuration initNewConfiguration(ProjectOrganization projectOrganization) {
        /* Aqui debemos mostrar la ventana de configuracion, obtener la configuracion
        ingresada por el usuario y agregarla a las configuraciones del plugin */
        return null;
    }

    @Override
    public List<Configuration> getConfigurations() {
        return configurations;
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
