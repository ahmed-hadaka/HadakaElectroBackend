package com.hadaka_electro.internal.salesReport.service;

import com.hadaka_electro.internal.order.repository.OrderDetailsRepository;
import com.hadaka_electro.internal.order.repository.OrderRepository;
import com.hadaka_electro.internal.salesReport.dto.ReportChartPayload;
import com.hadaka_electro.internal.salesReport.dto.SalesReportDataDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class SalesReportService {

    private final OrderRepository orderRepository;
    private final OrderDetailsRepository orderDetailsRepository;

    public SalesReportService(OrderRepository orderRepository, OrderDetailsRepository orderDetailsRepository) {
        this.orderRepository = orderRepository;
        this.orderDetailsRepository = orderDetailsRepository;
    }

    public ReportChartPayload getSalesByDateInRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<SalesReportDataDTO> reportData = orderRepository.getOrdersInDateRange(startDate, endDate);
        return buildReportChartPayload(reportData);
    }

    public ReportChartPayload getSalesByCategoryInRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<SalesReportDataDTO> reportData = orderDetailsRepository.getOrderDetailsByCategoryInDateRange(startDate, endDate);
        return buildReportChartPayload(reportData);
    }


    public ReportChartPayload getSalesByProductInRange(LocalDateTime startDate, LocalDateTime endDate) {
        List<SalesReportDataDTO> reportData = orderDetailsRepository.getOrderDetailsByProductInDateRange(startDate, endDate);
        return buildReportChartPayload(reportData);
    }

    private ReportChartPayload buildReportChartPayload(List<SalesReportDataDTO> salesReportDataDTOS) {
        Double totalGrossSales = 0.0, totalNetSales = 0.0;
        Long numGrossSalesPerDate = 0L, totalItems = 0L;
        for (SalesReportDataDTO dto : salesReportDataDTOS) {
            totalGrossSales += dto.grossSales();
            totalNetSales += dto.netSales();
            numGrossSalesPerDate += 1;
            totalItems += dto.itemCounts();
        }

        ReportChartPayload reportChartPayload =
                new ReportChartPayload(totalGrossSales, totalNetSales,
                        totalGrossSales / numGrossSalesPerDate, totalItems, salesReportDataDTOS);

        return reportChartPayload;
    }
}
