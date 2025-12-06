package com.example.pandora.dto;

public class RevenueSummaryDTO {
    private double totalRevenue;
    private long completedOrders;

    public RevenueSummaryDTO() {
    }

    public RevenueSummaryDTO(double totalRevenue, long completedOrders) {
        this.totalRevenue = totalRevenue;
        this.completedOrders = completedOrders;
    }

    public double getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(double totalRevenue) {
        this.totalRevenue = totalRevenue;
    }

    public long getCompletedOrders() {
        return completedOrders;
    }

    public void setCompletedOrders(long completedOrders) {
        this.completedOrders = completedOrders;
    }
}
