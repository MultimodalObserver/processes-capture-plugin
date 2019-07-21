package mo.capture.process.plugin.models;

import java.io.Serializable;
import java.util.List;

public class Snapshot implements Serializable {

    private List<Process> processes;
    private Long captureMilliseconds;

    public List<Process> getProcesses() {
        return processes;
    }

    public void setProcesses(List<Process> processes) {
        this.processes = processes;
    }

    public Long getCaptureMilliseconds() {
        return captureMilliseconds;
    }

    public void setCaptureMilliseconds(Long captureMilliseconds) {
        this.captureMilliseconds = captureMilliseconds;
    }
}
