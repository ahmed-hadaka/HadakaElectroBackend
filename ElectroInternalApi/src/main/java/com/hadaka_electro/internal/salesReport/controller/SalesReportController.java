package com.hadaka_electro.internal.salesReport.controller;

import com.hadaka_electro.internal.salesReport.dto.ReportChartPayload;
import com.hadaka_electro.internal.salesReport.service.SalesReportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;


@RestController
@RequestMapping("/reports")
public class SalesReportController {

    private final SalesReportService salesReportService;

    public SalesReportController(SalesReportService salesReportService) {
        this.salesReportService = salesReportService;
    }

    @GetMapping("/sales-by-date")
    public ResponseEntity<ReportChartPayload> getSalesByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        ReportChartPayload reportData = salesReportService.getSalesByDateInRange(startDate, endDate);
        return ResponseEntity.ok(reportData);
    }

    @GetMapping("/sales-by-category")
    public ResponseEntity<ReportChartPayload> getSalesByCategory(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        ReportChartPayload reportData = salesReportService.getSalesByCategoryInRange(startDate, endDate);
        return ResponseEntity.ok(reportData);
    }

    @GetMapping("/sales-by-product")
    public ResponseEntity<ReportChartPayload> getSalesByProduct(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        ReportChartPayload reportData = salesReportService.getSalesByProductInRange(startDate, endDate);
        return ResponseEntity.ok(reportData);
    }
}
