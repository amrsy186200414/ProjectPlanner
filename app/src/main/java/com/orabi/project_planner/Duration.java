package com.orabi.project_planner;

public  class Duration {
    int months;
    int days;
    int hours;
    int minutes;

    public Duration() {}

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
        return totalMinutes ;
    }
    public Duration fromMinutes(int minutes) {
        // Approximate conversions
//        long totalMinutes = minutes;
//        totalMinutes += hours * 60;
//        totalMinutes += days * 24 * 60;
//        totalMinutes += months * 30 * 24 * 60;
//        return totalMinutes ;
        int months=minutes/(30 * 24 * 60);
        int days= (minutes - months * (30 * 24 * 60) )/( 24 * 60 );
        int hours=(minutes - months * (30 * 24 * 60)- days *(24 * 60) )/( 60 );
        int minunts_still=(minutes - months * (30 * 24 * 60)- days *(24 * 60)- hours*60 );

        return new Duration(months,days,hours,minunts_still);
    }


    // Helper method to format as string
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Boolean Continue=true;
        if (months > 0 && Continue)
        {
            sb.append(months).append(" m ");
            Continue=false;
        }
        if (days > 0 && Continue)
        {
            sb.append(days).append(" d ");
            Continue=false;

        }
        if (hours > 0 && Continue)
        {
            sb.append(hours).append(" h ");
        }
        if (minutes > 0 && Continue)
        {
            sb.append(minutes).append(" m ");
        }
        return sb.toString().trim();
    }
}