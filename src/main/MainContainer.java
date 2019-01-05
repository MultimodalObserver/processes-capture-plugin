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
        /* Solo los procesos que esten corriendo */
        Stream<ProcessHandle> aliveProcesses = ProcessHandle.allProcesses().filter(process -> {
            return process.isAlive();
        });
        String jsonProcessesArray = processesSnapshotToJsonFormat(aliveProcesses);
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
        String otherString = "-";
        long otherLong = -1;
        List<String> jsonStringList= new ArrayList<>();
        processes.forEach(process -> {
            long pid = process.pid(); // pid del proceso
            String userName = process.info().user().orElse(otherString); //nombre de usuario que abrió el proceso
            String startInstant = process.info().startInstant().orElse(Instant.MIN).toString(); /* Instante de tiempo
            en el que se abrió el proceso */
            long totalCpuDuration = process.info().totalCpuDuration().orElse(Duration.ZERO).toMillis(); /* Duración en milisegundos
            del proceso */
            String command = process.info().command().orElse(otherString); //comando que levanta el proceso
            long parentPid = process.parent().isPresent() ? process.parent().get().pid() : otherLong; /*
            pid del padre si es que tiene */
            boolean hasChildren = process.children().count() != 0;
            /* Una cosa es el  momento en que se tomó el snapshot de procesos y otra el momento cuando se esta
             consultando su estado, por lo que puede ser que un proceso ya no este vivo
            al momento de consultar su estado. Lo mismo pasa cuando se requiera rearmar el ProcessHandle
            usando el metodo of(pid proceso)!!!!
             */
            /*
            boolean isAlive = process.isAlive(); //estado del proceso, sujeto a lo anterior
             incluir timestamp de cuando se consultó el estado del proceso??
            útil para métricas
             */
            boolean supportsNormalTermination = process.supportsNormalTermination(); /* indica si el proceso puede destruirse
            usando el método destroy() de la API de procesos de Java (interfaz ProcessHandle)

            En caso de no poder usar destroy(), se debe destruir a la fuerza con destroyForcibly()
            de la misma API.
            */
            HashMap<String, Object> jsonMap = new HashMap<>();
            jsonMap.put("pid", pid);
            //jsonMap.put("isAlive", isAlive);
            jsonMap.put("userName", userName);
            jsonMap.put("startInstant", startInstant);
            jsonMap.put("totalCpuDuration", totalCpuDuration);
            jsonMap.put("command", command);
            jsonMap.put("parentPid", parentPid);
            jsonMap.put("hasChildren", hasChildren);
            jsonMap.put("supportsNormalTermination", supportsNormalTermination);
            String jsonProcessString = gson.toJson(jsonMap);
            jsonStringList.add(jsonProcessString);
        });
        return gson.toJson(jsonStringList);
    }
}
