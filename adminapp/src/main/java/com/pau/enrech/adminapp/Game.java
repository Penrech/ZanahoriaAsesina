package com.pau.enrech.adminapp;

public class Game {
    public enum gStates{INACTIVE,REGISTRATING,REGISTRATION,ACTIVATING,ACTIVE,OVER,REESTABLISHING};

    public int totales;
    public int activos;
    public gStates estado;

    public Game(){};
    public Game(int totales, int activos, gStates estado) {
        this.totales = totales;
        this.activos = activos;
        this.estado = estado;
    }
}
