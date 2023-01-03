package com.gotaca.gotaca;

public enum Distributor {
    SHELL("Shell", R.drawable.shell_logo),
    ALE("Ale", R.drawable.ale_logo),
    PETROBRAS("BR", R.drawable.br_logo),
    BRANCA("Bandeira Branca", R.drawable.bandeira_branca),
    IPIRANGA("Ipiranga", R.drawable.ipiranga_logo);

    private final String texto;
    private final int recurso;

    Distributor (String texto, int recurso){
        this.texto = texto;
        this.recurso = recurso;
    }

    public String getTexto(){return texto;}
    public int getRecurso(){return recurso;}
}
