package mo.capture.process.plugin.models;

import com.google.gson.Gson;
import mo.communication.streaming.capture.CaptureEvent;
import mo.communication.streaming.capture.PluginCaptureListener;
import mo.capture.process.plugin.ProcessRecorder;
import mo.capture.process.utilities.DateHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Stream;

public class CaptureThread extends Thread {

    private int status;
    public static final int RUNNING_STATUS = 1;
    public static final int PAUSED_STATUS = 2;
    public static final int STOPPED_STATUS = 3;
    public static final int RESUMED_STATUS = 4;
    private static final String CAPTURE_MILLISECONDS_KEY = "captureMilliseconds";
    private static final String PID_KEY = "pid";
    private static final String USER_NAME_KEY = "userName";
    private static final String START_INSTANT_KEY = "startInstant";
    private static final String TOTAL_CPU_DURATION_KEY = "totalCpuDuration";
    private static final String COMMAND_KEY = "command";
    private static final String SUPPORTS_NORMAL_TERMINATION_KEY = "supportsNormalTermination";
    private static final String PARENT_PID_KEY = "parentPid";
    private static final String HAS_CHILDREN_KEY = "hasChildren";
    private static final String PROCESSES_KEY = "processes";
    private long pauseTime;
    private long resumeTime;
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");
    private ProcessRecorder recorder;
    private String test = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus congue mauris sit amet diam maximus congue. Proin et mauris a velit auctor accumsan. Maecenas vitae rhoncus felis. Vivamus elementum posuere quam quis hendrerit. Phasellus luctus vulputate ipsum, a hendrerit nisl sollicitudin id. Cras ultricies ac lectus nec finibus. Aliquam at interdum nibh, a aliquam nulla. Vivamus a lacinia mi, id posuere purus.\n" +
            "\n" +
            "Suspendisse condimentum augue non porta aliquet. Nullam commodo consectetur elit at fermentum. Curabitur ac suscipit arcu. In hac habitasse platea dictumst. Aliquam ac tempus magna. Donec dictum euismod cursus. Morbi finibus, nulla id consectetur tempus, turpis est semper justo, vel interdum elit velit eget lorem. Cras faucibus urna tincidunt eros dictum, vel dignissim lectus tempor. Etiam rutrum diam ac aliquam pharetra. Suspendisse et massa in mauris gravida laoreet. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Pellentesque pulvinar pretium ultrices.\n" +
            "\n" +
            "Etiam tristique ut massa vel tincidunt. Integer tellus leo, elementum at odio vel, dapibus imperdiet leo. Praesent tempor quam vel iaculis ultrices. Duis augue elit, efficitur at consectetur vel, convallis sed dui. Curabitur luctus eros ipsum, et tincidunt felis fringilla ut. Aliquam nec leo mauris. Curabitur nec nisi nec dui porttitor tempor eu quis justo. Sed fermentum lorem nulla, quis molestie quam eleifend ut. Fusce diam augue, vulputate fringilla turpis pellentesque, venenatis pellentesque neque. Quisque egestas ipsum est, a ultricies mi tincidunt vehicula. Nunc tempor urna ex, in viverra odio ornare a. Nam id lacinia mauris, eget tincidunt nisl. Etiam mattis cursus dolor ut dictum. Maecenas a fermentum felis, a elementum elit.\n" +
            "\n" +
            "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Quisque mattis augue vel pellentesque imperdiet. Pellentesque ultricies tristique ante at vehicula. Integer velit libero, fermentum ac augue eget, volutpat venenatis metus. Integer sit amet nulla nulla. Ut interdum ipsum in odio blandit bibendum. Mauris ac mollis ligula. Curabitur ac commodo erat. Morbi ultrices lorem id vestibulum sodales. Nam facilisis efficitur massa ut ultricies. Praesent vel magna sollicitudin, maximus libero id, tempus lorem. Praesent bibendum, nulla vitae vulputate eleifend, elit nisi porttitor dolor, a semper lacus dui eu libero. Nullam quis orci erat. Sed porta venenatis nunc ac elementum.\n" +
            "\n" +
            "Sed elit urna, consectetur quis est id, tristique efficitur felis. Donec sollicitudin pulvinar cursus. In lorem urna, euismod sed eleifend non, commodo id dolor. Phasellus id suscipit ex. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Phasellus sed sapien posuere, dignissim justo a, mollis leo. Fusce mattis ante non ex bibendum tempus. Pellentesque sollicitudin risus eros, ac egestas magna faucibus ac. Aenean varius pharetra nunc quis rutrum. Suspendisse vel mauris at ante eleifend fermentum. Class aptent taciti sociosqu ad litora torquent per conubia nostra, per inceptos himenaeos. Vestibulum aliquam nibh tempus, luctus sapien id, fermentum orci. Morbi sodales urna et dolor convallis, in sollicitudin ligula sollicitudin.\n" +
            "\n" +
            "Morbi gravida, diam nec interdum sollicitudin, orci lorem tristique leo, id lobortis nunc nisi ac diam. Suspendisse volutpat libero neque. Curabitur ac lorem nec massa eleifend varius vitae in est. Nullam nulla ante, tristique in maximus viverra, sagittis ullamcorper sem. Integer in orci vel erat ultrices sodales. Ut quis risus a neque varius tristique ac vitae ante. Maecenas faucibus risus fermentum, consectetur est nec, fringilla felis. Phasellus luctus neque et elit rutrum porta. Ut ullamcorper, lorem non condimentum sodales, justo tellus tincidunt elit, eu mollis lacus ante nec risus. Sed bibendum dapibus libero sollicitudin bibendum.\n" +
            "\n" +
            "Pellentesque feugiat tellus ut porta lacinia. Nam eget sagittis erat. Integer maximus rhoncus lectus ut feugiat. Nam a tellus non leo suscipit interdum ornare in libero. Duis efficitur lorem quis eleifend porttitor. Fusce gravida orci nec diam ultrices tempor nec ut nisl. Aliquam erat volutpat. Maecenas quis tincidunt libero. Ut faucibus facilisis dui a pretium. Duis tincidunt mattis efficitur.\n" +
            "\n" +
            "Duis nibh odio, finibus luctus ex nec, mattis iaculis orci. Nam faucibus nunc quis enim fermentum, et faucibus odio ullamcorper. Quisque consequat sapien non scelerisque rhoncus. Proin ornare malesuada mollis. Etiam nec vulputate mi, at maximus sapien. Sed viverra nibh ut egestas dignissim. Suspendisse tempus risus ut pharetra ullamcorper. Duis massa neque, cursus nec leo ac, gravida imperdiet augue. Suspendisse ornare dictum pharetra. Duis quis semper mi, eu tincidunt turpis.\n" +
            "\n" +
            "Cras ullamcorper sapien eget ex maximus, ut vehicula ante tempor. Vestibulum ullamcorper posuere turpis, maximus dignissim erat sagittis ac. Integer consequat, mi sed interdum semper, tellus leo efficitur quam, vitae accumsan nisi magna vel metus. Integer dapibus nunc ut orci rutrum dignissim faucibus feugiat felis. Morbi purus urna, fermentum a vehicula sed, posuere a tortor. Suspendisse facilisis dictum hendrerit. Nunc vulputate lorem et quam euismod pharetra. Sed quis consequat leo. Mauris feugiat, orci ut tristique congue, ex odio ullamcorper ante, at aliquam sapien turpis eu lacus. Cras pretium nibh est, sed egestas purus commodo in. Sed nec egestas turpis. Phasellus ac nisi pharetra, tincidunt nisi lacinia, pharetra risus. Vestibulum risus mauris, feugiat et pulvinar ac, pulvinar eu erat. Fusce consectetur, arcu ac imperdiet vestibulum, dolor arcu aliquam lacus, sit amet eleifend nisl dolor vel nulla. Nam turpis purus, luctus non dignissim at, convallis vel elit.\n" +
            "\n" +
            "Suspendisse id magna at risus semper euismod id quis quam. Vivamus posuere, purus vel lobortis pretium, eros urna ornare enim, non consequat enim velit non est. Nam egestas felis nec euismod ultricies. Suspendisse potenti. Proin quam velit, mollis facilisis justo sed, egestas porta est. Aenean congue velit et tellus hendrerit pellentesque. Praesent convallis nisi gravida purus posuere, quis congue nisl maximus. Ut porttitor diam erat, sit amet lacinia ipsum suscipit vel. Nunc tempus pretium sem, sit amet mollis ipsum venenatis eget.\n" +
            "\n" +
            "Sed nec elit quam. Nulla ac augue libero. Ut egestas ultricies ante, rutrum efficitur arcu lacinia id. Donec pretium bibendum tempor. Phasellus iaculis, nisi luctus vehicula pulvinar, ante erat laoreet nisi, in interdum ipsum diam vel turpis. Integer in eros orci. Nunc laoreet purus erat, a fermentum nunc accumsan sed. Fusce eu felis vitae lorem facilisis ullamcorper nec at dui.\n" +
            "\n" +
            "Vestibulum dictum metus nulla, ac ultrices nisi commodo efficitur. Pellentesque rhoncus in massa a cursus. Etiam pharetra sapien arcu, at varius nisi tincidunt non. Donec facilisis fringilla porta. Duis vel purus sed nisl ornare cursus in eu nisl. Aliquam egestas luctus libero nec gravida. Ut quis consequat nibh. Donec bibendum rhoncus purus in cursus.\n" +
            "\n" +
            "Nulla porta, magna non pulvinar feugiat, libero nunc convallis odio, eget ullamcorper est risus a enim. Phasellus sagittis sit amet risus consectetur porta. Nam eu ullamcorper enim. Integer aliquet elit in dui euismod pulvinar. In blandit convallis blandit. Fusce a blandit risus. Nulla lobortis cursus dui feugiat lobortis. Maecenas malesuada est a magna venenatis accumsan. Aenean consectetur nisi id justo rhoncus, vitae pulvinar augue fermentum. Integer vel dignissim turpis. Maecenas vel lorem non lacus aliquet eleifend. In mollis mollis erat eget tincidunt. Fusce quis lectus sed lacus blandit bibendum non ac leo. Praesent malesuada luctus congue. Vivamus felis nunc, vulputate in tellus ut, efficitur tristique libero.\n" +
            "\n" +
            "Sed tincidunt purus eu tincidunt tincidunt. Praesent auctor consequat porttitor. Nunc quis nunc ex. Aenean ante massa, facilisis nec dapibus sed, lobortis vel lorem. Nunc non lacus enim. Aenean et convallis turpis. Vestibulum facilisis nisi dolor, eget rhoncus elit molestie eu. Suspendisse faucibus maximus lectus, eu pellentesque ante egestas lobortis. Cras accumsan efficitur enim, et pellentesque ante posuere at. Integer egestas vestibulum augue, quis aliquet urna maximus at. Vivamus nec augue libero. Aliquam at volutpat odio. Etiam sem nunc, blandit vitae diam in, lobortis ultricies mi.\n" +
            "\n" +
            "Nullam quis sagittis neque. Praesent eget nisi tellus. Fusce fringilla elementum est, ut sagittis nisl placerat id. Duis eu rhoncus diam. Maecenas neque metus, hendrerit vitae felis et, sagittis consequat velit. In diam nulla, laoreet vitae libero in, consectetur ultrices turpis. Suspendisse viverra justo commodo libero elementum, non volutpat libero sodales. Proin aliquet ante finibus varius placerat.\n" +
            "\n" +
            "Morbi at est a metus porttitor feugiat. Donec eleifend massa at varius malesuada. Cras urna mi, pulvinar vitae ornare vel, tincidunt mattis turpis. Mauris dapibus tempor magna. Ut lacinia quam libero, sit amet pellentesque purus vestibulum sit amet. Curabitur nec aliquam purus. Praesent porttitor varius risus, sit amet ultrices ex mollis fringilla. Maecenas tempor elit eu euismod convallis. Nunc accumsan pretium pharetra. Quisque hendrerit lorem sit amet lorem imperdiet vestibulum vitae vel nisl. Phasellus dictum orci id semper aliquet. Vestibulum varius laoreet lacus, congue ultrices augue hendrerit mattis. Ut rhoncus risus in dui interdum porta. Cras pretium consequat nunc in efficitur.\n" +
            "\n" +
            "Nullam bibendum iaculis arcu, sed tempor mi. Vivamus finibus erat ut commodo vehicula. Ut magna eros, pretium non magna facilisis, eleifend vulputate tortor. Sed dignissim ipsum et elementum consequat. Vivamus aliquet libero odio, id maximus metus lobortis eget. Mauris vitae pretium ante. Aenean fringilla elit mi, ut accumsan nulla convallis non. Vivamus placerat velit est, vitae commodo nisl sodales vel. Nulla leo lectus, lobortis et mattis ut, malesuada sed ipsum. Sed pretium et ex ac vestibulum.\n" +
            "\n" +
            "Etiam fringilla neque a odio tempus finibus. Vivamus enim ligula, ornare at lectus vel, malesuada condimentum nisi. Proin cursus volutpat libero ac vulputate. Pellentesque cursus auctor scelerisque. Donec ac felis placerat, sagittis est quis, malesuada orci. Aenean vitae enim ut urna accumsan pharetra. Maecenas sed dapibus neque, eget molestie ipsum. Donec nec interdum nisl, eget vestibulum sapien. Fusce ornare nec quam quis rutrum. In ut mauris dictum, posuere est eu, efficitur sapien. Proin venenatis tellus sed libero laoreet, in euismod est consequat. Curabitur viverra est tellus, vitae mattis orci elementum sit amet. Etiam tortor nisl, faucibus quis convallis nec, dapibus ac tortor. Nulla suscipit nunc sagittis magna posuere, et porttitor lectus condimentum. Ut ornare ex vel nisl eleifend tincidunt.\n" +
            "\n" +
            "Ut volutpat urna erat, nec tristique nisl ultricies sed. Etiam vel tortor eget dolor sollicitudin rutrum non non purus. Maecenas condimentum lacus et odio egestas, eget sollicitudin ipsum vulputate. Maecenas interdum molestie orci. Morbi quis odio in justo egestas pellentesque. Phasellus vel rutrum mauris. Cras accumsan tellus nulla, nec efficitur lacus sollicitudin quis. Donec eu orci ac est tincidunt aliquam.\n" +
            "\n" +
            "Duis sit amet est lobortis, cursus mauris nec, dapibus ipsum. Praesent in elit congue, ornare sapien sed, dictum urna. Proin non pretium odio, eu auctor eros. Quisque laoreet ante nunc, eu consequat risus feugiat dictum. Mauris ultrices odio tortor, id consectetur mauris iaculis ac. Suspendisse quam risus, lacinia in massa quis, consectetur ornare metus. Sed tellus massa, rutrum cursus tellus non, ultrices commodo urna. Praesent nibh elit, volutpat at scelerisque sit amet, ornare ac tortor";

    public CaptureThread(int status, ProcessRecorder recorder) {
        this.status = status;
        this.pauseTime = 0;
        this.resumeTime = 0;
        this.recorder = recorder;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        if(status == PAUSED_STATUS){
            this.pauseTime = DateHelper.nowMilliseconds();
        } else if(status == RESUMED_STATUS){
            this.resumeTime = DateHelper.nowMilliseconds();
        }
        this.status = status;
    }

    @Override
    public void run(){
        while(true){
            if(this.status == RUNNING_STATUS || this.status == RESUMED_STATUS){
                /* Capturamos solo los procesos que están corriendo*/
                Stream<ProcessHandle> processes = ProcessHandle.allProcesses().filter(ProcessHandle::isAlive);
                long now = DateHelper.nowMilliseconds();
                long resumedCaptureTime = this.pauseTime + (now - this.resumeTime);
                long captureTime = this.resumeTime == 0 ? now : resumedCaptureTime;
                String jsonProcessesMap = processesSnapshotToJsonFormat(processes, captureTime);
                //String processesSnapshotToCSV = processesSnapshotToCSV(processes, captureTime);
                try {
                    this.recorder.getFileOutputStream().write(jsonProcessesMap.getBytes());
                    if(this.recorder.getDataListeners() != null){
                        CaptureEvent captureEvent = new CaptureEvent(this.recorder.getCaptureConfigurationController().getId(),
                                this.recorder.getClass().getName(), jsonProcessesMap);
                        for(PluginCaptureListener dataListener: this.recorder.getDataListeners()){
                            dataListener.onDataReceived(this.recorder,captureEvent);
                        }
                    }
                } catch (IOException e) {
                    ProcessRecorder.LOGGER.log(Level.SEVERE, null, e);
                    return;
                }
            }
            else if(this.status == PAUSED_STATUS){
                /* Si se ha pausado la captura, vamos durmiendo el Thread para que este "reaccione" de vez en cuando
                y no deje de ser considerado por le procesador al momento de la planificación de los procesos
                 */
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else if(this.status == STOPPED_STATUS) {
                try {
                    this.recorder.getFileOutputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                    ProcessRecorder.LOGGER.log(Level.SEVERE, null, e);
                }
                return;
            }
        }
    }

    private String processesSnapshotToJsonFormat(Stream<ProcessHandle> processes, long captureTime){
        Gson gson = new Gson();
        String otherString = "-";
        long otherLong = -1;
        HashMap<String,Object> processesJsonMap= new HashMap<>();
        List<HashMap<String, Object>> processesList = new ArrayList<>();
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
            HashMap<String, Object> processJsonMap = new HashMap<>();
            processJsonMap.put(PID_KEY, pid);
            processJsonMap.put(USER_NAME_KEY, userName);
            processJsonMap.put(START_INSTANT_KEY, startInstant);
            processJsonMap.put(TOTAL_CPU_DURATION_KEY, totalCpuDuration);
            processJsonMap.put(COMMAND_KEY, command);
            processJsonMap.put(PARENT_PID_KEY, parentPid);
            processJsonMap.put(HAS_CHILDREN_KEY, hasChildren);
            processJsonMap.put(SUPPORTS_NORMAL_TERMINATION_KEY, supportsNormalTermination);
            processesList.add(processJsonMap);
        });
        processesJsonMap.put(PROCESSES_KEY, processesList);
        processesJsonMap.put(CAPTURE_MILLISECONDS_KEY, captureTime);
        return gson.toJson(processesJsonMap) + LINE_SEPARATOR;
    }

    private String processesSnapshotToCSV(Stream<ProcessHandle> processes, long captureTime){
        Gson gson = new Gson();
        String otherString = "-";
        long otherLong = -1;
        String processDataSeparator = ",";
        String headers = "pid,captureTime,userName,startInstant,totalCpuDuration,command,supportsNormalTermination,parentPid,hasChildren";
        StringBuilder result = new StringBuilder(headers + LINE_SEPARATOR);
        List <String> processesList = new ArrayList<>();
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
            int hasChildren = process.children().count() != 0 ? 1 : 0;
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
            int supportsNormalTermination = process.supportsNormalTermination() ? 1 : 0; /* indica si el proceso puede destruirse
            usando el método destroy() de la API de procesos de Java (interfaz ProcessHandle)

            En caso de no poder usar destroy(), se debe destruir a la fuerza con destroyForcibly()
            de la misma API.
            */
            Process processModel = new Process(pid, userName, startInstant, totalCpuDuration, command, parentPid, hasChildren, supportsNormalTermination, captureTime);
            processesList.add(processModel.toCSV());
        });
        for(String processString : processesList){
            result.append(processString);
        }
        return result.toString();
    }
}
