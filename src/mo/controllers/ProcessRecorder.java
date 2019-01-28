package mo.controllers;

import mo.models.CaptureThread;
import mo.utilities.DateHelper;
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
    private static final String OUTPUT_FILE_EXTENSION = ".json";
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
        this.captureThread = new CaptureThread(CaptureThread.RUNNING_STATUS, this.fileOutputStream, this.captureConfigurationController.getTemporalConfig());
    }

    private void createOutputFile(File parent) {
        String reportDate = DateHelper.now();
        this.outputFile = new File(parent, reportDate + "_" + this.captureConfigurationController.getId() + OUTPUT_FILE_EXTENSION);
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
    }

    /* Para el pause y resume, hay que registrar el tiempo que se pauso y el que se resumió */
    public void pause(){
        this.captureThread.setStatus(CaptureThread.PAUSED_STATUS);
    }


    public void resume(){
        this.captureThread.setStatus(CaptureThread.RUNNING_STATUS);
    }

    public void cancel(){
        this.stop();
        this.deleteOutputFile();
    }

    public void subscribeListener(PluginCaptureListener pluginCaptureListener){
        this.dataListeners.add(pluginCaptureListener);
    }

    public void unsubscribeListener(PluginCaptureListener pluginCaptureListener){
        if(this.dataListeners.isEmpty() || !this.dataListeners.contains(pluginCaptureListener)){
            return;
        }
        this.dataListeners.remove(pluginCaptureListener);
    }

}
