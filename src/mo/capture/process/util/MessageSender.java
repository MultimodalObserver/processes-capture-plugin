package mo.capture.process.util;

import mo.communication.Command;
import mo.communication.PetitionResponse;
import mo.communication.RemoteClient;
import mo.communication.ServerConnection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/* Clase que permite enviar de forma facil mensajes a todos los clientes de MO

    Los mensajes son de la forma:

        tipo_mensaje: contenido_mensaje
 */
public class MessageSender {

    public static void sendMessage(String messageType, String messageContent){
        HashMap<String, Object> data = new HashMap<>();
        data.put(messageType, messageContent);
        PetitionResponse petitionResponse = new PetitionResponse(Command.DATA_STREAMING, data);
        ArrayList<RemoteClient> clients = ServerConnection.getInstance().getClients();
        if(clients == null || clients.size() == 0){
            return;
        }
        for(RemoteClient client : clients){
            client.send(petitionResponse);
        }
        //System.out.println("Envie mensaje");
    }
}
