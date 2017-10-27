package com.api.model;

/**
 * Created by prashant.patel on 6/1/2017.
 */

public class DataBindingModel {
    public final String firstName;
    public final String lastName;
    public DataBindingModel(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
    public String getFirstName() {
        return this.firstName;
    }
    public String getLastName() {
        return this.lastName;
    }
}
