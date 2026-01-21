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
        // Approximate conversions
        long totalMinutes = minutes;
        totalMinutes += hours * 60;
        totalMinutes += days * 24 * 60;
        totalMinutes += months * 30 * 24 * 60;
        return totalMinutes * 60 * 1000;
    }

    public long toMinutes() {
        // Approximate conversions
        long totalMinutes = minutes;
        totalMinutes += hours * 60;
        totalMinutes += days * 24 * 60;
        totalMinutes += months * 30 * 24 * 60;
        return totalMinutes;
    }

    // ADD THIS METHOD: Convert milliseconds to Duration
    public static Duration fromMillis(long millis) {
        long totalSeconds = millis / 1000;
        long totalMinutes = totalSeconds / 60;

        // Call the existing fromMinutes method
        return fromMinutes((int) totalMinutes);
    }

    public static Duration fromMinutes(int minutes) {
        // Approximate conversions
        int months = minutes / (30 * 24 * 60);
        int days = (minutes - months * (30 * 24 * 60)) / (24 * 60);
        int hours = (minutes - months * (30 * 24 * 60) - days * (24 * 60)) / 60;
        int minutes_still = (minutes - months * (30 * 24 * 60) - days * (24 * 60) - hours * 60);

        return new Duration(months, days, hours, minutes_still);
    }

    // ADD THIS METHOD: Convert string representation to Duration
    public static Duration fromString(String durationStr) {
        // Default values
        int months = 0;
        int days = 0;
        int hours = 0;
        int minutes = 0;

        if (durationStr == null || durationStr.isEmpty()) {
            return new Duration();
        }

        // Try to parse the string format (e.g., "2 mon ,5 d ,3 h ,10 min ,")
        String str = durationStr.trim();

        // Remove trailing comma if present
        if (str.endsWith(",")) {
            str = str.substring(0, str.length() - 1).trim();
        }

        // Split by comma to get individual components
        String[] parts = str.split(",");

        for (String part : parts) {
            part = part.trim();
            if (part.isEmpty()) {
                continue;
            }

            // Split by space to separate value and unit
            String[] valueUnit = part.split("\\s+");
            if (valueUnit.length < 2) {
                continue; // Skip invalid format
            }

            try {
                int value = Integer.parseInt(valueUnit[0]);
                String unit = valueUnit[1].toLowerCase();

                switch (unit) {
                    case "mon":
                        months = value;
                        break;
                    case "d":
                        days = value;
                        break;
                    case "h":
                        hours = value;
                        break;
                    case "min":
                        minutes = value;
                        break;
                    default:
                        // Unknown unit, ignore
                        break;
                }
            } catch (NumberFormatException e) {
                // Invalid number, skip this part
            }
        }

        return new Duration(months, days, hours, minutes);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        boolean addedPrevious = false;

        if (months > 0) {
            sb.append(months).append(" mon");
            addedPrevious = true;
        }

        if (days > 0) {
            if (addedPrevious) {
                sb.append(" ,");
            }
            sb.append(days).append(" d");
            addedPrevious = true;
        }

        if (hours > 0) {
            if (addedPrevious) {
                sb.append(" ,");
            }
            sb.append(hours).append(" h");
            addedPrevious = true;
        }

        if (minutes > 0) {
            if (addedPrevious) {
                sb.append(" ,");
            }
            sb.append(minutes).append(" min");
        }

        // Add trailing comma to match your original format
        if (sb.length() > 0) {
            sb.append(" ,");
        } else {
            sb.append("0 min ,");
        }

        return sb.toString();
    }
}