package main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public class MainContainer {
    public static void main(String[] args){
        String fileSeparator = System.getProperty("file.separator");
        String lineSeparator = System.getProperty("line.separator");
        String dataSeparator = ",";
        String other = "null";
        String outputPath = "../../output.txt";
        File file = new File(outputPath);
        FileWriter fileWriter = null;
        BufferedWriter bufferedWriter = null;
        try{
            fileWriter = new FileWriter(file);
            bufferedWriter = new BufferedWriter(fileWriter);
        }
        catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al crear el writer");
            return;
        }
        Object[] processes = ProcessHandle.allProcesses().toArray();
        for(Object process : processes){
            ProcessHandle aux = (ProcessHandle) process;
            String pid = String.valueOf(aux.pid());
            String userName = aux.info().user().orElse(other);
            String startInstant = aux.info().startInstant().orElse(Instant.MIN).toString();
            String totalCpuDuration = aux.info().totalCpuDuration().orElse(Duration.ZERO).toString();
            String command = aux.info().command().orElse(other);
            String processString = pid + dataSeparator + userName + dataSeparator +
                    startInstant + dataSeparator + totalCpuDuration + dataSeparator +
                    command + lineSeparator;
            try {
                bufferedWriter.write(processString);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error al escribir en el archivo de salida");
                return;
            }
        }
        try {
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cerrar el archivo creado");
        }
        System.out.println("Archivo de salida escrito correctamente");
    }

    /*
    private static String processDetails(ProcessHandle process) {
        return String.format("%8d %8s %10s %26s %-40s",
                process.pid(),
                text(process.info().user()),
                text(process.info().startInstant()),
                text(process.info().command()));
    }

    private static String text(Optional<?> optional) {
        return optional.map(Object::toString).orElse("-");
    }
    */

    public void storeProcessInfo(){

    }
}
