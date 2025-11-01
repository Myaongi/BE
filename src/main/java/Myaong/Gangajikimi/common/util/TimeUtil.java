package Myaong.Gangajikimi.common.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class TimeUtil {

    public static String getTimeAgo(LocalDateTime dateTime) {
        LocalDateTime now = LocalDateTime.now();
        long minutes = ChronoUnit.MINUTES.between(dateTime, now);
        long hours = ChronoUnit.HOURS.between(dateTime, now);
        long days = ChronoUnit.DAYS.between(dateTime, now);
        long weeks = ChronoUnit.WEEKS.between(dateTime, now);
        long months = ChronoUnit.MONTHS.between(dateTime, now);
        long years = ChronoUnit.YEARS.between(dateTime, now);

        if (minutes < 1) {
            return "방금 전";
        } else if (minutes < 60) {
            return minutes + "분 전";
        } else if (hours < 24) {
            return hours + "시간 전";
        } else if (days < 7) {
            return days + "일 전";
            // ~주 전 추가 (1주 이상, 4주 미만)
        } else if (weeks < 4) {
            return weeks + "주 전";
            // ~달 전 추가 (4주(약 1달) 이상, 12달 미만)
        } else if (months < 12) {
            return months + "달 전";
            // ~년 전 추가 (1년 이상)
        } else {
            return years + "년 전";
        }
    }
}
