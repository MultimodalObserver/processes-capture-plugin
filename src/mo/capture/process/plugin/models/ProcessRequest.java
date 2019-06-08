package mo.capture.process.plugin.models;

import java.io.Serializable;

public class ProcessRequest implements Serializable {

    private static final long SERIAL_VERSION_UID = 1L;

    private String action;
    private long selectedProcessPID;

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public long getSelectedProcessPID() {
        return selectedProcessPID;
    }

    public void setSelectedProcessPID(long selectedProcessPID) {
        this.selectedProcessPID = selectedProcessPID;
    }
}
