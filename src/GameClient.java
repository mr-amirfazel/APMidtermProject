import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ConnectException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class GameClient {
        private static final int MAXUSERS = 6;
        private  int port;
        private Socket socket;
        private String username;
        private ObjectOutputStream objectOutputStream;
        private ObjectInputStream objectInputStream;
        private ArrayList<String> names;
        Thread t1;

    public GameClient() {
        names = new ArrayList<>();
    }

    public GameClient(int port) {
        this.port = port;
        names = new ArrayList<>();
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void godConnect(){
        Scanner scanner = new Scanner(System.in);
        try(Socket socket = new Socket("127.0.0.1",port);) {
            System.out.println("connected to server\n waiting for  other players...");
             objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
             objectInputStream = new ObjectInputStream(socket.getInputStream());
             String name = playerAdd();
            setUsername(name);
            objectOutputStream.writeObject(name);
            System.out.println("Dear "+name+", you are officially a part of the game");
            boolean isready =readySet();
//            if (isready)
//            objectOutputStream.writeObject("true");
            System.out.println("you are set .you can chat to other players Untill the room is full.");
            GameClient gameClient = new GameClient();
            GameClient.ReadAssist readAssist = gameClient.new ReadAssist(objectInputStream);
            String tst ;
            t1 = new Thread(readAssist);
//            objectOutputStream.writeObject("ready");
//            objectOutputStream.writeObject("chat");
            t1.start();
            while(true){
                tst = scanner.nextLine();
                objectOutputStream.writeObject(tst);
//
//           // objectOutputStream.writeObject("introduction");
            }

        }

        catch(ConnectException e){
            System.out.println("no game is running on port: "+port);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
//        catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }


    }
    public boolean readySet() throws IOException {
        boolean iscor =false;
        Scanner scanner = new Scanner(System.in);
        char c;
       while (true) {
           System.out.println("are you ready???");
           c = scanner.next().charAt(0);
           if (c == 'y') {
               iscor=true;
               break;
           }
       }
        return iscor;
    }
    public void read()
    {
        try {
            System.out.println((String)objectInputStream.readObject());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String playerAdd(){
        String name="";

        while (true) {
            try {
                if (!(objectInputStream.readObject()).equals("send")) break;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            Scanner scanner = new Scanner(System.in);
            System.out.println("enter name:");
            name = scanner.nextLine();
            try {
                objectOutputStream.writeObject(name);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return name;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("enter the entry port:");
        int prt =scanner.nextInt();
        GameClient gameClient = new GameClient(prt);
        gameClient.godConnect();

    }
    public class ReadAssist implements Runnable{
        ObjectInputStream objectInputStream;

        public ReadAssist(ObjectInputStream objectInputStream) {
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
            System.out.println("EXIT : to leave chat \n HISTORY: review chats");
            String tst;
            try {
                while(true) {
                   tst =(String)objectInputStream.readObject();
                   if(tst.equalsIgnoreCase("chatready"))
                   continue;
                   else
                   {
                       System.out.println(tst);
                   }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }
        
    }
}

