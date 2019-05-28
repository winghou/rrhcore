package com.model;

public class AppChannelPageStatisticWithBLOBs extends AppChannelPageStatistic {
    private String total;

    private String dayStatistic;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getDayStatistic() {
        return dayStatistic;
    }

    public void setDayStatistic(String dayStatistic) {
        this.dayStatistic = dayStatistic;
    }
}