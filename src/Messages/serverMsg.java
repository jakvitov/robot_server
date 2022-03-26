package Messages;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

//Class used to send messages to a client
public class serverMsg {
    private String suffix = "\\a\\b";
    private Socket clientSocket;
    private PrintWriter clientWriter;
    public serverMsg(Socket input_socket){
        this.clientSocket = input_socket;
        try {
            this.clientWriter = new PrintWriter(this.clientSocket.getOutputStream(), true);
        } catch (IOException e){
            System.out.println("Error in opening the print stream for client!");
            System.exit(0);
        }
    }
    public void server_confirmation(int confirmation_code){
        String conf_message = Integer.toString(confirmation_code) + suffix;
        clientWriter.print(conf_message);
    }
    public void server_move(){
        clientWriter.print("102 MOVE" + suffix);
    }
    public void server_turn_left(){
        clientWriter.print("103 TURN LEFT" + suffix);
    }
    public void server_turn_right(){
        clientWriter.print("103 TURN RIGHT" + suffix);
    }
    public void server_pick_up(){
        clientWriter.print("105 GET MESSAGE" + suffix);
    }
    public void server_logout(){
        clientWriter.print("106 LOGOUT" + suffix);
    }
    public void server_key_request(){
        clientWriter.print("107 KEY REQUEST" + suffix);
    }
    public void server_ok(){
        clientWriter.print("200 OK" + suffix);
    }
    public void server_login_failed(){
        clientWriter.print("300 SERVER_LOGIN_FAILED" + suffix);
    }
    public void server_syntax_error(){
        clientWriter.print("301 SYNTAX ERROR" + suffix);
    }
    public void server_logic_error(){
        clientWriter.print("302 LOGIC ERROR" + suffix);
    }
    public void key_out_of_range_error(){
        clientWriter.print("303 KEY OUT OF RANGE" + suffix);
    }
};
