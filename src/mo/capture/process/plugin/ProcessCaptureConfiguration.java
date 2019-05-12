package mo.capture.process.plugin;

import mo.capture.RecordableConfiguration;
import mo.communication.streaming.capture.PluginCaptureListener;
import mo.communication.streaming.capture.PluginCaptureSender;
import mo.capture.process.plugin.models.CaptureConfiguration;
import mo.organization.Configuration;
import mo.organization.Participant;
import mo.organization.ProjectOrganization;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProcessCaptureConfiguration implements RecordableConfiguration, PluginCaptureSender {

    private CaptureConfiguration temporalConfig;
    private ProcessRecorder processRecorder;
    private static final Logger LOGGER = Logger.getLogger(ProcessCaptureConfiguration.class.getName());


    public ProcessCaptureConfiguration(CaptureConfiguration temporalConfig) {
        this.temporalConfig = temporalConfig;
    }

    /* Constructor que es utilizado para crear la configuración desde los archivos relacionados al plugin (que
    almacenan su info), luego de que
    MO ha sido cerrado.

    Esto es para que las configuraciones no se pierdan
     */
    public ProcessCaptureConfiguration(File file){
        String fileName = file.getName();
        String configData = fileName.substring(0, fileName.lastIndexOf("."));
        /* Aqui deberíamos leer del archivo xml, y no del nombre*/
        String[] configElements = configData.split("_");
        /* El elemento 0 es la palabra processes*/
        String configurationName = configElements[1];
        int snapshotCaptureTime = Integer.parseInt(configElements[2]);
        this.temporalConfig =  new CaptureConfiguration(configurationName, snapshotCaptureTime);
    }

    public CaptureConfiguration getTemporalConfig() {
        return temporalConfig;
    }

    @Override
    public void setupRecording(File file, ProjectOrganization projectOrganization, Participant participant) {
        this.processRecorder = new ProcessRecorder(file, projectOrganization, participant,this);
    }

    @Override
    public void startRecording() {
        processRecorder.start();
    }

    @Override
    public void cancelRecording() {
        processRecorder.cancel();
    }

    @Override
    public void pauseRecording() {
        processRecorder.pause();
    }

    @Override
    public void resumeRecording() {
        processRecorder.resume();
    }

    @Override
    public void stopRecording() {
        processRecorder.stop();
    }

    @Override
    public void subscribeListener(PluginCaptureListener pluginCaptureListener) {
        this.processRecorder.subscribeListener(pluginCaptureListener);
    }

    @Override
    public void unsubscribeListener(PluginCaptureListener pluginCaptureListener) {
        this.processRecorder.unsubscribeListener(pluginCaptureListener);
    }

    @Override
    public String getCreator() {
        return ProcessRecorder.class.getName();
    }

    @Override
    public void send25percent() {

    }

    @Override
    public void send50percent() {

    }

    @Override
    public void send75percent() {

    }

    @Override
    public void send100percent() {

    }

    @Override
    public String getId() {
        return this.temporalConfig.getName();
    }

    @Override
    public File toFile(File parent) {
        try {
            String childFileName = "processes_"+this.temporalConfig.getName()+
                    "_"+ this.temporalConfig.getSnapshotCaptureTime() +".xml";
            File f = new File(parent, childFileName);
            f.createNewFile();
            /* AQUI SE DEBERIA ESCRIBIR EL CONTENIDO EN XML,
            NO USAR EL NOMBRE DEL ARCHIVO PARA ALMACENAR VALORES DE CONFIGURACION
             */
            return f;
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    @Override
    public Configuration fromFile(File file) {
        String fileName = file.getName();
        if(!fileName.contains("_") || !fileName.contains(".")){
            return null;
        }
        String configData = fileName.substring(0, fileName.lastIndexOf("."));
        String[] configElements = configData.split("_");
        String configurationName = configElements[0];
        int snapshotCaptureTime = Integer.parseInt(configElements[1]);
        CaptureConfiguration auxConfig = new CaptureConfiguration(configurationName, snapshotCaptureTime);
        return new ProcessCaptureConfiguration(auxConfig);
    }

}
