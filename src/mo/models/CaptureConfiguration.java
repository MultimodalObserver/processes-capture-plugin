package mo.models;

/* Clase POJO para encapsular los datos de configuración del plugin ingresados por el usuario*/
public class CaptureConfiguration {
    private String name;
    private int selectedFilterId;
    private int captureSnapshotRepeatTime;

    public CaptureConfiguration(String name, int selectedFilterId, int captureSnapshotRepeatTime) {
        this.name = name;
        this.selectedFilterId = selectedFilterId;
        this.captureSnapshotRepeatTime = captureSnapshotRepeatTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSelectedFilterId() {
        return selectedFilterId;
    }

    public void setSelectedFilterId(int selectedFilterId) {
        this.selectedFilterId = selectedFilterId;
    }

    public int getCaptureSnapshotRepeatTime() {
        return captureSnapshotRepeatTime;
    }

    public void setCaptureSnapshotRepeatTime(int captureSnapshotRepeatTime) {
        this.captureSnapshotRepeatTime = captureSnapshotRepeatTime;
    }
}
