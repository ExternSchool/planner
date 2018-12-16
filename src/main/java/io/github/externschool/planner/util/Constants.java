package io.github.externschool.planner.util;

import java.time.Duration;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class Constants {
    // role names to display in Ukrainian when selected
    public static final Map<String, String> UK_ROLE_NAMES;
    static {
        UK_ROLE_NAMES = new HashMap<>();
        UK_ROLE_NAMES.put("ROLE_ADMIN", "Адміністратор");
        UK_ROLE_NAMES.put("ROLE_GUEST", "Відвідувач");
        UK_ROLE_NAMES.put("ROLE_OFFICER", "Посадовець");
        UK_ROLE_NAMES.put("ROLE_STUDENT", "Учень");
        UK_ROLE_NAMES.put("ROLE_TEACHER", "Вчитель");
    }

    public static final Locale LOCALE = new Locale("uk", "UA");

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
    // error messages
    public static final String UK_FORM_INVALID_KEY_MESSAGE = "Введено недійсний ключ!";
    public static final String UK_FORM_VALIDATION_ERROR_MESSAGE = "Помилка вводу даних!";
    public static final String UK_FORM_VALIDATION_ERROR_SUBJECT_MESSAGE = "Будь ласка, введіть назву нового предмета!";
    public static final String UK_FORM_VALIDATION_ERROR_EVENT_TYPE_MESSAGE = "Будь ласка, введіть назву нового типу події!";
    public static final String UK_SUBSCRIBE_SCHEDULE_EVENT_ERROR_MESSAGE =
            "Вибачте, виникла помилка під час резервування. \nСпробуйте повторити пізніше";
    // default constants set when a new course created
    public static final String UK_COURSE_NO_TITLE = "Назва курсу відсутня";
    public static final String UK_COURSE_NO_TEACHER = "Не призначений";

    //TODO Test event types. Remove from Bootstrap Data Populator when DB migration finished
    public static final String UK_EVENT_TYPE_NOT_DEFINED = "Тип не визначений";
    public static final String UK_EVENT_TYPE_PERSONAL = "Індивідуальна консультація";
    public static final String UK_EVENT_TYPE_GROUP = "Групова консультація";
    public static final String UK_EVENT_TYPE_GRADE_BOOK = "Видача залікових книжок";
    public static final String UK_EVENT_TYPE_PSYCHOLOGIST = "Співбесіда з психологом";
    public static final String UK_EVENT_TYPE_PRINCIPAL = "Прийом директора";
    public static final String UK_EVENT_TYPE_TEST = "Написання контрольної роботи";

    public static final String UK_WEEK_DAYS_MONDAY = "Понеділок";
    public static final String UK_WEEK_DAYS_TUESAY = "Вівторок";
    public static final String UK_WEEK_DAYS_WEDNESDAY = "Середа";
    public static final String UK_WEEK_DAYS_THIRSDAY = "Четвер";
    public static final String UK_WEEK_DAYS_FRIDAY = "П'ятниця";

    // need to point start date for standard events schema
    public static final LocalDate FIRST_MONDAY_OF_EPOCH = LocalDate.of(1970, 1, 5);
    // period of time when it's already late to join as participant to an incoming event
    public static final Duration DAYS_BETWEEN_LATEST_RESERVE_AND_EVENT = Duration.ofDays(0);
    public static final Duration HOURS_BETWEEN_LATEST_RESERVE_AND_EVENT = Duration.ofHours(0);
    // mailing service text messages
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
