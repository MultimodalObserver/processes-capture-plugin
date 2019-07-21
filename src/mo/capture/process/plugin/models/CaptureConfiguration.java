package mo.capture.process.plugin.models;

import javax.xml.bind.annotation.XmlRootElement;

/* Clase POJO para encapsular los datos de configuración del plugin ingresados por el usuario*/
@XmlRootElement
public class CaptureConfiguration {

    private String name;
    private int snapshotCaptureTime;
    private String outputFormat;

    public CaptureConfiguration(){

    }

    public CaptureConfiguration(String name, int snapshotCaptureTime, String outputFormat) {
        this.name = name;
        this.snapshotCaptureTime = snapshotCaptureTime;
        this.outputFormat = outputFormat;
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

    public String getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(String outputFormat) {
        this.outputFormat = outputFormat;
    }
}
