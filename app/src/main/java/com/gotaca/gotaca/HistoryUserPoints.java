package com.gotaca.gotaca;

public class HistoryUserPoints {

    private String reason;
    private String date;
    private String tacas;
    private String pointsId;

    public String getPointsId() {return pointsId;}
    public void setPointsId(String pointsId) {this.pointsId = pointsId;}

    public String getReason() {return reason;}
    public void setReason(String reason) {this.reason = reason;}

    public String getDate() {return date;}
    public void setDate(String date) {this.date = date;}

    public String getTacas() {return tacas;}
    public void setTacas(String tacas) {this.tacas = tacas;}


    public HistoryUserPoints(String pointsId, String date, long tacas, String reason) {
        setPointsId(pointsId);
        this.date = date;
        this.tacas = String.valueOf(tacas);
        this.reason = reason;
    }

}
