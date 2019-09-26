package mo.capture.process.plugin.model;

public enum Format {
    CSV("csv"),
    JSON("json");

    private final String value;

    Format(String value){
        this.value = value;
    }

    public String getValue(){
        return this.value;
    }
}
