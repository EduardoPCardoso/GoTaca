package com.gotaca.gotaca;

public class Station {
    private String name;
    private String adress;
    private GeoPoint position;
    private com.google.firebase.firestore.GeoPoint coord;
    private String gasolinac;
    private String etanol;
    private String updateDate, whoUpdated;
    private long hora;

    private String distributor;
    private String stationId;

    public String getStationId() {return stationId;}
    public void setStationId(String stationId) {this.stationId = stationId;}

    public com.google.firebase.firestore.GeoPoint getCoord() {return coord;}
    public void setCoord(com.google.firebase.firestore.GeoPoint coord) {this.coord = coord;}

    public long getHora() {return hora;}
    public void setHora(long hora) {this.hora = hora;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public String getAdress() {return adress;}
    public void setAdress(String adress) {this.adress = adress;}

    public GeoPoint getPosition() {return position;}
    public void setPosition(GeoPoint position) {this.position = position;}

    public String getGasolinac() {return gasolinac;}
    public void setGasolinac(String gasolinac) {this.gasolinac = gasolinac;}

    public String getEtanol() {return etanol;}
    public void setEtanol(String etanol) {this.etanol = etanol;}

    public String getUpdateDate() {return updateDate;}
    public void setUpdateDate(String updateDate) {this.updateDate = updateDate;}

    public String getWhoUpdated() {return whoUpdated;}
    public void setWhoUpdated(String whoUpdated) {this.whoUpdated = whoUpdated;}

    /*public String getDistributor() {return distributor;}
    public void setDistributor(Distributor distributor) {this.distributor = distributor;};*/

    @Override
    public String toString(){
        return "Station{" +
                "name='" + name + '\'' +
                "adress='" + adress + '\'' +
                "position='" + position + '\'' +
                "gasolinac='" + gasolinac + '\'' +
                "etanol='" + etanol + '\'' +
                "distributor='" + distributor + '\'' +
                '}';
    }

    public Station(String stationId, com.google.firebase.firestore.GeoPoint coord, String name, String adress,
                   String gasolinac, String updateDate, String whoUpdated ,String etanol/*, String distributor*/) {

        setStationId(stationId);
        hora = System.currentTimeMillis();
        //position = new GeoPoint(longitude, latitude);
        this.name = name;
        this.adress = adress;
        this.gasolinac = gasolinac;
        this.etanol = etanol;
        this.updateDate = updateDate;
        this.whoUpdated = whoUpdated;

        //this.coord = getCoord();
        setCoord(coord);

        //this.distributor = distributor;
        //this.distributor = Distributor.BRANCA;
    }

    /*
    public Station(String name, String adress, String gasolinac, String etanol, String distributor){
        hora = System.currentTimeMillis();
        position = new GeoPoint(0,0);
        this.distributor = Distributor.BRANCA;
    }*/
}
