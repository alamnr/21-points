package com.jhipster.health.web.rest.vm;

import java.time.LocalDate;

public class PointsPerWeek {

    private LocalDate week;
    private Integer points;

    public PointsPerWeek(LocalDate week, Integer points) {
        this.week = week;
        this.points = points;
    }

    public LocalDate getWeek() {
        return week;
    }

    public Integer getPoints() {
        return points;
    }

    public void setWeek(LocalDate week) {
        this.week = week;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    @Override
    public String toString() {
        return "PointsPerWeek [week=" + week + ", points=" + points + "]";
    }
}
