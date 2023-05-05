package ru.praktikum.qa_scooter.api.util;

import ru.praktikum.qa_scooter.api.model.Courier;

public class CourierGenerator {
    public static Courier getDefault() {
        return new Courier("004denisill", "0019", "den07");
    }

    public static Courier getDefaultWithoutPassword() {
        return new Courier("004denisill", "", "den07");
    }

    public static Courier getRegisteredCourier() {
        return new Courier("002qwerty", "456098", "bob");
    }

    public static Courier getWrong() {
        return new Courier("002qwerty11", "11111", "bob");
    }
}
