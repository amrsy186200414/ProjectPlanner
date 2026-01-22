package com.orabi.project_planner;

public class Duration {
    int months;
    int days;
    int hours;
    int minutes;

    public Duration() {
        months = 0;
        days = 0;
        hours = 0;
        minutes = 0;
    }

    public Duration(int months, int days, int hours, int minutes) {
        this.months = months;
        this.days = days;
        this.hours = hours;
        this.minutes = minutes;
    }

    public int getMonths() { return months; }
    public void setMonths(int months) { this.months = months; }

    public int getDays() { return days; }
    public void setDays(int days) { this.days = days; }

    public int getHours() { return hours; }
    public void setHours(int hours) { this.hours = hours; }

    public int getMinutes() { return minutes; }
    public void setMinutes(int minutes) { this.minutes = minutes; }

    public long toMillis() {
        long totalMinutes = minutes;
        totalMinutes += hours * 60;
        totalMinutes += days * 24 * 60;
        totalMinutes += months * 30 * 24 * 60;
        return totalMinutes * 60 * 1000;
    }

    public long toMinutes() {
        long totalMinutes = minutes;
        totalMinutes += hours * 60;
        totalMinutes += days * 24 * 60;
        totalMinutes += months * 30 * 24 * 60;
        return totalMinutes;
    }

    public static Duration fromMillis(long millis) {
        long totalSeconds = millis / 1000;
        long totalMinutes = totalSeconds / 60;

        return fromMinutes((int) totalMinutes);
    }

    public static Duration fromMinutes(int minutes) {
        int months = minutes / (30 * 24 * 60);
        int days = (minutes - months * (30 * 24 * 60)) / (24 * 60);
        int hours = (minutes - months * (30 * 24 * 60) - days * (24 * 60)) / 60;
        int minutes_still = (minutes - months * (30 * 24 * 60) - days * (24 * 60) - hours * 60);

        return new Duration(months, days, hours, minutes_still);
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (months > 0) sb.append(months).append("mo ");
        if (days > 0) sb.append(days).append("d ");
        if (hours > 0) sb.append(hours).append("h ");
        if (minutes > 0 || sb.length() == 0) sb.append(minutes).append("m");
        return sb.toString().trim();
    }

    public static Duration fromString(String durationStr) {
        if (durationStr == null || durationStr.isEmpty()) {
            return new Duration(0, 0, 0, 0);
        }

        try {
            String[] parts = durationStr.split(":");
            if (parts.length == 4) {
                return new Duration(
                        Integer.parseInt(parts[0]), // months
                        Integer.parseInt(parts[1]), // days
                        Integer.parseInt(parts[2]), // hours
                        Integer.parseInt(parts[3])  // minutes
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new Duration(0, 0, 0, 0);
    }
    public void add(Duration other) {
        this.minutes += other.minutes;
        this.hours += other.hours;
        this.days += other.days;
        this.months += other.months;

        if (this.minutes >= 60) {
            this.hours += this.minutes / 60;
            this.minutes = this.minutes % 60;
        }
        if (this.hours >= 24) {
            this.days += this.hours / 24;
            this.hours = this.hours % 24;
        }
        if (this.days >= 30) {
            this.months += this.days / 30;
            this.days = this.days % 30;
        }
    }

    public int getTotalDays() {
        return (this.months * 30) + this.days;
    }
}