package com.pau.enrech.adminapp;



public class User {
    public enum State{ELIMINATED,ACTIVE,LIMBO,WINNER};

    public String nom;
    public String cognom;
    public String penya;
    public int phone;
    public int age;
    public State active;
    public int ranking;
    public String target;

    public int getActive(){
        int active = 0;
        switch (this.active){
            case ACTIVE:
                active = R.color.colorGreenActive;
                break;
            case LIMBO:
                active= R.color.colorYellowLimbo;
                break;
            case WINNER:
                active = R.color.colorGreenActive;
                break;
            case ELIMINATED:
                active = R.color.colorRedEliminated;
                break;
        }
        return active;
    }

    public String getNomAp(){
        return String.format("%s %s",this.nom,this.cognom);
    }

    public User(){}
    public User(String nom, String surname, String penya, int phone, int age, State state, int rank, String target) {
        this.nom = nom;
        this.cognom = surname;
        this.penya = penya;
        this.phone = phone;
        this.age = age;
        this.active = state;
        this.ranking = rank;
        this.target = target;

    }
}
