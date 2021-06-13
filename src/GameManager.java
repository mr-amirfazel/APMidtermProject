import Roles.*;

import java.util.ArrayList;
import java.util.Collections;

public class GameManager {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    private static final int MAXUSERS = 3;
    private ArrayList<String> readySets;
    private ArrayList<Player> players;
    private ArrayList<Role> roles;
    private boolean gameShelf;

    public GameManager() {
        readySets = new ArrayList<>();
        players = new ArrayList<>();
        roles = new ArrayList<>();
        initialRoles();
        shuffleRoles();
        this.gameShelf = false;
    }

    /**
     * getter for the gameShelf
     * @return
     */
    public boolean isGameShelf() {
        return gameShelf;
    }

    /**
     *setter for the gameShelf
     * @param gameShelf
     */
    public void setGameShelf(boolean gameShelf) {
        this.gameShelf = gameShelf;
    }

    /**
     * creates an instance of every role
     */
    private void initialRoles(){
        roles.add(new Detective());
        roles.add(new DieHard());
        roles.add(new Doctor());
        roles.add(new GodFather());
        roles.add(new Lecter());
        roles.add(new Mayor());
        roles.add(new Psychiatrist());
        roles.add(new SimpleCitizen());
        roles.add(new SimpleMafia());
        roles.add(new Sniper());
    }

    /**
     * this method uses Collection class and the shuffle method to shuffle the roles inside roles arrayList
     */
    private void shuffleRoles(){
        Collections.shuffle(roles);
    }

    /**
     * this methods adds a new plyer to the array list of players
     * @param player
     */
    public void addPlayer(Player player)
    {
        players.add(player);
    }

    /**
     * this method:
     * shuffles the roles list
     * and then give each player a role
     */
    public  void assignRoles(){

        int i =0;
        for (Player p:players){
            p.setRole(roles.get(i));
            i++;
        }
    }

    /**
     * this method adds a character data type as y or n
     * to show if a client is ready yo start or not
     * @param ready
     */
    public void addReadyState(String ready){
        readySets.add(ready);
    }
    public boolean startAllowance(){
        boolean isAllowed = false;
        if (readySets.size()==MAXUSERS)
            isAllowed=true;
        return isAllowed;
    }

    /**
     *
     * @return
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * this method determines when the game is over
     * @return
     */
    public boolean endGame(){
        boolean endgame = false;
            if ((mafiaCounter() ==citizenCounter())||(mafiaCounter()==0))
                endgame = true;

        return endgame;
    }

    /**
     * this inner mwthod counts the mafias left in game
     * @return
     */
    private int mafiaCounter(){
        int i =0;
        for (Player p:players)
            if ((p.getRole() instanceof Mafia)&&(p.isAlive()))
                i++;
    return i;
    }

    /**
     * this inner method counts the citizens left
     * @return
     */
    private int citizenCounter(){
        int i =0;
        for (Player p:players)
            if (!(p.getRole() instanceof Mafia) && p.isAlive())
                i++;
        return i;
    }
    public String gameOverStatement(){
        String txt="";
        if(mafiaCounter()==citizenCounter())
            txt= "Mafia Won The Game";
        else if (mafiaCounter()==0)
            txt = "Citizens Won The Game";

        return txt;
    }
    public String remainingPlayers(int index){
        Player player = players.get(index);
        int i =1;
        StringBuilder stringBuilder = new StringBuilder();
        for (Player p:players)
            if (p.isAlive())
            {
                if (p.equals(player))
                    stringBuilder.append(ANSI_BLACK).append(i).append(")").append(p.getUsername()).append(ANSI_RESET);
                else
                stringBuilder.append(i).append(")").append(p.getUsername()).append('\n');

                i++;
            }
        return stringBuilder.toString();
    }

}
