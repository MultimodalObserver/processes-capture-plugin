package mo.models;

import com.google.gson.Gson;
import mo.communication.streaming.capture.CaptureEvent;
import mo.communication.streaming.capture.PluginCaptureListener;
import mo.controllers.ProcessRecorder;
import mo.utilities.DateHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Stream;

public class CaptureThread extends Thread {

    private int status;
    public static final int RUNNING_STATUS = 1;
    public static final int PAUSED_STATUS = 2;
    public static final int STOPPED_STATUS = 3;
    public static final int RESUMED_STATUS = 4;
    private static final String CAPTURE_MILLISECONDS_KEY = "captureMilliseconds";
    private static final String PID_KEY = "pid";
    private static final String USER_NAME_KEY = "userName";
    private static final String START_INSTANT_KEY = "startInstant";
    private static final String TOTAL_CPU_DURATION_KEY = "totalCpuDuration";
    private static final String COMMAND_KEY = "command";
    private static final String SUPPORTS_NORMAL_TERMINATION_KEY = "supportsNormalTermination";
    private static final String PARENT_PID_KEY = "parentPid";
    private static final String HAS_CHILDREN_KEY = "hasChildren";
    private static final String PROCESSES_KEY = "processes";
    private long pauseTime;
    private long resumeTime;
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private ProcessRecorder recorder;

    public CaptureThread(int status, ProcessRecorder recorder) {
        this.status = status;
        this.pauseTime = 0;
        this.resumeTime = 0;
        this.recorder = recorder;
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
                String jsonProcessesMap = processesSnapshotToJsonFormat(processes, captureTime);
                try {
                    this.recorder.getFileOutputStream().write(jsonProcessesMap.getBytes());
                    if(this.recorder.getDataListeners() != null){
                        CaptureEvent captureEvent = new CaptureEvent(this.recorder.getCaptureConfigurationController().getTemporalConfig().getName(),
                                this.recorder.getClass().getName(), jsonProcessesMap);
                        for(PluginCaptureListener dataListener: this.recorder.getDataListeners()){
                            dataListener.onDataReceived(this,captureEvent);
                        }
                    }
                } catch (IOException e) {
                    ProcessRecorder.LOGGER.log(Level.SEVERE, null, e);
                    return;
                }
            }
            else if(this.status == PAUSED_STATUS){
                /* Si se ha pausado la captura, vamos durmiendo el Thread para que este "reaccione" de vez en cuando
                y no deje de ser considerado por le procesador al momento de la planificación de los procesos
                 */
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else if(this.status == STOPPED_STATUS) {
                try {
                    this.recorder.getFileOutputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                    ProcessRecorder.LOGGER.log(Level.SEVERE, null, e);
                }
                return;
            }
        }
    }

    private String processesSnapshotToJsonFormat(Stream<ProcessHandle> processes, long captureTime){
        Gson gson = new Gson();
        String otherString = "-";
        long otherLong = -1;
        HashMap<String,Object> processesJsonMap= new HashMap<>();
        List<HashMap<String, Object>> processesList = new ArrayList<>();
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
            HashMap<String, Object> processJsonMap = new HashMap<>();
            processJsonMap.put(PID_KEY, pid);
            processJsonMap.put(USER_NAME_KEY, userName);
            processJsonMap.put(START_INSTANT_KEY, startInstant);
            processJsonMap.put(TOTAL_CPU_DURATION_KEY, totalCpuDuration);
            processJsonMap.put(COMMAND_KEY, command);
            processJsonMap.put(PARENT_PID_KEY, parentPid);
            processJsonMap.put(HAS_CHILDREN_KEY, hasChildren);
            processJsonMap.put(SUPPORTS_NORMAL_TERMINATION_KEY, supportsNormalTermination);
            processesList.add(processJsonMap);
        });
        processesJsonMap.put(PROCESSES_KEY, processesList);
        processesJsonMap.put(CAPTURE_MILLISECONDS_KEY, captureTime);
        return gson.toJson(processesJsonMap) + LINE_SEPARATOR;
    }
}
