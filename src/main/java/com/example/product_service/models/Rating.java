package com.example.product_service.models;


public class Rating {

    private Double average;
    private Integer count;

    public Rating() {}

    public Rating(Double average, Integer count) {
        this.average = average;
        this.count = count;
    }

    public Double getAverage() {
        return average;
    }

    public void setAverage(Double average) {
        this.average = average;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}