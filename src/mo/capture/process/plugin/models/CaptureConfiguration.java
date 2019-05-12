package mo.capture.process.plugin.models;

/* Clase POJO para encapsular los datos de configuración del plugin ingresados por el usuario*/
public class CaptureConfiguration {

    private String name;
    private int snapshotCaptureTime;

    public CaptureConfiguration(String name, int snapshotCaptureTime) {
        this.name = name;
        this.snapshotCaptureTime = snapshotCaptureTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSnapshotCaptureTime() {
        return snapshotCaptureTime;
    }

    public void setSnapshotCaptureTime(int snapshotCaptureTime) {
        this.snapshotCaptureTime = snapshotCaptureTime;
    }
}
