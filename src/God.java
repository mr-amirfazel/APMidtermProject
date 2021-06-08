import ChatRoom.ChatServer;
import ChatRoom.RoleTag;
import Roles.Doctor;
import Roles.Mafia;
import Roles.Mayor;

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
    private static final int MAXUSERS=3;
    private static Vector<SendAssist> clients = new Vector<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(MAXUSERS);
    private GameManager gameManager;
    private ArrayList<String> names;

    public God() {
        gameManager = new GameManager();
        names = new ArrayList<>();
    }

    public void godRun(God god){
        God.SendAssist sendAssist= null;
        int port = randomPort();
        try(ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("God has just started a game running on "+port+"\n waiting for players to join...");
            int userCounter=0;
            while(true) {

                    Socket socket = serverSocket.accept();
                    objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    objectInputStream = new ObjectInputStream(socket.getInputStream());
                    String name = playerAdd();
                    names.add(name);
                    gameManager.addPlayer(new Player(name));
                    System.out.println(name + " is connected from port: " + socket.getPort());
                    String c = (String) objectInputStream.readObject();
                    gameManager.addReadyState(c);
                    sendAssist = god.new SendAssist(clients, name, objectInputStream, objectOutputStream,gameManager);
                    clients.add(sendAssist);
                    pool.execute(sendAssist);
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
    public int randomPort(){
        Random rand = new Random();
        int port = rand.nextInt(7000)+3000;
        return port;
    }


    public class SendAssist implements Runnable{
        private Vector<SendAssist> clients;
        private String name;
        private String broadCastMsg;
        private ObjectOutputStream objectOutputStream = null;
        private ObjectInputStream objectInputStream = null;
        private GameManager gameManager;


        public SendAssist(Vector<SendAssist> clients, String name, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream,GameManager gameManager) {
            this.clients = clients;
            this.name = name;
            this.objectOutputStream = objectOutputStream;
            this.objectInputStream = objectInputStream;
            this.gameManager=gameManager;
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
         * this method sends a string message to every connected clients
         * @param msg
         */
        public void sendToAll(String msg){
                for (SendAssist sa:clients) {
                    try {
                        sa.objectOutputStream.writeObject(msg);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

        }

        public void spreadRoles(){
            gameManager.assignRoles();
            int i=0;
            for (Player p:gameManager.getPlayers()){
                sendToClient("you are : "+p.getRole().toString(),i);
                i++;
            }

        }

        /**
         * this method sends a String message to a specific client by its index in the list of clients ordering according to
         * the connection
         * @param message
         * @param clientIndex
         */
        public void sendToClient(String message,int clientIndex)
        {
            try {
                clients.get(clientIndex).objectOutputStream.writeObject(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /**
         * this method makes the mafia know each other
         * and majes the mayor to know the doctor
         */
        public void introductionNight(){
            mafiaIntroduce();
            docIntroduce();
        }
        public void mafiaIntroduce()
        {
            ArrayList<Integer> mafias = new ArrayList<>();
            for (int i = 0; i <gameManager.getPlayers().size() ; i++) {
                if(gameManager.getPlayers().get(i).getRole() instanceof Mafia)
                    mafias.add(i);
            }

            for (int i = 0; i < mafias.size() ; i++) {
                for (int j = 0; j < mafias.size() ; j++) {
                    if(i==j)
                        continue;
                    sendToClient(gameManager.getPlayers().get(j).toString(),mafias.get(i));
                }
            }
        }
        public void docIntroduce(){
            for (int i = 0; i <gameManager.getPlayers().size() ; i++) {
                if(gameManager.getPlayers().get(i).getRole() instanceof Mayor)
                {
                    for (int j = 0; j <gameManager.getPlayers().size() ; j++) {
                        if(gameManager.getPlayers().get(j).getRole() instanceof Doctor)
                                sendToClient(gameManager.getPlayers().get(j).toString(),i);
                    }
                }
            }
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

                if(gameManager.startAllowance()) {
//                    while(true) {
                    tst = (String) objectInputStream.readObject();
                    if(tst.equalsIgnoreCase("ready")) {
                        sendToAll("game shall begin");
                        spreadRoles();
                        introductionNight();
                    }
                    System.out.println("done!!");


                    tst=(String)objectInputStream.readObject();
                        while(true) {
                            if (tst.equalsIgnoreCase("chat"))
                            {
                            String msg =(String) objectInputStream.readObject();
                            sendToAll(name+" : "+msg);
                        }
                    }

//                }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

}
}
