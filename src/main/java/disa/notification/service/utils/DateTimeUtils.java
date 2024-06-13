package disa.notification.service.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Locale;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateTimeUtils {

    public static final int HOUR_23 = 23;
    public static final int SECOND_59 = 59;
    public static final int MINUTE_59 = 59;

    public static DateInterval getLastWeekInterVal(){
        LocalDate now = LocalDate.now();
        //Get the First day of the week
        TemporalField fieldUS = WeekFields.of(Locale.US).dayOfWeek();
        LocalDate sunday=now.with(fieldUS, 1);
        LocalDate saturdayLastWeek=sunday.minusDays(1);//Saturday LastWeek
        LocalDate sundayLastWeek=saturdayLastWeek.minusDays(6);// Sunday Last Week
        return  DateInterval.of(sundayLastWeek.atStartOfDay(),saturdayLastWeek.atTime(HOUR_23,MINUTE_59,SECOND_59));
    }
    
    public static DateInterval getCurrentWeekInterVal() {
        LocalDateTime now = LocalDateTime.now();
        TemporalField dayOfWeek = WeekFields.of(Locale.US).dayOfWeek();
        LocalDate sunday = now.toLocalDate().with(dayOfWeek, 1);
        return DateInterval.of(sunday.atStartOfDay(), now);
    }
}


