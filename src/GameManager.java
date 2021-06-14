import Roles.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class GameManager {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    private static final int MAXUSERS = 3;
    private ArrayList<String> readySets;
    private ArrayList<Player> players;
    private ArrayList<Role> roles;
    private ArrayList<Player> outPlayers;
    private boolean gameShelf;
    private HashMap<Player,String> votes;

    public GameManager() {
        readySets = new ArrayList<>();
        players = new ArrayList<>();
        roles = new ArrayList<>();
        outPlayers = new ArrayList<>();
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
     * this inner method counts the mafias left in game
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
                    stringBuilder.append(ANSI_BLACK).append(i).append(")").append(p.getUsername()).append('\n').append(ANSI_RESET);
                else
                stringBuilder.append(i).append(")").append(p.getUsername()).append('\n');

                i++;
            }
        return stringBuilder.toString();
    }

    /**
     * this method is supposed to be the main method to do the tasks while game is in phaze VOTING
     */
    public Player votingSystem(){
        ArrayList<Player> alivePlayers = getAlivePlayers();
        ArrayList<Vote> votes = new ArrayList<>();
        for (Player p: alivePlayers)
        {
            votes.add(new Vote(p));
        }
        voteCount(votes);
        Vote mostVote = sort(votes);
        Player result;
        if (sameCountCheck(mostVote,votes))
            result=null;
        else
            result = mostVote.getPlayer();

        return result;
    }


    private void voteCount(ArrayList<Vote> voteset){
        for (Vote v:voteset)
        {
            for(Map.Entry<Player,String> entry :votes.entrySet())
            {
                    if (entry.getValue().equals(v.getPlayer().getUsername()))
                    v.addCount();
            }
        }
    }
    private Vote sort(ArrayList<Vote> voteset){
        Vote vote=null;
        for (Vote v: voteset)
        {
            for (Vote vote1:voteset)
            {
                if (v.getCount()>=vote1.getCount()){
                    vote = v;
                }
                else
                    vote = vote1;
            }
        }
        return vote;

    }
    private boolean sameCountCheck(Vote vote,ArrayList<Vote> voteset){
        boolean hasSameCount = false;
        for (Vote v:voteset){
            if (v.getCount()==vote.getCount())
            {
                hasSameCount = true;
                break;
            }
        }
        return  hasSameCount;
    }

    /**
     * getter for Hashmap Votes
     * @return
     */
    public HashMap<Player, String> getVotes() {
        return votes;
    }

    /**
     * this method makes a new hashmap once its called
     * we didnt initialize the hashmap in constructor
     * because whenever we need a votingList it should be empty to add some votes
     */
    public void voteInit(){
        votes = new HashMap<>();
    }
    private ArrayList<Player> getAlivePlayers(){
        ArrayList<Player> aliveMembers = new ArrayList<>();
        for (Player p:players)
            if(p.isAlive())
                aliveMembers.add(p);


         return aliveMembers;

    }
    public boolean allHaveVoted(){
        boolean voteFull = false;
            if (votes.size()== getAlivePlayers().size())
                voteFull = true;

        return voteFull;
    }

}
