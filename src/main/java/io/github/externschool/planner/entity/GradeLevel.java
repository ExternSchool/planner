package io.github.externschool.planner.entity;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum GradeLevel {
    LEVEL_NOT_DEFINED(0){
        public String toString() {
            return "Не визначено";
        }
    }, LEVEL_1(1) {
        public String toString() {
            return "1й клас";
        }
    }, LEVEL_2(2) {
        public String toString() {
            return "2й клас";
        }
    }, LEVEL_3(3) {
        public String toString() {
            return "3й клас";
        }
    }, LEVEL_4(4) {
        public String toString() {
            return "4й клас";
        }
    }, LEVEL_5(5) {
        public String toString() {
            return "5й клас";
        }
    }, LEVEL_6(6) {
        public String toString() {
            return "6й клас";
        }
    }, LEVEL_7(7) {
        public String toString() {
            return "7й клас";
        }
    }, LEVEL_8(8) {
        public String toString() {
            return "8й клас";
        }
    }, LEVEL_9(9) {
        public String toString() {
            return "9й клас";
        }
    }, LEVEL_10(10) {
        public String toString() {
            return "10й клас";
        }
    }, LEVEL_11(11) {
        public String toString() {
            return "11й клас";
        }
    }, LEVEL_12(12) {
        public String toString() {
            return "12й клас";
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