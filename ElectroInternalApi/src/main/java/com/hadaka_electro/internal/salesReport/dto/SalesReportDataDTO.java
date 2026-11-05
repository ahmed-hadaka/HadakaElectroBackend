package com.hadaka_electro.internal.salesReport.dto;

public record SalesReportDataDTO(
        String identifier,
        Long itemCounts, Double grossSales, Double netSales) {

    // Overloaded constructor to safely catch Hibernate's unknown function types and dynamic number formats
    public SalesReportDataDTO(Object identifier, Number itemCounts, Number grossSales, Number netSales) {
        this(
                identifier != null ? String.valueOf(identifier) : null,
                itemCounts != null ? itemCounts.longValue() : 0L,
                grossSales != null ? grossSales.doubleValue() : 0.0,
                netSales != null ? netSales.doubleValue() : 0.0
        );
    }
}
