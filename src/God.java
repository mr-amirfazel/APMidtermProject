import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class God {

    public static void main(String[] args) {
        try( ServerSocket serverSocket = new ServerSocket(8585)) {
            System.out.println("God has just started a game \n waiting for players to join...");
            while (true){
                Socket socket = serverSocket.accept();
                System.out.println("a new player has joined");

            }


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
