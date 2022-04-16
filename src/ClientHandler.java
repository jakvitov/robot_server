import java.io.BufferedReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable{

    private PrintWriter clientWriter;
    private BufferedReader clientReader;
    private Socket clientSocket;
    private String suffix = "\\a\\b";

    public ClientHandler (Socket clientSocket){

    }

    @Override
    public void run(){

    }

}
