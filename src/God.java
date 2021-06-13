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
    private static final int MAXUSERS = 3;
    private static Vector<SendAssist> clients = new Vector<>();
    private static ExecutorService pool = Executors.newFixedThreadPool(MAXUSERS);
    private GameManager gameManager;
    private ArrayList<String> names;

    public God() {
        gameManager = new GameManager();
        names = new ArrayList<>();
    }

    public void godRun(God god) {
        God.SendAssist sendAssist = null;
        int port = randomPort();
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("God has just started a game running on " + port + "\n waiting for players to join...");
            int userCounter = 0;
            while (true) {

                Socket socket = serverSocket.accept();
                objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectInputStream = new ObjectInputStream(socket.getInputStream());
                String name;
                name =playerAdd();
                names.add(name);
                gameManager.addPlayer(new Player(name));
                System.out.println(name + " is connected from port: " + socket.getPort());
                String c = (String) objectInputStream.readObject();
                gameManager.addReadyState(c);
                sendAssist = god.new SendAssist(clients, name, objectInputStream, objectOutputStream, gameManager);
                clients.add(sendAssist);
                pool.execute(sendAssist);
                userCounter++;

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        God god = new God();
        god.godRun(god);
    }

    public void readyCheck() throws IOException, ClassNotFoundException {
        do {
            try {
                objectOutputStream.writeObject("are you ready ? yes/no");
            } catch (IOException e) {
                e.printStackTrace();
            }
            //  gameManager.addReadyState((Character) objectInputStream.readObject());
        }
        while ((Character) objectInputStream.readObject() != 'y');
    }

    public String playerAdd() {

        String name = "";
        do {
            try {
                objectOutputStream.writeObject("send");
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                name = (String) objectInputStream.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        } while (!isValidName(name));
        try {
            objectOutputStream.writeObject("end");
        } catch (IOException e) {
            e.printStackTrace();
        }

        return name;
    }

    public boolean isValidName(String name) {
        boolean isVal = true;
        for (String Name : names) {
            if (Name.equals(name)) {
                isVal = false;
                break;
            }
        }
        return isVal;
    }

    public int randomPort() {
        Random rand = new Random();
        int port = rand.nextInt(7000) + 3000;
        return port;
    }


    public class SendAssist implements Runnable {
        public static final String ANSI_RESET = "\u001B[0m";
        public static final String ANSI_BLACK = "\u001B[30m";
        public static final String ANSI_RED = "\u001B[31m";
        public static final String ANSI_GREEN = "\u001B[32m";
        public static final String ANSI_YELLOW = "\u001B[33m";
        public static final String ANSI_BLUE = "\u001B[34m";
        public static final String ANSI_PURPLE = "\u001B[35m";
        public static final String ANSI_CYAN = "\u001B[36m";
        public static final String ANSI_WHITE = "\u001B[37m";
        private Vector<SendAssist> clients;
        private String name;
        private String broadCastMsg;
        private ObjectOutputStream objectOutputStream = null;
        private ObjectInputStream objectInputStream = null;
        private GameManager gameManager;
        private Phaze gamePhaze;

        /**
         * constructor for the SendAssist Class
         * where every task related to server is managed in
         * @param clients
         * @param name
         * @param objectInputStream
         * @param objectOutputStream
         * @param gameManager
         */
        public SendAssist(Vector<SendAssist> clients, String name, ObjectInputStream objectInputStream, ObjectOutputStream objectOutputStream, GameManager gameManager) {
            this.clients = clients;
            this.name = name;
            this.objectOutputStream = objectOutputStream;
            this.objectInputStream = objectInputStream;
            this.gameManager = gameManager;
            this.gamePhaze = Phaze.DAY;
        }

        /**
         * getter for the current Phaze of the game
         * @return
         */
        public Phaze getGamePhaze() {
            return gamePhaze;
        }

        /**
         * setter for the Phaze of the Game
         * @param gamePhaze
         */
        public void setGamePhaze(Phaze gamePhaze) {
            this.gamePhaze = gamePhaze;
        }

        /**
         * this method sends a string message to every connected clients
         *
         * @param msg
         */
        public void sendToAll(String msg) {
            for (SendAssist sa : clients) {
                try {
                    if (msg.equals("") || msg.equals(null))
                        continue;
                    sa.objectOutputStream.writeObject(msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        /**
         * this method spreads roles among the players in game
         */
        public void spreadRoles() {
            gameManager.assignRoles();
            int i = 0;
            for (Player p : gameManager.getPlayers()) {
                sendToClient("you are : " + p.getRole().toString(), i);
                System.out.println(p.getUsername() +" is: "+ p.getRole().toString());
                i++;
            }

        }

        /**
         * this method sends a String message to a specific client by its index in the list of clients ordering according to
         * the connection
         *
         * @param message
         * @param clientIndex
         */
        public void sendToClient(String message, int clientIndex) {
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
        public void introductionNight() {
            mafiaIntroduce();
            docIntroduce();
        }

        /**
         * introduces the mafia team to other mafias
         */
        private void mafiaIntroduce() {
            ArrayList<Integer> mafias = new ArrayList<>();
            for (int i = 0; i < gameManager.getPlayers().size(); i++) {
                if (gameManager.getPlayers().get(i).getRole() instanceof Mafia)
                    mafias.add(i);
            }

            for (int i = 0; i < mafias.size(); i++) {
                for (int j = 0; j < mafias.size(); j++) {
                    if (i == j)
                        continue;
                    else
                    sendToClient(gameManager.getPlayers().get(mafias.get(j)).toString(), mafias.get(i));
                }
            }
        }

        /**
         * introduces the doctor of game to the Mayor of game
         */
        private void docIntroduce() {
            for (int i = 0; i < gameManager.getPlayers().size(); i++) {
                if (gameManager.getPlayers().get(i).getRole() instanceof Mayor) {
                    for (int j = 0; j < gameManager.getPlayers().size(); j++) {
                        if (gameManager.getPlayers().get(j).getRole() instanceof Doctor)
                            sendToClient(gameManager.getPlayers().get(j).toString(), i);
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
                /**
                 * literally the game starts from here cause the gameAllowance method gives us the permission to the primary tasks
                 * such as spreading roles and doing th introductionNight
                 */
                if (gameManager.startAllowance()) {
                    sendToAll("game shall begin");
                    spreadRoles();
                    introductionNight();
                    gameManager.setGameShelf(true);
                    }
              //  while (!gameManager.endGame())
                while(true){

                        switch (gamePhaze) {
                            case DAY:
                                day();
                                break;
                            case NIGHT:
                                night();
                                switchPhaze();
                                break;
                            case VOTING:
                                voting();
                                switchPhaze();
                                break;
                        }


                }
              //  sendToAll(gameManager.gameOverStatement());
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        /**
         * this method is for managing the game while its on Phaze of DAY
         * aka the CHATROOM of the game
         */
        public void day(){
            System.out.println("PHAZE : DAY");
            if (gameManager.isGameShelf())
            sendToAll(ANSI_RED + "Entered chatroom__type something" + ANSI_RESET);
            String tst="";
            long start = System.currentTimeMillis();
            long finish = start + 5*60*1000;

            try {
                while (System.currentTimeMillis()<finish){
                    tst = (String) objectInputStream.readObject();
                    sendToAll(name+" : "+tst);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

            switchPhaze();
        }

        /**
         * this method is for managing the game while its on Phaze NIGHT
         * what happens in this method is what every players do in their night move
         */
        public void night(){}

        /**
         * this method will start working right after DAY Phaze and the CHATROOM time ends
         * and is a voting system for players to kick out any other player
         *
         * and if we end up to a tie it wont do anything
         */
        public void voting(){
            System.out.println("PHAZE : VOTING");
            if (gameManager.isGameShelf())
            sendToAll(ANSI_BLUE+"Entered Voting Area  ------- enter the name of who U think is Mafia\n Note not to vote for yourself or a wrong name"+ANSI_RESET);
            for (int i = 0; i <clients.size() ; i++) {
                sendToClient(gameManager.remainingPlayers(i),i);
            }
            String tst="";
            try {
                tst = (String)objectInputStream.readObject();
                gameManager.voteInit();
                if(tst.equals(name))
                    gameManager.getVotes().put(playerByName(name),"INVALID");
                else if (nameExist(tst))
                    gameManager.getVotes().put(playerByName(name),tst);
                else
                    gameManager.getVotes().put(playerByName(name),"INVALID");

            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        /**
         * this method makes the phazes to change their states while the game is going on
         * ------if we are in day and the method is done , we move to voting Phaze----
         * ------if we are in voting and the method is done , we move to Night Phaze-----
         * ------if we are in night and the actions are done, we move to Day Phaze-------
         */
        private void switchPhaze(){
            switch (getGamePhaze())
            {
                case DAY:setGamePhaze(Phaze.VOTING);
                break;
                case VOTING:setGamePhaze(Phaze.NIGHT);
                break;
                case NIGHT:setGamePhaze(Phaze.DAY);
                break;
            }
        }

        private Player playerByName(String name){
            Player player=null;
            for(Player p:gameManager.getPlayers())
                if (p.getUsername().equals(name))
                    player=p;

                return player;
        }
        private boolean nameExist(String name){
            boolean exists = false;

            for(Player p: gameManager.getPlayers())
                if(p.getUsername().equals(name)) {
                    exists = true;
                    break;
                }
            return exists;
        }

    }
}
