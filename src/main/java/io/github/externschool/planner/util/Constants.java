package io.github.externschool.planner.util;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class Constants {
    public static final Map<String, String> UK_ROLE_NAMES;
    static {
        UK_ROLE_NAMES = new HashMap<>();
        UK_ROLE_NAMES.put("ROLE_ADMIN", "Адміністратор");
        UK_ROLE_NAMES.put("ROLE_GUEST", "Відвідувач");
        UK_ROLE_NAMES.put("ROLE_OFFICER", "Посадовець");
        UK_ROLE_NAMES.put("ROLE_STUDENT", "Учень");
        UK_ROLE_NAMES.put("ROLE_TEACHER", "Вчитель");
    }

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
    public static final String UK_FORM_VALIDATION_ERROR_EVENT_TYPE_MESSAGE = "Будь ласка, введіть назву нового типу події!";

    public static final String UK_COURSE_NO_TITLE = "Назва курсу відсутня";
    public static final String UK_COURSE_NO_TEACHER = "Не призначений";

    public static final String UK_EVENT_TYPE_PERSONAL = "Індивідуальна консультація";
    public static final String UK_EVENT_TYPE_GROUP = "Групова консультація";
    public static final String UK_EVENT_TYPE_GRADE_BOOK = "Видача залікових книжок";
    public static final String UK_EVENT_TYPE_PSYCHOLOGIST = "Співбесіда з психологом";
    public static final String UK_EVENT_TYPE_PRINCIPAL = "Прийом директора";
    public static final String UK_EVENT_TYPE_TEST = "Написання контрольної роботи";
    public static final String UK_EVENT_TYPE_COUNCIL = "Педагогічна рада";

    public static final Locale LOCALE = new Locale("uk", "UA");

    public static final LocalDate FIRST_MONDAY_OF_EPOCH = LocalDate.of(1970, 1, 5);

    public static final String APPOINTMENT_CANCELLATION_SUBJECT = "Відміна зустрічі ";
    public static final String APPOINTMENT_CANCELLATION_TEXT =
            "Вибачте, але в зв'язку з поважними причинами скасовано Вашу зустріч ";
    //TODO Add ACTUAL LINK to this service
    public static final String APPOINTMENT_CANCELLATION_PROPOSAL =
            "Будь ласка, скористайтеся цим посиланням для призначення нової зустрічі в зручний для Вас час: "
            + "https://extern.kiev.ua";
    public static final String APPOINTMENT_CANCELLATION_SIGNATURE = "З повагою,\nАдміністрація Школи Екстернів";

    private Constants() {
        throw new AssertionError();
    }
}
