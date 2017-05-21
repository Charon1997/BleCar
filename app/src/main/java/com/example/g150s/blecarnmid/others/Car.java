package com.example.g150s.blecarnmid.others;

/**
 * Created by G150S on 2017/3/15.
 */

public class Car {

    private String carName;
    private String carAddress;

    public Car(String carName, String carMac) {
        this.carName = carName;
        this.carAddress = carMac;
    }

    public String getCarAddress() {
        return carAddress;
    }

    public void setCarAddress(String carMac) {
        this.carAddress = carMac;
    }

    public void setCarName(String name)
    {
        carName = name;
    }
    public String getCarName()
    {
        return carName;
    }
}
