import ChatRoom.Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class GameClient {
        private  int port;
        private Socket socket;
        private String username;

    public GameClient() { }

    public GameClient(int port) {
        this.port = port;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void godConnect(){
        Scanner scanner = new Scanner(System.in);
        try(Socket socket = new Socket("127.0.0.1",port);) {
            System.out.println("connected to server");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(username);
            System.out.println((String)objectInputStream.readObject());
            objectOutputStream.writeObject(scanner.next().charAt(0));

        }

        catch(ConnectException e){
            System.out.println("no game is running on port: "+port);
        }
        catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("enter the entry port:");
        int prt =scanner.nextInt();scanner.nextLine();
        GameClient gameClient = new GameClient(prt);
        System.out.println("enter name:");
        gameClient.setUsername(scanner.nextLine());
        gameClient.godConnect();

    }
    public class SelfView implements Runnable{
        ObjectInputStream objectInputStream;

        public SelfView(ObjectInputStream objectInputStream) {
            this.objectInputStream = objectInputStream;
        }

        /**
         * When an object implementing interface <code>Runnable</code> is used
         * to create a thread, starting the thread causes the object's
         * <code>run</code> method to be called in that separately executing
         * thread.
         * <p>
         * The general contract of the method <code>run</code> is that it may
         * take any action whatsoever.
         *
         * @see Thread#run()
         */
        @Override
        public void run() {


            try {
                while(true){
                    System.out.println((String)objectInputStream.readObject());
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
