import dataStruct.AutKeys;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * A Server is running in an infinite loop listening for clients on a specific port and waiting for clients connections
 * As soon as client connects, we start ClientHandler in a separate thread for him and pass it that client
 */

public class Server {

    private String suffix = "\\a\\b";
    private AutKeys keyDatabase;
    private ServerSocket serverSocket;

    public Server (int portNum){
        try {
            this.serverSocket = new ServerSocket(portNum);
            System.out.println("Server started listening on port " + portNum);
        }
        catch (IOException IOE){
            System.out.println("Error while opening the server socket!");
            System.exit(1);
        }
    }

    public static void main(String[] args) {

        Server server = new Server(8080);

        while (true) {

            Socket client;

            try {
                client = server.serverSocket.accept();
            }
            catch (IOException IOE){
                System.out.println("Error while accepting the connection!");
                continue;
            }

            Thread clientThread = new Thread(new ClientHandler(client));
            clientThread.start();

        }
    }
}
