package io.github.externschool.planner.entity;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public enum GradeLevel {
    NOT_DEFINED(0), LEVEL_1(1), LEVEL_2(2), LEVEL_3(3), LEVEL_4(4), LEVEL_5(5), LEVEL_6(6),
    LEVEL_7(7), LEVEL_8(8), LEVEL_9(9), LEVEL_10(10), LEVEL_11(11), LEVEL_12(12);

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