package pl.valueadd.date;



import java.sql.Timestamp;
import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class DateProviderImpl implements DateProvider {

    @Override
    public Timestamp endOfTheWorld() {
        return Timestamp.valueOf(LocalDateTime.of(9999, 12, 30, 23, 59, 59));
    }

    @Override
    public Timestamp currentTimestamp() {
        return new Timestamp(currentTimeMillis());
    }

    @Override
    public Timestamp currentTimestampAddingHours(int hours) {
        return new Timestamp(currentTimeMillis() + (hours * 60 * 60 * 1000));
    }
    @Override
    public Timestamp currentTimestampSubtractingHours(int hours) {
        return new Timestamp(currentTimeMillis() - (hours * 60 * 60 * 1000));
    }

    @Override
    public Timestamp currentTimestampAddingMinutes(int minutes) {
        return new Timestamp(currentTimeMillis() + (minutes * 60 * 1000));
    }

    @Override
    public Timestamp currentTimestampSubtractingMinutes(int minutes) {
        return new Timestamp(currentTimeMillis() - (minutes * 60 * 1000));
    }


    @Override
    public LocalDateTime currentDateTime() {
        return LocalDateTime.now(Clock.systemUTC());
    }

    @Override
    public LocalDate currentDate() {
        return LocalDate.now(Clock.systemUTC());
    }

    @Override
    public long currentTimeMillis() {
        return System.currentTimeMillis();
    }

    @Override
    public long daysSince(Timestamp date) {
        return (currentTimestamp().getTime() - date.getTime()) / (24 * 60 * 60 * 1000);
    }

}
