import ChatRoom.RoleTag;

import java.util.Arrays;
import java.util.Collections;

public class Main {

    public static void main(String[] args) {

        String name;
        RoleTag arr[]=RoleTag.values();
        Collections.shuffle(Arrays.asList(arr));
        for (RoleTag rt: arr)
            name = String.valueOf(rt);
           // System.out.println(rt);
    }
}
