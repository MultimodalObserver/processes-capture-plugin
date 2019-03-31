package mo.capture.process.plugin.models;

/* Clase POJO para encapsular los datos de configuración del plugin ingresados por el usuario*/
public class CaptureConfiguration {
    private String name;

    public CaptureConfiguration(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
