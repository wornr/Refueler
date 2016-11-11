package pl.marek.refueler.database;

import java.io.Serializable;
import se.emilsjolander.sprinkles.Model;
import se.emilsjolander.sprinkles.annotations.AutoIncrement;
import se.emilsjolander.sprinkles.annotations.Column;
import se.emilsjolander.sprinkles.annotations.Key;
import se.emilsjolander.sprinkles.annotations.Table;

@Table("Cars")
public class Car extends Model implements Serializable {
    public Car() {
        id = 0;
        brand = "";
        model = "";
        registrationNumber = "";
        productionYear = 0;
        totalDistance = 0;
        fuelType = "";
    }

    public String toString()
    {
        return(brand + " " + model);
    }

    @Key
    @AutoIncrement
    @Column("id")
    private long id;

    @Column("brand")
    private String brand;

    @Column("model")
    private String model;

    @Column("registrationNumber")
    private String registrationNumber;

    @Column("productionYear")
    private int productionYear;

    @Column("totalDistance")
    private int totalDistance;

    @Column("fuelType")
    private String fuelType;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public int getProductionYear() {
        return productionYear;
    }

    public void setProductionYear(int productionYear) {
        this.productionYear = productionYear;
    }

    public int getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(int totalDistance) {
        this.totalDistance = totalDistance;
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType;
    }
}