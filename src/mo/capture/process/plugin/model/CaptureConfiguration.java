package mo.capture.process.plugin.model;

import javax.xml.bind.annotation.XmlRootElement;

/* Clase POJO para encapsular los datos de configuración del plugin ingresados por el usuario*/
@XmlRootElement
public class CaptureConfiguration {

    private String name;
    private int snapshotCaptureTime;
    private boolean exportToCsv;

    public CaptureConfiguration(){

    }

    public CaptureConfiguration(String name, int snapshotCaptureTime, boolean exportToCsv) {
        this.name = name;
        this.snapshotCaptureTime = snapshotCaptureTime;
        this.exportToCsv = exportToCsv;
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

    public boolean isExportToCsv() {
        return exportToCsv;
    }

    public void setExportToCsv(boolean exportToCsv) {
        this.exportToCsv = exportToCsv;
    }
}
