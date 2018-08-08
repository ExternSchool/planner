package io.github.externschool.planner.entity.profile;

public enum Gender {

    MALE(1) {
        public String toString() {
            return "чол.ст.";
        }
    }, FEMALE(2) {
        public String toString() {
            return "жін.ст.";
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
