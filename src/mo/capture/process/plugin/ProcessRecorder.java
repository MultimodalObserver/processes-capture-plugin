package mo.capture.process.plugin;

import mo.capture.process.plugin.model.Format;
import mo.capture.process.plugin.model.OutputFile;
import mo.capture.process.plugin.model.Separator;
import mo.communication.streaming.capture.CaptureConfig;
import mo.capture.process.plugin.model.CaptureThread;
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
    private static final String INIT_JSON_ARRAY = "[";
    private static final String END_JSON_ARRAY = "]";
    private List<OutputFile> outputFiles;
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
        this.outputFiles = this.createOutputFiles(stageFolder);
        int sleepTime = captureConfigurationController.getTemporalConfig().getSnapshotCaptureTime();
        this.captureThread = new CaptureThread(CaptureThread.RUNNING_STATUS, this.outputFiles, sleepTime);
    }

    private List<OutputFile> createOutputFiles(File parent) {
        List<OutputFile> outputFiles = new ArrayList<>();
        String reportDate = DateHelper.now();
        File jsonFile = new File(parent, reportDate + "_" + this.captureConfigurationController.getId() +
                "." + Format.JSON.getValue());
        FileOutputStream jsonOutputStream;
        try {
            jsonOutputStream = new FileOutputStream(jsonFile);
            jsonOutputStream.write(INIT_JSON_ARRAY.getBytes());
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Unable to create JSON File", e);
            return outputFiles;
        }
        this.fileDescription = new FileDescription(jsonFile, ProcessRecorder.class.getName());
        OutputFile jsonOutputFile = new OutputFile();
        jsonOutputFile.setFormat(Format.JSON.getValue());
        jsonOutputFile.setFile(jsonFile);
        jsonOutputFile.setOutputStream(jsonOutputStream);
        outputFiles.add(jsonOutputFile);
        if(this.captureConfigurationController.getTemporalConfig().isExportToCsv()){
            File csvFile = new File(parent, reportDate + "_" + this.captureConfigurationController.getId()
                    + "." + Format.CSV.getValue());
            FileOutputStream csvOutputStream;
            try{
                csvOutputStream = new FileOutputStream(csvFile);
                String headers = CaptureThread.CSV_HEADERS + System.getProperty("line.separator");
                csvOutputStream.write(headers.getBytes());
            }
            catch(IOException e){
                LOGGER.log(Level.SEVERE, "Unable to export to CSV", e);
                return outputFiles;
            }
            OutputFile csvOutputFile = new OutputFile();
            csvOutputFile.setFormat(Format.CSV.getValue());
            csvOutputFile.setFile(csvFile);
            csvOutputFile.setOutputStream(csvOutputStream);
            outputFiles.add(csvOutputFile);
        }
        return outputFiles;
    }

    private void deleteOutputFiles(List<OutputFile> outputFiles){
        if(outputFiles == null || outputFiles.isEmpty()){
            return;
        }
        for(OutputFile outputFile : outputFiles){
            outputFile.getFile().delete();
        }
        this.fileDescription.deleteFileDescription();
    }


    public void start(){
        this.captureThread.start();
    }

    public void stop(){
        this.captureThread.setStatus(CaptureThread.STOPPED_STATUS);
        for(OutputFile outputFile : outputFiles){
            boolean isCsvFile = outputFile.getFormat().equals(Format.CSV.getValue());
            try {
                outputFile.getOutputStream().flush();
                long channelSize = outputFile.getOutputStream().getChannel().position();
                String lastChar = isCsvFile ? Separator.CSV_ROW.getValue() : Separator.JSON.getValue();
                long sizeWithoutLastChar = channelSize - lastChar.getBytes().length;
                outputFile.getOutputStream().getChannel().truncate(sizeWithoutLastChar);
                if(!isCsvFile){
                    outputFile.getOutputStream().write(END_JSON_ARRAY.getBytes());
                }
                outputFile.getOutputStream().flush();
                outputFile.getOutputStream().close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, null, e);
            }
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
        this.deleteOutputFiles(this.outputFiles);
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
}
