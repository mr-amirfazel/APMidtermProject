import ChatRoom.ChatServer;
import ChatRoom.RoleTag;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class God {
    ObjectOutputStream objectOutputStream;
    ObjectInputStream objectInputStream;
    private static final int MAXUSERS=10;
    private static Vector<SendAssist> clients = new Vector<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(MAXUSERS);
    private GameManager gameManager;
    private ArrayList<String> names;

    public God() {
        gameManager = new GameManager();
        names = new ArrayList<>();
    }

    public void godRun(God god){
        try( ServerSocket serverSocket = new ServerSocket(8585)) {
            System.out.println("God has just started a game \n waiting for players to join...");
            int userCounter=0;
            while (userCounter<MAXUSERS){
                Socket socket = serverSocket.accept();
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectInputStream = new ObjectInputStream(socket.getInputStream());
                String name =(String) objectInputStream.readObject();
                names.add(name);
                gameManager.addPlayer(new Player(name));
                System.out.println(name+" is connected from port: " + socket.getPort());
                God.SendAssist sendAssist = god.new SendAssist(clients,socket,name,objectInputStream,objectOutputStream);
                objectOutputStream.writeObject("are you ready ? yes/no");
                gameManager.addReadyState((Character) objectInputStream.readObject());




                userCounter++;
            }


        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        God god = new God();
        god.godRun(god);
    }





    public class SendAssist implements Runnable{
        private Vector<SendAssist> clients = new Vector<>();
        private Socket socket;
        private String name;
        private RoleTag roleTag;

        private ObjectOutputStream objectOutputStream = null;
        private ObjectInputStream objectInputStream = null;


        public SendAssist(Vector<SendAssist> clients, Socket socket, String name, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {
            this.clients = clients;
            this.socket = socket;
            this.name = name;
            this.objectInputStream = objectInputStream;
            this.objectOutputStream = objectOutputStream;

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
                String tst;

                while (true){
                    tst = (String)objectInputStream.readObject();
                    for (SendAssist h : clients) {
                        if (tst.equals("")||tst.equals(null))
                            continue;
                        h.objectOutputStream.writeObject(name+" : "+tst);
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                objectInputStream.close();
                objectOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

}
