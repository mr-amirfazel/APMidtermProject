import ChatRoom.RoleTag;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
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
        Random rand = new Random();
        int port = rand.nextInt(7000)+3000;
        try( ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("God has just started a game running on "+port+"\n waiting for players to join...");
            int userCounter=0;
            while (userCounter<MAXUSERS){
                Socket socket = serverSocket.accept();
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectInputStream = new ObjectInputStream(socket.getInputStream());
                String name = playerAdd();
                names.add(name);
                gameManager.addPlayer(new Player(name));
                System.out.println(name+" is connected from port: " + socket.getPort());
                String c =(String)objectInputStream.readObject();
                gameManager.addReadyState(c);
                God.SendAssist sendAssist = god.new SendAssist(clients,name,objectInputStream,objectOutputStream);
                clients.add(sendAssist);

             //   sendAssist.setBroadCastMsg("everyone is ready we will begin in 3..2...1");
              //  pool.execute(sendAssist);





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
    public void readyCheck() throws IOException, ClassNotFoundException {
        do{
            try {
                objectOutputStream.writeObject("are you ready ? yes/no");
            } catch (IOException e) {
                e.printStackTrace();
            }
            //  gameManager.addReadyState((Character) objectInputStream.readObject());
        }
        while((Character)objectInputStream.readObject()!='y');
    }
    public String playerAdd(){

        String name="";
        do {
            try {
                objectOutputStream.writeObject("send");
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                name =(String) objectInputStream.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }while(!isValidName(name));
        try {
            objectOutputStream.writeObject("end");
        } catch (IOException e) {
            e.printStackTrace();
        }

       return name;
    }
    public  boolean isValidName(String name)
    {
        boolean isVal = true;
        for (String Name:names)
        {
            if (Name.equals(name)){
                isVal=false;
            break;}
        }
        return isVal;
    }



    public class SendAssist implements Runnable{
        private Vector<SendAssist> clients;
        private String name;
        private String broadCastMsg;
        private ChatMode chatMode;
        private ObjectOutputStream objectOutputStream = null;
        private ObjectInputStream objectInputStream = null;


        public SendAssist(Vector<SendAssist> clients, String name, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream) {
            this.clients = clients;
            this.name = name;
            this.objectInputStream = objectInputStream;
            this.objectOutputStream = objectOutputStream;
            this.chatMode=ChatMode.ROOM;

        }

        /**
         * getter for the state of Chatmode
         * @return
         */
        public ChatMode getChatMode() {
            return chatMode;
        }

        /**
         * setter for the state of chatmode
         * @param chatMode
         */
        public void setChatMode(ChatMode chatMode) {
            this.chatMode = chatMode;
        }

        /**
         * getter for the string message to be broadcasted
         * @return
         */
        public String getBroadCastMsg() {
            return broadCastMsg;
        }

        /**
         * setter for the message to be broadcasted
         * @param broadCastMsg
         */
        public void setBroadCastMsg(String broadCastMsg) {
            this.broadCastMsg = broadCastMsg;
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
            if (chatMode.equals(ChatMode.ROOM)) {


                try {
                    String tst;

                    while (true) {
                        tst =getBroadCastMsg();
                        for (SendAssist h : clients) {
                            if (tst.equals("") || tst.equals(null))
                                continue;
                            h.objectOutputStream.writeObject(name + " : " + tst);
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    objectInputStream.close();
                    objectOutputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            else if(chatMode.equals(ChatMode.SOLO))
            {

            }
        }
    }

}
