package com.hadaka_electro.internal.order.repository;

import com.hadaka_electro.common.entities.order.OrderDetail;
import com.hadaka_electro.internal.salesReport.dto.SalesReportDataDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetail, Integer> {
    @Query(value = "select new com.hadaka_electro.internal.salesReport.dto.SalesReportDataDTO(" +
            " od.product.category.name, " +
            " sum(od.quantity), " +
            " sum(od.subtotal), " +
            " sum(od.subtotal - (od.productCost * od.quantity))) " +
            " from OrderDetail od where od.order.orderTime >= ?1 and od.order.orderTime <= ?2" +
            " group by od.product.category.name")
    List<SalesReportDataDTO> getOrderDetailsByCategoryInDateRange(LocalDateTime startDate, LocalDateTime endDate);

    @Query(value = "select new com.hadaka_electro.internal.salesReport.dto.SalesReportDataDTO(" +
            " od.product.name, " +
            " sum(od.quantity), " +
            " sum(od.subtotal), " +
            " sum(od.subtotal - (od.productCost * od.quantity))) " +
            " from OrderDetail od where od.order.orderTime >= ?1 and od.order.orderTime <= ?2" +
            " group by od.product.name")
    List<SalesReportDataDTO> getOrderDetailsByProductInDateRange(LocalDateTime startDate, LocalDateTime endDate);
}
