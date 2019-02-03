package io.github.externschool.planner.entity;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import static io.github.externschool.planner.util.Constants.UK_GRADE_LEVEL_1;
import static io.github.externschool.planner.util.Constants.UK_GRADE_LEVEL_10;
import static io.github.externschool.planner.util.Constants.UK_GRADE_LEVEL_11;
import static io.github.externschool.planner.util.Constants.UK_GRADE_LEVEL_2;
import static io.github.externschool.planner.util.Constants.UK_GRADE_LEVEL_3;
import static io.github.externschool.planner.util.Constants.UK_GRADE_LEVEL_4;
import static io.github.externschool.planner.util.Constants.UK_GRADE_LEVEL_5;
import static io.github.externschool.planner.util.Constants.UK_GRADE_LEVEL_6;
import static io.github.externschool.planner.util.Constants.UK_GRADE_LEVEL_7;
import static io.github.externschool.planner.util.Constants.UK_GRADE_LEVEL_8;
import static io.github.externschool.planner.util.Constants.UK_GRADE_LEVEL_9;
import static io.github.externschool.planner.util.Constants.UK_GRADE_LEVEL_NOT_DEFINED;

public enum GradeLevel {
    LEVEL_NOT_DEFINED(0){
        public String toString() {
            return UK_GRADE_LEVEL_NOT_DEFINED;
        }
    }, LEVEL_1(1) {
        public String toString() {
            return UK_GRADE_LEVEL_1;
        }
    }, LEVEL_2(2) {
        public String toString() {
            return UK_GRADE_LEVEL_2;
        }
    }, LEVEL_3(3) {
        public String toString() {
            return UK_GRADE_LEVEL_3;
        }
    }, LEVEL_4(4) {
        public String toString() {
            return UK_GRADE_LEVEL_4;
        }
    }, LEVEL_5(5) {
        public String toString() {
            return UK_GRADE_LEVEL_5;
        }
    }, LEVEL_6(6) {
        public String toString() {
            return UK_GRADE_LEVEL_6;
        }
    }, LEVEL_7(7) {
        public String toString() {
            return UK_GRADE_LEVEL_7;
        }
    }, LEVEL_8(8) {
        public String toString() {
            return UK_GRADE_LEVEL_8;
        }
    }, LEVEL_9(9) {
        public String toString() {
            return UK_GRADE_LEVEL_9;
        }
    }, LEVEL_10(10) {
        public String toString() {
            return UK_GRADE_LEVEL_10;
        }
    }, LEVEL_11(11) {
        public String toString() {
            return UK_GRADE_LEVEL_11;
        }
    };

    private int value;
    private static final Map<Integer, GradeLevel> map = Arrays.stream(GradeLevel.values())
            .collect(Collectors.toMap(level -> level.value, level -> level));

    GradeLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    public static GradeLevel valueOf(int i) {
        return map.get(i);
    }
}