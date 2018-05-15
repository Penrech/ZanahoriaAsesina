package com.pau.enrech.zanahoriaasesina;

public class User {
    public enum State{ELIMINATED,ACTIVE};

    public String nom;
    public String cognom;
    public String penya;
    public int phone;
    public int age;
    public State active;
    public int ranking;
    public String target;

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
