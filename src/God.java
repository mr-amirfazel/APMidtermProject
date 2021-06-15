import ChatRoom.ChatServer;
import ChatRoom.RoleTag;
import Roles.*;

import javax.swing.*;
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
                if(userCounter<MAXUSERS)
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
         * this method sends a specified String message only to players with a mafia role
         * @param msg
         */
        public void sendToMafias(String msg){
            ArrayList<Integer> mafiaIndex = getMafias();
            for (int i=0;i<mafiaIndex.size();i++)
            {
                sendToClient(msg, mafiaIndex.get(i));
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
            ArrayList<Integer> mafias = getMafias();
            for (int i = 0; i < mafias.size(); i++) {
                for (int j = 0; j < mafias.size(); j++) {
                    if (i == j)
                        continue;
                    else
                    sendToClient(gameManager.getPlayers().get(mafias.get(j)).toString(), mafias.get(i));
                }
            }
        }
        private ArrayList<Integer> getMafias(){
            ArrayList<Integer> mafias = new ArrayList<>();
            for (int i = 0; i < gameManager.getPlayers().size(); i++) {
                if (gameManager.getPlayers().get(i).getRole() instanceof Mafia)
                    mafias.add(i);
            }
            return mafias;
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
                                switchPhaze();
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

            if (gameManager.isGameShelf()){
                System.out.println("PHAZE : DAY");
                sendToAll(ANSI_RED + "Entered chatroom__type something \nyou have 5 minutes \nenter\"READY\" for Voting" + ANSI_RESET);
            }
            String tst="";
            long start = System.currentTimeMillis();
            long finish = start + 5*60*1000;
           // gameManager.setReadyVoteCount(0);
            try {
                while (System.currentTimeMillis()<=finish) {
                        tst = (String) objectInputStream.readObject();
                        if (tst.equals("READY")) {
                            sendToAll(name + ":" + ANSI_RED + tst + ANSI_RESET);
                            sendToAll(name+ANSI_WHITE+" has decided to vote.Other players can chat"+ANSI_RESET);
                                break;

                        } else
                            sendToAll(name + " : " + tst);
                    }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }


        }

        /**
         * this method is for managing the game while its on Phaze NIGHT
         * what happens in this method is what every players do in their night move
         */
        public void night(){
            if (gameManager.isGameShelf()){
                System.out.println("PHAZE : NIGHT");
                sendToAll(ANSI_PURPLE+"its Night.\nif youre role has a night task,prepare for mission :)"+ANSI_RESET);
            }
            mafiaChat();
           mafiaAttack();
           lecterSave();


        }

        /**
         * first thing that happens when game goes to PHaze NIGHT is that mafias talk and then decide a player to get out of the game
         */
        public void mafiaChat(){
           sendToMafias(ANSI_YELLOW+"you are part of the mafiaTeam.\nyou have 3 minutes to make a decision.\nwhen You came into a conclusion," +
                   "send \"MAFFINISH\""+ANSI_RESET);
            long start = System.currentTimeMillis();
            long finish = start+ 3*60*1000;
            String tst ;
            while(System.currentTimeMillis()<=finish){
                try {
                    tst = (String) objectInputStream.readObject();
                    if (playerByName(name).getRole() instanceof Mafia)
                    {
                        sendToMafias(name+" : "+tst);
                        if (tst.equals("MAFFINISH"))
                            break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }

        }

        /**
         * this method is specified to kill a player when its night and mafias have the shot
         */
        private void mafiaAttack(){
            String tst ;
            long start = System.currentTimeMillis();
            long finish = start+40*1000;
            while(System.currentTimeMillis()<=finish) {
                if (gameManager.isGodFatherAlive()) {
                    for (int i = 0; i < gameManager.getPlayers().size(); i++) {
                        if (gameManager.getPlayers().get(i).getRole() instanceof GodFather)
                            sendToClient(ANSI_YELLOW + "HI GODFATHER ,enter the name of player you wanna kill this night\nWhen done enter\"ATTACKOFF\"" + ANSI_RESET, i);
                    }
                    try {
                        tst = (String) objectInputStream.readObject();
                        if(playerByName(name).getRole() instanceof GodFather)
                        {
                            if (nameExist(tst))
                            {
                                Player p = playerByName(tst);
                                p.setAlive(false);
                                p.setCanChat(false);
                                gameManager.getInterval().add(p);
                            }
                            else if(tst.equals("ATTACKOFF"))
                                break;

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                else if (gameManager.isLecterAlive()) {
                    for (int i = 0; i < gameManager.getPlayers().size(); i++) {
                        if (gameManager.getPlayers().get(i).getRole() instanceof Lecter)
                            sendToClient(ANSI_YELLOW + "HI LECTER ,since the godfather is dead you have to choose the player to kill,\nenter the name of player you wanna kill this night\nWhen done enter\"ATTACKOFF\"" + ANSI_RESET, i);
                    }
                    try {
                        tst =(String) objectInputStream.readObject();
                        if(playerByName(name).getRole() instanceof Lecter)
                        {
                            if (nameExist(tst))
                            {
                                Player p = playerByName(tst);
                                p.setAlive(false);
                                p.setCanChat(false);
                                gameManager.getInterval().add(p);
                            }
                            else if(tst.equals("ATTACKOFF"))
                                break;

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                } else {
                    for (int i = 0; i < gameManager.getPlayers().size(); i++) {
                        if (gameManager.getPlayers().get(i).getRole() instanceof SimpleMafia)
                            sendToClient(ANSI_YELLOW + "HI Random simple mafia ,since the godfather is dead you have to choose the player to kill,\nenter the name of player you wanna kill this night\nWhen done enter\"ATTACKOFF\"" + ANSI_RESET, i);
                    }
                    try {
                        tst =(String) objectInputStream.readObject();
                        if(playerByName(name).getRole() instanceof SimpleMafia)
                        {
                            if (nameExist(tst))
                            {
                                Player p = playerByName(tst);
                                p.setAlive(false);
                                p.setCanChat(false);
                                gameManager.getInterval().add(p);
                            }
                            else if(tst.equals("ATTACKOFF"))
                                break;

                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        public void lecterSave(){
            String tst;
            if(gameManager.isLecterAlive())
            {
                for (int i = 0; i <gameManager.getPlayers().size() ; i++) {
                    if (gameManager.getPlayers().get(i).getRole() instanceof Lecter)
                    {
                        sendToClient(ANSI_YELLOW+"HI LECTER,time to save someone of your team\nNOTES:1)dont write a name that doesnt exist\n" +
                                "2)dont save a player that is not a part of your team\n" +
                                "3)be sure to enter\"MAFSAVE\" after youre done!!\n" +
                                "4)you have 1 minutes to do it" +
                                "5))))GOOD LUCK!!!!"+ANSI_RESET,i);
                    }
                }
                try {
                    long start = System.currentTimeMillis();
                    long finish = start+2*60*1000;
                    while (System.currentTimeMillis()<=finish) {
                        tst = (String) objectInputStream.readObject();
                        if (playerByName(name).getRole() instanceof Lecter) {
                            if (nameExist(tst)) {
                                if (playerByName(tst).getRole() instanceof Mafia) {
                                    if (playerByName(tst).getRole() instanceof Lecter) {
                                        if (!((Lecter) playerByName(tst).getRole()).isSelfSaved()) {
                                            ((Lecter) playerByName(tst).getRole()).setSelfSaved(true);

                                        }
                                    }
                                    ((Mafia) playerByName(tst).getRole()).setSavedByLecter(true);

                                }
                            } else if (tst.equals("MAFSAVE"))
                                break;
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }


            }
        }

        /**
         * this method will start working right after DAY Phaze and the CHATROOM time ends
         * and is a voting system for players to kick out any other player
         *
         * and if we end up to a tie it wont do anything
         */
        public void voting(){

            if (gameManager.isGameShelf()) {
                System.out.println("PHAZE : VOTING");
                sendToAll(ANSI_BLUE + "Entered Voting Area \nenter the name of who U think is Mafia\n" +
                        " Note not to vote for yourself or a wrong name \n" +
                        "if you dont want to vote anyone off,just type \"NOVOTE\"\n" +
                        "\n yo can change your vote as many times as you demand." +
                        "\nwhen you're sure about your vote please enter \"DONE\"" +
                        "----you have 30 seconds---" + ANSI_RESET);
            }
            for (int i = 0; i <clients.size() ; i++) {
                sendToClient(gameManager.remainingPlayers(i),i);
            }
            String tst="";
            try {
                long start = System.currentTimeMillis();
                long finish = start + 30*1000;
                while(System.currentTimeMillis()<=finish) {
                    tst = (String) objectInputStream.readObject();
                    gameManager.voteInit();
                    if (tst.equals(name)){
                        gameManager.getVotes().put(playerByName(name), "INVALID");
                        sendToAll(name+" voted to himself "+ANSI_BLACK+"  ///INVALIDVOTE///"+ANSI_RESET);
                    }
                    else if (nameExist(tst)){
                        gameManager.getVotes().put(playerByName(name), tst);
                        sendToAll(name+" voted --> "+tst);
                    }
                    else if (tst.equals("NOVOTE")){
                        gameManager.getVotes().put(playerByName(name), tst);
                        sendToAll(name+" didn't vote!!!!!!!");
                    }
                    else if(tst.equals("DONE"))
                    {
                        sendToAll(name+" : "+ANSI_BLUE+tst+ANSI_RESET);
                        break;
                    }
                    else{
                        gameManager.getVotes().put(playerByName(name), "INVALID");
                        sendToAll(name+" made a false vote "+ANSI_BLACK+"  ///INVALIDVOTE///"+ANSI_RESET);
                    }
                }
                if(gameManager.allHaveVoted()){
                Player resultPlayer = gameManager.votingSystem();
                    if (resultPlayer != null)
                    {
                        if (isMayorAlive())
                            mayorDecision(resultPlayer);
                        else
                        {
                            kickPlayer(resultPlayer);
                        }


                    }

                    sendToAll("The voting has ended");
                }

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

        /**
         * searches among the players and return the one with the same name as parameter
         * @param name
         * @return
         */
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
                if(p.getUsername().equals(name)&& p.isAlive()) {
                    exists = true;
                    break;
                }
            return exists;
        }

        /**
         * this method will get what mayor decides about the voting results and apply them
         * @param player
         */
       private void mayorDecision(Player player)
       {
            for (int i = 0;i<gameManager.getPlayers().size();i++)
            {
                if ((gameManager.getPlayers().get(i).getRole() instanceof Mayor))
                {
                        sendToClient("the most voted player is: "+player.getUsername()+"\n do you mind to cancel the voting?",i);
                    try {
                        String response = (String)objectInputStream.readObject();
                        if(playerByName(name).getRole() instanceof Mayor)
                        {
                            if (!response.equals("yes"))
                            {
                                kickPlayer(player);
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

        /**
         * this method checks if mayor is alive so we can use his action in agme
         * @return
         */
       private boolean isMayorAlive(){
            boolean isAlive = false;
            for (Player p :gameManager.getPlayers())
                if ((p.getRole() instanceof Mayor)&& p.isAlive())
                {
                    isAlive = true;
                    break;
                }
            return isAlive;
       }

        /**
         * this method kicks(remove,kill,...) a player out of the game
         * @param player
         */
       private void kickPlayer(Player player){
           player.setAlive(false);
           player.setCanChat(false);
           gameManager.getOutPlayers().add(player);
       }

    }
}
