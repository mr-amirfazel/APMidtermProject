import ChatRoom.RoleTag;

import java.util.Arrays;
import java.util.Collections;

public class Main {

    public static void main(String[] args) {

        RoleTag arr[]=RoleTag.values();
        Collections.shuffle(Arrays.asList(arr));
        for (RoleTag rt: arr)
            System.out.println(rt);
    }
}
