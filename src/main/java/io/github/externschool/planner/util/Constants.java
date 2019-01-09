package io.github.externschool.planner.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
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

    public static final String SCHOOL_PHONE_NUMBER = "(044) 257-10-28";

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
    public static final String UK_FORM_VALIDATION_ERROR_MESSAGE = "Виникла помилка вводу даних! \n" +
            "Перевірте отриманий результат і за необхідності спробуйте повторити пізніше";
    public static final String UK_FORM_VALIDATION_ERROR_SUBJECT_MESSAGE = "Будь ласка, введіть назву нового предмета!";
    public static final String UK_FORM_VALIDATION_ERROR_EVENT_TYPE_MESSAGE = "Будь ласка, введіть назву нового типу події!";
    public static final String UK_SUBSCRIBE_SCHEDULE_EVENT_ERROR_MESSAGE =
            "Вибачте, виникла помилка під час резервування. \nСпробуйте повторити пізніше";
    public static final String UK_UNSUBSCRIBE_SCHEDULE_EVENT_USER_NOT_FOUND_ERROR_MESSAGE =
            "За користувачем не зареєстровано вказану подію. \nСпробуйте ще або зверніться до адміністратора";
    public static final String UK_USER_ACCOUNT_NOT_CONFIRMED =
            "Користувач з вказаною адресою не знайдений або обліковий запис не активований!\n" +
            "Після реєстрації активуйте обліковий запис за посиланням, яке надіслано на вказану при реєстрації адресу.";

    // default constants set when a new course created
    public static final String UK_COURSE_NO_TITLE = "Назва курсу відсутня";
    public static final String UK_COURSE_NO_TEACHER = "Не призначений";
    public static final String UK_COURSE_ADMIN_IN_CHARGE = "Адміністратор";
    //T standard event types
    public static final String UK_EVENT_TYPE_NOT_DEFINED = "Тип не визначений";
    public static final String UK_EVENT_TYPE_PERSONAL = "Індивідуальна консультація";
    public static final String UK_EVENT_TYPE_GROUP = "Групова консультація";
    public static final String UK_EVENT_TYPE_TEST = "Контрольне тестування";
    public static final String UK_EVENT_TYPE_PSYCHOLOGIST = "Співбесіда з психологом";
    public static final String UK_EVENT_TYPE_GRADE_BOOK = "Видача залікових книжок";
    public static final String UK_EVENT_TYPE_PRINCIPAL = "Прийом директора";
    public static final String UK_EVENT_TYPE_DEPUTY = "Прийом заступника директора";
    public static final List<String> UK_EVENT_TYPES;
    static {
        UK_EVENT_TYPES = Arrays.asList(
                UK_EVENT_TYPE_PERSONAL,
                UK_EVENT_TYPE_GROUP,
                UK_EVENT_TYPE_TEST,
                UK_EVENT_TYPE_PSYCHOLOGIST,
                UK_EVENT_TYPE_GRADE_BOOK,
                UK_EVENT_TYPE_PRINCIPAL,
                UK_EVENT_TYPE_DEPUTY
        );
    }

    private static final String UK_WEEK_DAYS_MONDAY = "Понеділок";
    private static final String UK_WEEK_DAYS_TUESDAY = "Вівторок";
    private static final String UK_WEEK_DAYS_WEDNESDAY = "Середа";
    private static final String UK_WEEK_DAYS_THIRSDAY = "Четвер";
    private static final String UK_WEEK_DAYS_FRIDAY = "П'ятниця";
    public static final List<String> UK_WEEK_WORKING_DAYS;
    static {
        UK_WEEK_WORKING_DAYS = Arrays.asList(
                UK_WEEK_DAYS_MONDAY,
                UK_WEEK_DAYS_TUESDAY,
                UK_WEEK_DAYS_WEDNESDAY,
                UK_WEEK_DAYS_THIRSDAY,
                UK_WEEK_DAYS_FRIDAY);
    }

    public static final Integer DEFAULT_DURATION_FOR_UNDEFINED_EVENT_TYPE = 45;
    public static final LocalTime DEFAULT_TIME_WHEN_WORKING_DAY_BEGINS = LocalTime.of(9, 0);
    // need to point start date for standard events schema
    public static final LocalDate FIRST_MONDAY_OF_EPOCH = LocalDate.of(1970, 1, 5);
    // period of time when it's already late to join as participant to an incoming event
    public static final Duration DAYS_BETWEEN_LATEST_RESERVE_AND_EVENT = Duration.ofDays(0);
    public static final Duration HOURS_BETWEEN_LATEST_RESERVE_AND_EVENT = Duration.ofHours(0);

    // mailing service text messages
    public static final String FAKE_MAIL_DOMAIN = "x";
    public static final String SCHOOL_EMAIL = "extern.school@gmail.com";
    public static final String HOST_NAME = "http://localhost:8080"; //"https://extern.com.ua";
    public static final String APPOINTMENT_CANCELLATION_SUBJECT = "Відміна зустрічі ";
    public static final String APPOINTMENT_CANCELLATION_TEXT =
            "Вибачте, але в зв'язку з поважними причинами скасовано Вашу зустріч ";
    public static final String APPOINTMENT_CANCELLATION_PROPOSAL = "Зателефонуйте до школи за номером:"
                    + SCHOOL_PHONE_NUMBER
                    + " або скористайтеся цим посиланням для призначення нової зустрічі в зручний для Вас час: "
                    + HOST_NAME
                    + "\n"
                    + "Цей лист сформований та відправлений в автоматичному режимі, будь ласка, не відповідайте на нього."
                    + "\n";
    public static final String ADMINISTRATION_EMAIL_SIGNATURE = "З повагою,\nАдміністрація Школи Екстернів";
    public static final String EMAIL_CONFIRMATION_SUBJECT = "Підтвердження поштової адреси. Email confirmation";
    public static final String EMAIL_CONFIRMATION_TEXT = "Вітаємо! Ви зареєструвалися в сервісі планування відвідувань " +
            "Школи Екстернів в м.Києві. Для підтвердження вказаної адреси та завершення реєстрації, " +
            "будь ласка, скористайтеся наступним посиланням: ";
    public static final String EMAIL_CONFIRMATION_DISCLAIMER_EN = "You have received this message because somebody " +
            "used your address to subscribe to Kiev Extern School Visit Planner service. If you have received this " +
            "by mistake, please don't reply it or follow the link above. Your registration will be " +
            "cancelled automatically.";

    private Constants() {
        throw new AssertionError();
    }
}
