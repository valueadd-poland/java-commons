package pl.valueadd.date;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

public interface DateProvider {

    Timestamp endOfTheWorld();

    Timestamp currentTimestamp();

    Timestamp currentTimestampAddingHours(int hours);

    Timestamp currentTimestampSubtractingHours(int hours);

    Timestamp currentTimestampAddingMinutes(int minutes);

    Timestamp currentTimestampSubtractingMinutes(int minutes);

    LocalDateTime currentDateTime();

    LocalDate currentDate();

    long currentTimeMillis();

    long daysSince(Timestamp createdAt);

}
