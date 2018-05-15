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
        target.active = target.active.ELIMINATED;
        target.ranking = cont;
        Log.d("InfoRanking", target.nom +" ha quedado en "+target.ranking + " lugar.");
        cont--;
        if (target.target == killer.nom) {
            assert(cont == 1);
            killer.ranking = cont;
            return null;
        }
        return UserDatabase.getUserFromId(target.target);
    }
}
