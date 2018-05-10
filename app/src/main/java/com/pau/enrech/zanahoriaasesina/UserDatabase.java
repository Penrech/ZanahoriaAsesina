package com.pau.enrech.zanahoriaasesina;

import android.util.Log;

public class UserDatabase {
    private static int cont =3;
    private static int numUsers= 3;

    public static int getNumUsers() {
        return numUsers;
    }

    public static User getUserFromId(String id){
        if (id.equals("Pau")){
            return new User("Pau",
                    "Enrech",
                    "D'asti cap alla",
                    66655444,
                    23,
                    User.State.ACTIVE,
                    -1,
                    "Laura");
        }
        if (id.equals("Laura")){
            return new User("Laura",
                    "Gonzalez",
                    "CITM",
                    66655444,
                    22,
                    User.State.ACTIVE,
                    -1,
                    "Daniel");
        }
        if (id.equals("Daniel")){
            return new User("Daniel",
                    "Rios",
                    "UPC",
                    66655444,
                    23,
                    User.State.ACTIVE,
                    -1,
                    "Pau");
        }
        return null;
    }

    public static User eliminateUser(User killer, User target){
        target.state = target.state.ELIMINATED;
        target.rank = cont;
        Log.d("InfoRanking", target.name +" ha quedado en "+target.rank+ " lugar.");
        cont--;
        if (target.target == killer.name) {
            assert(cont == 1);
            killer.rank = cont;
            return null;
        }
        return UserDatabase.getUserFromId(target.target);
    }
}
