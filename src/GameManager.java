import Roles.*;

import java.util.ArrayList;
import java.util.Collections;

public class GameManager {
    private static final int MAXUSERS = 10;
    private ArrayList<Character> readySets;
    private ArrayList<Player> players;
    private ArrayList<Role> roles;

    public GameManager() {
        readySets = new ArrayList<>();
        players = new ArrayList<>();
        roles = new ArrayList<>();
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
    public  void AssignRoles(){
        initialRoles();
        shuffleRoles();
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
    public void addReadyState(Character ready){
        readySets.add(ready);
    }
    public boolean startAllowance(){
        boolean isAllowed = true;
        for (Character ch:readySets)
            if(ch !='y')
            {
                isAllowed = false;
                break;
            }
        return isAllowed;
    }
}
