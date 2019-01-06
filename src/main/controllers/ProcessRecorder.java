package main.controllers;

import com.google.gson.Gson;
import main.utilities.DateHelper;
import mo.communication.streaming.capture.PluginCaptureListener;
import mo.organization.FileDescription;
import mo.organization.Participant;
import mo.organization.ProjectOrganization;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class ProcessRecorder {

    File stageFolder;
    ProjectOrganization projectOrganization;
    Participant participant;
    ProcessCaptureConfiguration captureConfigurationController;
    private File outputFile;
    private FileOutputStream fileOutputStream;
    private FileDescription fileDescription;
    private static final String OUTPUT_FILE_EXTENSION = ".json";
    private List<PluginCaptureListener> dataListeners;
    private int status;
    private static final int RUNNING_STATUS = 1;
    private static final int PAUSED_STATUS = 2;
    private static final int STOPPED_STATUS = 3;
    private static final Logger LOGGER = Logger.getLogger(ProcessRecorder.class.getName());
    private String pauseTime;

    public ProcessRecorder(File stageFolder, ProjectOrganization projectOrganization, Participant participant,
                           ProcessCaptureConfiguration captureConfigurationController){
        this.stageFolder = stageFolder;
        this.projectOrganization = projectOrganization;
        this.participant = participant;
        this.captureConfigurationController = captureConfigurationController;
        this.dataListeners = new ArrayList<>();
        this.createOutputFile(stageFolder);
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

    private synchronized void changeStatus(int newStatus){
        if(newStatus != PAUSED_STATUS){
            pauseTime = null;
        }
        else{
            pauseTime = DateHelper.now();
        }
        status = newStatus;
    }

    public void start(){
        changeStatus(RUNNING_STATUS);
        new Thread(new Capture()).start();
    }

    public void stop(){
        try {
            changeStatus(STOPPED_STATUS);
            this.fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
            LOGGER.log(Level.SEVERE, null, e);
        }
    }

    /* Para el pause y resume, hay que registrar el tiempo que se pauso y el que se resumió */
    public void pause(){
        changeStatus(PAUSED_STATUS);
    }


    public void resume(){
        changeStatus(RUNNING_STATUS);
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

    /* Clase que ejecuta su funcionalidad com ouna nueva hebra
    * Esto con el fin de realizar la captura en paralelo a la ejecución del programa*/
    private class Capture implements Runnable{

        @Override
        public void run() {
            while(status == RUNNING_STATUS){
                /* Solo los procesos que esten corriendo */
                Stream<ProcessHandle> aliveProcesses = ProcessHandle.allProcesses().filter(process -> {
                    return process.isAlive();
                });
                String captureTime = pauseTime == null ? DateHelper.now() : pauseTime;
                String jsonProcessesArray = processesSnapshotToJsonFormat(aliveProcesses, captureTime);
                try {
                    fileOutputStream.write(jsonProcessesArray.getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                    System.out.println("Error al escribir en el archivo de salida");
                    LOGGER.log(Level.SEVERE, null, e);
                    return;
                }
                System.out.println("Archivo de salida escrito correctamente");
            }
        }

        private String processesSnapshotToJsonFormat(Stream<ProcessHandle> processes, String captureTime){
            Gson gson = new Gson();
            String otherString = "-";
            long otherLong = -1;
            List<String> jsonStringList= new ArrayList<>();
            processes.forEach(process -> {
                long pid = process.pid(); // pid del proceso
                String userName = process.info().user().orElse(otherString); //nombre de usuario que abrió el proceso
                String startInstant = process.info().startInstant().orElse(Instant.MIN).toString(); /* Instante de tiempo
            en el que se abrió el proceso */
                long totalCpuDuration = process.info().totalCpuDuration().orElse(Duration.ZERO).toMillis(); /* Duración en milisegundos
            del proceso */
                String command = process.info().command().orElse(otherString); //comando que levanta el proceso
                long parentPid = process.parent().isPresent() ? process.parent().get().pid() : otherLong; /*
            pid del padre si es que tiene */
                boolean hasChildren = process.children().count() != 0;
            /* Una cosa es el  momento en que se tomó el snapshot de procesos y otra el momento cuando se esta
             consultando su estado, por lo que puede ser que un proceso ya no este vivo
            al momento de consultar su estado. Lo mismo pasa cuando se requiera rearmar el ProcessHandle
            usando el metodo of(pid proceso)!!!!
             */
            /*
            boolean isAlive = process.isAlive(); //estado del proceso, sujeto a lo anterior
             incluir timestamp de cuando se consultó el estado del proceso??
            útil para métricas
             */
                boolean supportsNormalTermination = process.supportsNormalTermination(); /* indica si el proceso puede destruirse
            usando el método destroy() de la API de procesos de Java (interfaz ProcessHandle)

            En caso de no poder usar destroy(), se debe destruir a la fuerza con destroyForcibly()
            de la misma API.
            */
                HashMap<String, Object> jsonMap = new HashMap<>();
                jsonMap.put("pid", pid);
                //jsonMap.put("isAlive", isAlive);
                jsonMap.put("userName", userName);
                jsonMap.put("startInstant", startInstant);
                jsonMap.put("totalCpuDuration", totalCpuDuration);
                jsonMap.put("command", command);
                jsonMap.put("parentPid", parentPid);
                jsonMap.put("hasChildren", hasChildren);
                jsonMap.put("supportsNormalTermination", supportsNormalTermination);
                jsonMap.put("captureTime", captureTime);
                String jsonProcessString = gson.toJson(jsonMap);
                jsonStringList.add(jsonProcessString);
            });
            return gson.toJson(jsonStringList);
        }
    }
}
