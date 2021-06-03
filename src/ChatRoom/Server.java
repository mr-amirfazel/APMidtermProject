package ChatRoom;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private static int i=1;
    private static Vector<Handler> clients = new Vector<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(6);
    public static void main(String[] args) {
        try( ServerSocket serverSocket = new ServerSocket(8085)) {
            System.out.println("waiting for a client to connect");
            while(true) {
                Socket socket = serverSocket.accept();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                System.out.println("client is connected from port: " + socket.getPort());
                Server s1 = new Server();
                Server.Handler handler= s1.new Handler(clients,socket,"client"+i,objectInputStream,objectOutputStream);
                clients.add(handler);
                pool.execute(handler);
                i++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public class Handler implements Runnable{
        private Vector<Handler> clients = new Vector<>();
        private Socket socket;
        private String name;

        private ObjectOutputStream objectOutputStream = null;
        private ObjectInputStream objectInputStream = null;

        public Handler(Vector<Handler> clients, Socket socket, String name, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {
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
                    for (Handler h : clients) {
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

