package mo.capture.process.plugin.models;

import com.google.gson.Gson;
import mo.capture.process.plugin.ProcessCaptureConfiguration;
import mo.capture.process.util.MessageSender;
import mo.communication.streaming.capture.CaptureEvent;
import mo.communication.streaming.capture.PluginCaptureListener;
import mo.capture.process.plugin.ProcessRecorder;
import mo.capture.process.util.DateHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CaptureThread extends Thread {

    private int status;
    public static final int RUNNING_STATUS = 1;
    public static final int PAUSED_STATUS = 2;
    public static final int STOPPED_STATUS = 3;
    public static final int RESUMED_STATUS = 4;
    public static final String OTHER_STRING = "-";
    public static final long OTHER_LONG = -1;
    public static final String COMMA_SEPARATOR = ",";
    private long pauseTime;
    private long resumeTime;
    private int sleepTime;
    private FileOutputStream fileOutputStream;
    private String outputFormat;
    public static final String CSV_FORMAT = "csv";
    public static final String JSON_FORMAT = "json";
    public static final String CSV_HEADERS = "pid,captureTime,userName,startInstant,totalCpuDuration,command,supportsNormalTermination,parentPid,hasChildren";
    private final Gson gson;

    public CaptureThread(int status, FileOutputStream fileOutputStream,  int sleepTime, String outputFormat) {
        this.status = status;
        this.pauseTime = 0;
        this.resumeTime = 0;
        this.sleepTime = sleepTime;
        this.fileOutputStream = fileOutputStream;
        this.outputFormat = outputFormat;
        this.gson = new Gson();
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        if(status == PAUSED_STATUS){
            this.pauseTime = DateHelper.nowMilliseconds();
        } else if(status == RESUMED_STATUS){
            this.resumeTime = DateHelper.nowMilliseconds();
        }
        this.status = status;
    }

    @Override
    public void run(){
        while(true){
            if(this.status == RUNNING_STATUS || this.status == RESUMED_STATUS){
                /* Capturamos solo los procesos que están corriendo*/
                Stream<ProcessHandle> processes = ProcessHandle.allProcesses().filter(ProcessHandle::isAlive);
                long now = DateHelper.nowMilliseconds();
                long resumedCaptureTime = this.pauseTime + (now - this.resumeTime);
                long captureTime = this.resumeTime == 0 ? now : resumedCaptureTime;
                String processesData = this.outputFormat.equals(CSV_FORMAT) ? this.processesSnapshotToCSV(processes, captureTime) :
                        processesSnapshotToJsonFormat(processes, captureTime);
                try {
                    this.fileOutputStream.write((processesData + COMMA_SEPARATOR).getBytes());
                    MessageSender.sendMessage(ProcessCaptureConfiguration.MESSAGE_CONTENT_KEY, processesData);
                } catch (IOException e) {
                    ProcessRecorder.LOGGER.log(Level.SEVERE, null, e);
                    return;
                }
                try {
                    Thread.sleep(sleepTime*1000);
                } catch (InterruptedException e) {
                    ProcessRecorder.LOGGER.log(Level.SEVERE, null, e);
                    return;
                }
            }
            else if(this.status == PAUSED_STATUS){
                /* Si se ha pausado la captura, vamos durmiendo el Thread para que este "reaccione" de vez en cuando
                y no deje de ser considerado por le procesador al momento de la planificación de los procesos
                 */
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
            }
            else if(this.status == STOPPED_STATUS) {
                return;
            }
        }
    }

    private String processesSnapshotToJsonFormat(Stream<ProcessHandle> processes, long captureTime){
        List<Process> processesList = new ArrayList<>();
        processes.forEach(process -> {
            Process processModel = new Process(process);
            processesList.add(processModel);
        });
        Snapshot snapshot = new Snapshot();
        snapshot.setProcesses(processesList);
        snapshot.setCaptureMilliseconds(captureTime);
        return gson.toJson(snapshot);
    }

    private String processesSnapshotToCSV(Stream<ProcessHandle> processes, long captureTime){
        StringBuilder result = new StringBuilder();
        List <String> processesList = new ArrayList<>();
        processes.forEach(process -> {
            Process processModel = new Process(process);
            processesList.add(processModel.toCSV(captureTime));
        });
        for(String processString : processesList){
            result.append(processString + System.getProperty("line.separator"));
        }
        return result.toString();
    }
}
