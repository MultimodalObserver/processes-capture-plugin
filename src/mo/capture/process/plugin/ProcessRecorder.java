package mo.capture.process.plugin;

import mo.communication.streaming.capture.CaptureConfig;
import mo.capture.process.plugin.models.CaptureThread;
import mo.capture.process.util.DateHelper;
import mo.communication.streaming.capture.PluginCaptureListener;
import mo.organization.FileDescription;
import mo.organization.Participant;
import mo.organization.ProjectOrganization;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProcessRecorder {

    File stageFolder;
    ProjectOrganization projectOrganization;
    Participant participant;
    ProcessCaptureConfiguration captureConfigurationController;
    public static final int ALL_PROCESSES = 0;
    public static final int ONLY_RUNNING_PROCESSES = 1;
    public static final int ONLY_NOT_RUNNING_PROCESSES = 2;
    private File outputFile;
    private FileOutputStream fileOutputStream;
    private FileDescription fileDescription;
    private List<PluginCaptureListener> dataListeners;
    private CaptureThread captureThread;
    public static final Logger LOGGER = Logger.getLogger(ProcessRecorder.class.getName());

    public ProcessRecorder(File stageFolder, ProjectOrganization projectOrganization, Participant participant,
                           ProcessCaptureConfiguration captureConfigurationController){
        this.stageFolder = stageFolder;
        this.projectOrganization = projectOrganization;
        this.participant = participant;
        this.captureConfigurationController = captureConfigurationController;
        this.dataListeners = new ArrayList<>();
        this.createOutputFile(stageFolder);
        int sleepTime = captureConfigurationController.getTemporalConfig().getSnapshotCaptureTime();
        String selectedOutputFormat = captureConfigurationController.getTemporalConfig().getOutputFormat();
        this.captureThread = new CaptureThread(CaptureThread.RUNNING_STATUS, this.fileOutputStream,
                sleepTime, selectedOutputFormat);
    }

    private void createOutputFile(File parent) {
        String reportDate = DateHelper.now();
        String outputfileExtension = "." + this.getCaptureConfigurationController().getTemporalConfig().getOutputFormat();
        this.outputFile = new File(parent, reportDate + "_" + this.captureConfigurationController.getId() + outputfileExtension);
        try {
            this.outputFile.createNewFile();
            this.fileOutputStream = new FileOutputStream(outputFile);
            this.fileDescription = new FileDescription(outputFile, ProcessRecorder.class.getName());
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, null, e);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, null, e);
        }
    }

    private void deleteOutputFile(){
        if(!this.outputFile.isFile()){
            return;
        }
        this.outputFile.delete();
        if(!this.fileDescription.getDescriptionFile().isFile()){
            return;
        }
        this.fileDescription.deleteFileDescription();
    }


    public void start(){
        this.captureThread.start();
    }

    public void stop(){
        this.captureThread.setStatus(CaptureThread.STOPPED_STATUS);
        try {
            this.fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, null, e);
        }
    }

    /* Para el pause y resume, hay que registrar el tiempo que se pauso y el que se resumió */
    public void pause(){
        this.captureThread.setStatus(CaptureThread.PAUSED_STATUS);
    }


    public void resume(){
        this.captureThread.setStatus(CaptureThread.RESUMED_STATUS);
    }

    public void cancel(){
        this.stop();
        this.deleteOutputFile();
    }

    public void subscribeListener(PluginCaptureListener pluginCaptureListener){
        if(this.dataListeners.contains(pluginCaptureListener)){
            return;
        }
        this.dataListeners.add(pluginCaptureListener);
        CaptureConfig initialRemoteCaptureConfiguration = new CaptureConfig(ProcessRecorder.class.getName(),
                this.captureConfigurationController.getId(), null);
        pluginCaptureListener.setInitConfiguration(this, initialRemoteCaptureConfiguration);
    }

    public void unsubscribeListener(PluginCaptureListener pluginCaptureListener){
        if(this.dataListeners.isEmpty() || !this.dataListeners.contains(pluginCaptureListener)){
            return;
        }
        this.dataListeners.remove(pluginCaptureListener);
    }

    public FileOutputStream getFileOutputStream() {
        return fileOutputStream;
    }

    public List<PluginCaptureListener> getDataListeners() {
        return dataListeners;
    }

    public ProcessCaptureConfiguration getCaptureConfigurationController() {
        return captureConfigurationController;
    }
}
