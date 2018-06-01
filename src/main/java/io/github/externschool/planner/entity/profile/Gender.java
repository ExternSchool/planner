package io.github.externschool.planner.entity.profile;

public enum Gender {

    MALE(1), FEMALE(2); //for saving value in data base

    private int genderValue;


    Gender(int genderValue) {
        this.genderValue = genderValue;
    }

    public int getGenderValue() {
        return genderValue;
    }
}
