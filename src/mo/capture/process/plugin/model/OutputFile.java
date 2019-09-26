package mo.capture.process.plugin.model;

import java.io.File;
import java.io.FileOutputStream;

public class OutputFile {

    private String format;
    private File file;
    private FileOutputStream outputStream;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public FileOutputStream getOutputStream() {
        return outputStream;
    }

    public void setOutputStream(FileOutputStream outputStream) {
        this.outputStream = outputStream;
    }
}
