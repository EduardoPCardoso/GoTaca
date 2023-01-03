package com.gotaca.gotaca;

public class UserGoTacaBase {

    private String name;
    private String point;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getPoint() {return point;}
    public void setPoint(String point) {
        this.point = point;
    }

    @Override
    public String toString(){
        return "Station{" +
                "name='" + name + '\'' +
                "point='" + point + '\'' +'}';
    }

    public UserGoTacaBase (String name, Long point) {
        this.name = name;
        this.point = String.valueOf(point);
    }
}