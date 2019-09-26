package mo.capture.process.plugin.model;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;

public class Process implements Serializable {

    private long pid;
    private String userName;
    private String startInstant;
    private long totalCpuDuration;
    private String command;
    private long parentPid;
    private int hasChildren;
    private int supportsNormalTermination;

    public Process() {

    }

    public Process(ProcessHandle process){
        long pid = process.pid(); // pid del proceso
        String userName = process.info().user().orElse(CaptureThread.OTHER_STRING); //nombre de usuario que abrió el proceso
        String startInstant = process.info().startInstant().orElse(Instant.MIN).toString(); /* Instante de tiempo
            en el que se abrió el proceso */
        long totalCpuDuration = process.info().totalCpuDuration().orElse(Duration.ZERO).toMillis(); /* Duración en milisegundos
            del proceso */
        String command = process.info().command().orElse(CaptureThread.OTHER_STRING); //comando que levanta el proceso
        long parentPid = process.parent().isPresent() ? process.parent().get().pid() : CaptureThread.OTHER_LONG; /*
            pid del padre si es que tiene */
        int hasChildren = process.children().count() != 0 ? 1: 0;
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
        int supportsNormalTermination = process.supportsNormalTermination() ? 1 : 0; /* indica si el proceso puede destruirse
            usando el método destroy() de la API de procesos de Java (interfaz ProcessHandle)

            En caso de no poder usar destroy(), se debe destruir a la fuerza con destroyForcibly()
            de la misma API.
            */
        this.pid = pid;
        this.userName = userName;
        this.startInstant = startInstant;
        this.totalCpuDuration = totalCpuDuration;
        this.command = command;
        this.parentPid = parentPid;
        this.hasChildren = hasChildren;
        this.supportsNormalTermination = supportsNormalTermination;
    }

    public long getPid() {
        return pid;
    }

    public void setPid(long pid) {
        this.pid = pid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getStartInstant() {
        return startInstant;
    }

    public void setStartInstant(String startInstant) {
        this.startInstant = startInstant;
    }

    public long getTotalCpuDuration() {
        return totalCpuDuration;
    }

    public void setTotalCpuDuration(long totalCpuDuration) {
        this.totalCpuDuration = totalCpuDuration;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public long getParentPid() {
        return parentPid;
    }

    public void setParentPid(long parentPid) {
        this.parentPid = parentPid;
    }

    public int getHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(int hasChildren) {
        this.hasChildren = hasChildren;
    }

    public int getSupportsNormalTermination() {
        return supportsNormalTermination;
    }

    public void setSupportsNormalTermination(int supportsNormalTermination) {
        this.supportsNormalTermination = supportsNormalTermination;
    }


    public String toCSV(String columnSeparator, long captureTime){
        return  this.pid + columnSeparator + captureTime + columnSeparator +
                this.userName + columnSeparator + this.startInstant + columnSeparator +
                this.totalCpuDuration + columnSeparator + this.command + columnSeparator +
                this.supportsNormalTermination + columnSeparator + this.parentPid + columnSeparator +
                this.hasChildren;
    }

}
