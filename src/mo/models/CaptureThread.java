package mo.models;

import com.google.gson.Gson;
import mo.controllers.ProcessRecorder;
import mo.utilities.DateHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Stream;

public class CaptureThread extends Thread {
    private int status;
    private FileOutputStream fileOutputStream;
    public static final int RUNNING_STATUS = 1;
    public static final int PAUSED_STATUS = 2;
    public static final int STOPPED_STATUS = 3;
    private String pauseTime;

    public CaptureThread(int status, FileOutputStream fileOutputStream) {
        this.status = status;
        this.pauseTime = null;
        this.fileOutputStream = fileOutputStream;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        if(status != PAUSED_STATUS){
            this.pauseTime = null;
        }
        else{
            this.pauseTime = DateHelper.now();
        }
        this.status = status;
    }

    @Override
    public void run(){
        while(true){
            //ProcessRecorder.LOGGER.log(Level.SEVERE, String.valueOf(this.status));
            if(this.status == RUNNING_STATUS){
                //ProcessRecorder.LOGGER.log(Level.SEVERE, "ESTOY CORRIENDO");
                /* Solo los procesos que esten corriendo */
                Stream<ProcessHandle> aliveProcesses = ProcessHandle.allProcesses().filter(process -> {
                    return process.isAlive();
                });
                String captureTime = this.pauseTime == null ? DateHelper.now() : this.pauseTime;
                String jsonProcessesArray = processesSnapshotToJsonFormat(aliveProcesses, captureTime);
                try {
                    this.fileOutputStream.write(jsonProcessesArray.getBytes());
                } catch (IOException e) {
                    ProcessRecorder.LOGGER.log(Level.SEVERE, null, e);
                    /* EN el contexto de ejecución de la captura dentro de MO, esto ocurre cuando se
                    termina la captura y al mismo tiempo se estaba escribiendo un resultado de captura en el archivo
                    Hay que analizar la situación. Se intenta implementar un esquema simple de sincronización
                     */
                    return;
                }
            }
            else if(this.status == STOPPED_STATUS){
                try {
                    this.fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    ProcessRecorder.LOGGER.log(Level.SEVERE, null, e);
                }
                return;
            }
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
