package main;

import com.google.gson.Gson;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

public class MainContainer {
    public static void main(String[] args){
        String outputPath = "output.json";
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
        String jsonProcessesArray = processesSnapshotToJsonFormat(ProcessHandle.allProcesses());
        try {
            bufferedWriter.write(jsonProcessesArray);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al escribir en el archivo de salida");
            return;
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

    private static String processesSnapshotToJsonFormat(Stream<ProcessHandle> processes){
        Gson gson = new Gson();
        String other = null;
        List<String> jsonStringList= new ArrayList<>();
        processes.forEach(process -> {
            String pid = String.valueOf(process.pid());
            String userName = process.info().user().orElse(other);
            String startInstant = process.info().startInstant().orElse(Instant.MIN).toString();
            String totalCpuDuration = process.info().totalCpuDuration().orElse(Duration.ZERO).toString();
            String command = process.info().command().orElse(other);
            HashMap<String, String> jsonMap = new HashMap<>();
            jsonMap.put("pid", pid);
            jsonMap.put("userName", userName);
            jsonMap.put("startInstant", startInstant);
            jsonMap.put("totalCpuDuration", totalCpuDuration);
            jsonMap.put("command", command);
            String jsonProcessString = gson.toJson(jsonMap);
            jsonStringList.add(jsonProcessString);
        });
        return gson.toJson(jsonStringList);
    }
}
