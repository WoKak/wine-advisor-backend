package pw.mssql.wineadvisor.model;

import lombok.*;

public class Wine {

    private int alcoholPercentage;
    private String grapeVariety;
    private String wineType;
    private String wineDryness;
    private String wineOrigin;
    private String purpose;

    public Wine(int alcoholPercentage, String grapeVariety, String wineType, String wineDryness, String wineOrigin, String purpose) {
        this.alcoholPercentage = alcoholPercentage;
        this.grapeVariety = grapeVariety;
        this.wineType = wineType;
        this.wineDryness = wineDryness;
        this.wineOrigin = wineOrigin;
        this.purpose = purpose;
    }

    public Wine() {
    }

    public int getAlcoholPercentage() {
        return alcoholPercentage;
    }

    public void setAlcoholPercentage(int alcoholPercentage) {
        this.alcoholPercentage = alcoholPercentage;
    }

    public String getGrapeVariety() {
        return grapeVariety;
    }

    public void setGrapeVariety(String grapeVariety) {
        this.grapeVariety = grapeVariety;
    }

    public String getWineType() {
        return wineType;
    }

    public void setWineType(String wineType) {
        this.wineType = wineType;
    }

    public String getWineDryness() {
        return wineDryness;
    }

    public void setWineDryness(String wineDryness) {
        this.wineDryness = wineDryness;
    }

    public String getWineOrigin() {
        return wineOrigin;
    }

    public void setWineOrigin(String wineOrigin) {
        this.wineOrigin = wineOrigin;
    }

    public String getPurpose() {
        return purpose;
    }

    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
}
