package ChatRoom;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class Client {
       private int port;
      private  Socket socket;
      private String username;
      private String RoleTag;

    public Client() { }

    public Client(int port) {
        this.port = port;
    }
    public void serverReach(){
        Scanner scanner = new Scanner(System.in);
        try {
            Socket socket = new Socket("127.0.0.1",port);
            System.out.println("connected to server");
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            objectOutputStream.writeObject(username);
            System.out.println("enter message down below:");
            Client c1 = new Client();
            Client.SelfView selfView = c1.new SelfView(objectInputStream);
            String tst = scanner.nextLine();
            Thread t1 = new Thread(selfView);
            t1.start();
            while(true)
            {
                objectOutputStream.writeObject(tst);
                tst = scanner.nextLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("enter the entry port:");
        int prt =scanner.nextInt();scanner.nextLine();
        Client cl = new Client(prt);
        System.out.println("enter name:");
        cl.setUsername(scanner.nextLine());
        cl.serverReach();
    }

    public void setUsername(String username) {
        this.username = username;
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


