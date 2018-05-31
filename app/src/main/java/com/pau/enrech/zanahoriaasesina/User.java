package com.pau.enrech.zanahoriaasesina;

public class User {
    public enum State{ELIMINATED,ELIMINATING,ACTIVE,LIMBO,WINNER};

    public String nom;
    public String cognom;
    public String penya;
    public int phone;
    public int age;
    public State active;
    public int ranking;
    public String target;
    public String img;

    public String getNomAp(){
        return String.format("%s %s",this.nom,this.cognom);
    }

    public User(){}
    public User(String nom, String surname, String penya, int phone, int age, State state, int rank, String target,String img) {
        this.nom = nom;
        this.cognom = surname;
        this.penya = penya;
        this.phone = phone;
        this.age = age;
        this.active = state;
        this.ranking = rank;
        this.target = target;
        this.img = img;

    }
}
