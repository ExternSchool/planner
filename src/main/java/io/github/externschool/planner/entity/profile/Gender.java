package io.github.externschool.planner.entity.profile;

import static io.github.externschool.planner.util.Constants.UK_GENDER_FEMALE;
import static io.github.externschool.planner.util.Constants.UK_GENDER_MALE;

public enum Gender {

    MALE(1) {
        public String toString() {
            return UK_GENDER_MALE;
        }
    }, FEMALE(2) {
        public String toString() {
            return UK_GENDER_FEMALE;
        }
    };

    private int genderValue;


    Gender(int genderValue) {
        this.genderValue = genderValue;
    }

    public int getGenderValue() {
        return genderValue;
    }
}
