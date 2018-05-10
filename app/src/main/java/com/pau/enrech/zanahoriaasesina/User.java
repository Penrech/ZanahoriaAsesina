package com.pau.enrech.zanahoriaasesina;

public class User {
    public enum State{ELIMINATED,ACTIVE};

    public String id;
    public String name;
    public String surname;
    public String penya;
    public int phone;
    public int age;
    public State state;
    public int rank;
    public String target;

    public User(String name, String surname, String penya, int phone, int age, State state, int rank, String target) {
        this.name = name;
        this.surname = surname;
        this.penya = penya;
        this.phone = phone;
        this.age = age;
        this.state = state;
        this.rank = rank;
        this.target = target;
    }
}
