package com.wrpinheiro.deadcodedetection.model;

/**
 * Created by wrpinheiro on 3/25/17.
 */
public enum Language {
    ADA("ada"), CPP("c++"), FORTRAN("fortran"), JAVA("java");

    private final String strValue;

    Language(String strValue) {
        this.strValue = strValue;
    }

    public String getStrValue() {
        return this.strValue;
    }

    public static Language fromString(String name) {
        return valueOf(name.toUpperCase());
    }
}
