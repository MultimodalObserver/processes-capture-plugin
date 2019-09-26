package mo.capture.process.plugin.model;

public enum Separator {

    CSV_COLUMN(","),
    CSV_ROW(System.getProperty("line.separator")),
    JSON(",");

    private final String value;

    Separator(String value){
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }
}
