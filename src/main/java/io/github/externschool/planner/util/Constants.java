package io.github.externschool.planner.util;

import java.util.Locale;

public final class Constants {
    public static final String UK_GENDER_MALE = "чол.";
    public static final String UK_GENDER_FEMALE = "жін.";

    public static final String UK_GRADE_LEVEL_NOT_DEFINED = "Не визначено";
    public static final String UK_GRADE_LEVEL_1 = "1 клас";
    public static final String UK_GRADE_LEVEL_2 = "2 клас";
    public static final String UK_GRADE_LEVEL_3 = "3 клас";
    public static final String UK_GRADE_LEVEL_4 = "4 клас";
    public static final String UK_GRADE_LEVEL_5 = "5 клас";
    public static final String UK_GRADE_LEVEL_6 = "6 клас";
    public static final String UK_GRADE_LEVEL_7 = "7 клас";
    public static final String UK_GRADE_LEVEL_8 = "8 клас";
    public static final String UK_GRADE_LEVEL_9 = "9 клас";
    public static final String UK_GRADE_LEVEL_10 = "10 клас";
    public static final String UK_GRADE_LEVEL_11 = "11 клас";
    public static final String UK_GRADE_LEVEL_12 = "12 клас";

    public static final String UK_FORM_INVALID_KEY_MESSAGE = "Введено недійсний ключ!";
    public static final String UK_FORM_VALIDATION_ERROR_MESSAGE = "Помилка вводу даних!";
    public static final String UK_FORM_VALIDATION_ERROR_SUBJECT_MESSAGE = "Будь ласка, введіть назву нового предмета!";

    public static final String UK_COURSE_NO_TITLE = "Назва курсу відсутня";
    public static final String UK_COURSE_NO_TEACHER = "Не призначений";

    public static final String UK_EVENT_TYPE_PERSONAL = "Індивідуальна консультація";

    public static final Locale LOCALE = new Locale("uk", "UA");

    private Constants() {
        throw new AssertionError();
    }
}
