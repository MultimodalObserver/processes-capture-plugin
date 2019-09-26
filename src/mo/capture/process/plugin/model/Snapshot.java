package mo.capture.process.plugin.model;

import java.io.Serializable;
import java.util.List;

public class Snapshot implements Serializable {

    private List<Process> processes;
    private Long captureTimestamp;

    public List<Process> getProcesses() {
        return processes;
    }

    public void setProcesses(List<Process> processes) {
        this.processes = processes;
    }

    public Long getCaptureTimestamp() {
        return captureTimestamp;
    }

    public void setCaptureTimestamp(Long captureTimestamp) {
        this.captureTimestamp = captureTimestamp;
    }

    public String toCSV(String columnSeparator, String rowSeparator){
        StringBuilder result = new StringBuilder();
        if(columnSeparator == null || columnSeparator.isEmpty() || rowSeparator == null
                || rowSeparator.isEmpty() || this.processes == null || this.processes.isEmpty()
                || this.captureTimestamp == null || this.captureTimestamp <= 0){
            return result.toString();
        }
        for(Process process : this.processes){
            result.append(process.toCSV(columnSeparator, this.captureTimestamp)).append(rowSeparator);
        }
        return result.toString();
    }
}
