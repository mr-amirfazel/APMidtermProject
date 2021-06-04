import Roles.*;

import java.util.ArrayList;
import java.util.Collections;

public class GameManager {
    private ArrayList<Player> players;
    private ArrayList<Role> roles;

    public GameManager() {

    }

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
    private void shuffleRoles(){
        Collections.shuffle(roles);
    }

    public void addPlayer(Player player)
    {
        players.add(player);
    }
    public  void AssignRoles(){
        initialRoles();
        shuffleRoles();
        int i =0;
        for (Player p:players){
            p.setRole(roles.get(i));
            i++;
        }
    }
}
