package mo.capture.process.plugin.models;

public class Process{

    private long pid;
    private String userName;
    private String startInstant;
    private long totalCpuDuration;
    private String command;
    private long parentPid;
    private int hasChildren;
    private int supportsNormalTermination;
    private long captureTime;
    public static final String DATA_SEPARATOR = ",";

    public Process(long pid, String userName, String startInstant, long totalCpuDuration, String command, long parentPid, int hasChildren, int supportsNormalTermination, long captureTime) {
        this.pid = pid;
        this.userName = userName;
        this.startInstant = startInstant;
        this.totalCpuDuration = totalCpuDuration;
        this.command = command;
        this.parentPid = parentPid;
        this.hasChildren = hasChildren;
        this.supportsNormalTermination = supportsNormalTermination;
        this.captureTime = captureTime;
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

    public long getCaptureTime() {
        return captureTime;
    }

    public void setCaptureTime(long captureTime) {
        this.captureTime = captureTime;
    }

    public String toCSV(){
        return  this.pid + DATA_SEPARATOR + this.captureTime + DATA_SEPARATOR +
                userName + DATA_SEPARATOR + startInstant + DATA_SEPARATOR +
                totalCpuDuration + DATA_SEPARATOR + command + DATA_SEPARATOR + parentPid + DATA_SEPARATOR +
                hasChildren;
    }

}
