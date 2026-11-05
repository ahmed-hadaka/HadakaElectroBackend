package com.hadaka_electro.internal.salesReport.dto;

import java.util.List;

public record ReportChartPayload(
        Double totalGrossSales,
        Double totalNetSales,
        Double avgGrossSales,
        Long totalItems,
        List<SalesReportDataDTO> chartData
) {
}
