package mo.capture.process.plugin.model;

import com.google.gson.Gson;
import mo.capture.process.plugin.ProcessCaptureConfiguration;
import mo.capture.process.util.MessageSender;
import mo.capture.process.plugin.ProcessRecorder;
import mo.capture.process.util.DateHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Stream;

public class CaptureThread extends Thread {

    private volatile int status;
    public static final int RUNNING_STATUS = 1;
    public static final int PAUSED_STATUS = 2;
    public static final int STOPPED_STATUS = 3;
    public static final int RESUMED_STATUS = 4;
    static final String OTHER_STRING = "-";
    static final long OTHER_LONG = -1;
    private long pauseTime;
    private long resumeTime;
    private int sleepTime;
    private List<OutputFile> outputFiles;
    public static final String CSV_HEADERS = "pid,captureTime,userName,startInstant,totalCpuDuration,command,supportsNormalTermination,parentPid,hasChildren";
    private final Gson gson;

    public CaptureThread(int status, List<OutputFile> outputFiles,  int sleepTime) {
        this.status = status;
        this.pauseTime = 0;
        this.resumeTime = 0;
        this.sleepTime = sleepTime;
        this.gson = new Gson();
        this.outputFiles = outputFiles;
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
                Snapshot snapshot = this.processestoSnapshot(processes, captureTime);
                for(OutputFile outputFile : this.outputFiles){
                    String toWrite ="";
                    if(outputFile.getFormat().equals(Format.JSON.getValue())){
                        toWrite = gson.toJson(snapshot) + Separator.JSON.getValue();
                    }
                    else if(outputFile.getFormat().equals(Format.CSV.getValue())){
                        toWrite = snapshot.toCSV(Separator.CSV_COLUMN.getValue(), Separator.CSV_ROW.getValue());
                    }
                    try {
                        outputFile.getOutputStream().write(toWrite.getBytes());
                    } catch (IOException e) {
                        ProcessRecorder.LOGGER.log(Level.SEVERE, null, e);
                        return;
                    }
                }
                MessageSender.sendMessage(ProcessCaptureConfiguration.MESSAGE_CONTENT_KEY, gson.toJson(snapshot));
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

    private Snapshot processestoSnapshot(Stream<ProcessHandle> processes, long captureTime){
        List<Process> processesList = new ArrayList<>();
        processes.forEach(process -> {
            Process processModel = new Process(process);
            processesList.add(processModel);
        });
        Snapshot snapshot = new Snapshot();
        snapshot.setProcesses(processesList);
        snapshot.setCaptureTimestamp(captureTime);
        return snapshot;
    }
}
