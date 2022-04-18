package Testing;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.Buffer;
import java.util.Set;

/**
 * Basic class for the client tester that runs in a separate thread
 * and read the messages from the server
 */
public class ClientReader implements  Runnable{

    private BufferedReader clientReader;

    public ClientReader (BufferedReader clientReader){
        this.clientReader = clientReader;
    }

    @Override
    public void run (){
        while (true) {
            String message = new String();
            try {
                message = this.clientReader.readLine();
                System.out.println(message);
            }
            catch (IOException IOE){
                System.out.println("Error while reading from the input stream");
                System.exit(1);
            }
        }
    }
}
