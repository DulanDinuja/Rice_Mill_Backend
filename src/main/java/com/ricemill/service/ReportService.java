package com.ricemill.service;

import com.ricemill.dto.ReportDto;
import com.ricemill.entity.*;
import com.ricemill.exception.BusinessException;
import com.ricemill.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {
    
    private final StockMovementRepository stockMovementRepository;
    private final ProcessingRecordRepository processingRecordRepository;
    private final WarehouseRepository warehouseRepository;
    private final SupplierRepository supplierRepository;
    private final BatchRepository batchRepository;

    private static final List<String> VALID_REPORT_TYPES = Arrays.asList(
            "paddy_threshing", "paddy_sale", "paddy_add_stock", "rice_sale", "rice_add_stock"
    );

    public ReportDto.MovementReportResponse getMovementReport(ProductType type, MovementType movementType,
                                                               UUID warehouseId, LocalDateTime from, LocalDateTime to,
                                                               Pageable pageable) {
        Page<StockMovement> movements = stockMovementRepository.findByFilters(type, movementType, warehouseId, from, to, pageable);
        
        List<ReportDto.MovementEntry> entries = movements.stream()
                .map(this::toMovementEntry)
                .collect(Collectors.toList());
        
        BigDecimal totalQuantity = movements.stream()
                .map(StockMovement::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return ReportDto.MovementReportResponse.builder()
                .movements(entries)
                .totalQuantity(totalQuantity)
                .count((int) movements.getTotalElements())
                .build();
    }
    
    public ReportDto.ProcessingReportResponse getProcessingReport(LocalDateTime from, LocalDateTime to) {
        List<ProcessingRecord> records = processingRecordRepository.findByDateRange(from, to);
        
        List<ReportDto.ProcessingEntry> entries = records.stream()
                .map(this::toProcessingEntry)
                .collect(Collectors.toList());
        
        BigDecimal totalInput = records.stream()
                .map(ProcessingRecord::getInputQty)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalOutput = records.stream()
                .map(ProcessingRecord::getOutputQty)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalWaste = records.stream()
                .map(ProcessingRecord::getWasteQty)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal averageYield = records.isEmpty() ? BigDecimal.ZERO :
                records.stream()
                        .map(ProcessingRecord::getYieldPercent)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
                        .divide(BigDecimal.valueOf(records.size()), 2, RoundingMode.HALF_UP);
        
        return ReportDto.ProcessingReportResponse.builder()
                .records(entries)
                .totalInput(totalInput)
                .totalOutput(totalOutput)
                .totalWaste(totalWaste)
                .averageYield(averageYield)
                .build();
    }

    public ReportDto.ReportDataResponse getReports(String reportType, LocalDateTime fromDate, LocalDateTime toDate,
                                                    String warehouse, String paddyType, String riceType,
                                                    String supplier, Integer page, Integer limit) {
        // Validate report type
        if (!VALID_REPORT_TYPES.contains(reportType)) {
            throw new BusinessException("Invalid report type: " + reportType);
        }

        // Validate date range
        if (fromDate != null && toDate != null && fromDate.isAfter(toDate)) {
            throw new BusinessException("From date must be earlier than to date");
        }

        // Validate pagination
        if (limit > 1000) {
            limit = 1000;
        }

        // Build pageable
        Pageable pageable = PageRequest.of(page - 1, limit, Sort.by(Sort.Direction.DESC, "performedAt"));

        // Determine movement type and product type from report type
        MovementType movementType = null;
        ProductType productType = null;

        if (reportType.startsWith("paddy")) {
            productType = ProductType.PADDY;
            if (reportType.equals("paddy_sale")) {
                movementType = MovementType.OUTBOUND;
            } else if (reportType.equals("paddy_add_stock")) {
                movementType = MovementType.INBOUND;
            } else if (reportType.equals("paddy_threshing")) {
                movementType = MovementType.PROCESSING;
            }
        } else if (reportType.startsWith("rice")) {
            productType = ProductType.RICE;
            if (reportType.equals("rice_sale")) {
                movementType = MovementType.OUTBOUND;
            } else if (reportType.equals("rice_add_stock")) {
                movementType = MovementType.INBOUND;
            }
        }

        // Fetch data
        Page<StockMovement> movements = stockMovementRepository.findByFiltersDetailed(
                productType, movementType, warehouse, paddyType, riceType, supplier, fromDate, toDate, pageable);

        // Map to response
        List<ReportDto.ReportDataEntry> entries = movements.stream()
                .map(this::toReportDataEntry)
                .collect(Collectors.toList());

        // Calculate summary
        BigDecimal totalQuantity = movements.stream()
                .map(StockMovement::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        LocalDateTime minDate = movements.stream()
                .map(StockMovement::getPerformedAt)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime maxDate = movements.stream()
                .map(StockMovement::getPerformedAt)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        ReportDto.ReportSummary summary = ReportDto.ReportSummary.builder()
                .totalRecords(movements.getTotalElements())
                .totalQuantity(totalQuantity)
                .dateRange(ReportDto.DateRange.builder()
                        .from(fromDate != null ? fromDate : minDate)
                        .to(toDate != null ? toDate : maxDate)
                        .build())
                .build();

        // Build pagination info
        ReportDto.PaginationInfo paginationInfo = ReportDto.PaginationInfo.builder()
                .currentPage(page)
                .totalPages(movements.getTotalPages())
                .totalRecords(movements.getTotalElements())
                .limit(limit)
                .hasNextPage(movements.hasNext())
                .hasPreviousPage(movements.hasPrevious())
                .build();

        return ReportDto.ReportDataResponse.builder()
                .data(entries)
                .pagination(paginationInfo)
                .summary(summary)
                .build();
    }

    public ReportDto.ChartDataResponse getChartData(String reportType, LocalDateTime fromDate, LocalDateTime toDate,
                                                     String warehouse, String paddyType, String riceType,
                                                     String supplier, String groupBy) {
        // Validate report type
        if (!VALID_REPORT_TYPES.contains(reportType)) {
            throw new BusinessException("Invalid report type: " + reportType);
        }

        // Determine movement type and product type
        MovementType movementType = null;
        ProductType productType = null;

        if (reportType.startsWith("paddy")) {
            productType = ProductType.PADDY;
            if (reportType.equals("paddy_sale")) {
                movementType = MovementType.OUTBOUND;
            } else if (reportType.equals("paddy_add_stock")) {
                movementType = MovementType.INBOUND;
            } else if (reportType.equals("paddy_threshing")) {
                movementType = MovementType.PROCESSING;
            }
        } else if (reportType.startsWith("rice")) {
            productType = ProductType.RICE;
            if (reportType.equals("rice_sale")) {
                movementType = MovementType.OUTBOUND;
            } else if (reportType.equals("rice_add_stock")) {
                movementType = MovementType.INBOUND;
            }
        }

        // Fetch all matching data
        List<StockMovement> movements = stockMovementRepository.findAllByFiltersDetailed(
                productType, movementType, warehouse, paddyType, riceType, supplier, fromDate, toDate);

        // Group by period
        Map<String, ReportDto.ChartDataEntry> grouped = new LinkedHashMap<>();
        DateTimeFormatter formatter = getFormatterForGrouping(groupBy);

        for (StockMovement movement : movements) {
            String period = movement.getPerformedAt().format(formatter);
            String label = getLabel(movement.getPerformedAt(), groupBy);

            ReportDto.ChartDataEntry entry = grouped.computeIfAbsent(period, k ->
                    ReportDto.ChartDataEntry.builder()
                            .period(period)
                            .label(label)
                            .rice(BigDecimal.ZERO)
                            .paddy(BigDecimal.ZERO)
                            .quantity(BigDecimal.ZERO)
                            .recordCount(0L)
                            .build());

            entry.setQuantity(entry.getQuantity().add(movement.getQuantity()));
            entry.setRecordCount(entry.getRecordCount() + 1);

            if (movement.getType() == ProductType.RICE) {
                entry.setRice(entry.getRice().add(movement.getQuantity()));
            } else {
                entry.setPaddy(entry.getPaddy().add(movement.getQuantity()));
            }
        }

        List<ReportDto.ChartDataEntry> chartData = new ArrayList<>(grouped.values());

        // Calculate summary
        BigDecimal totalQuantity = chartData.stream()
                .map(ReportDto.ChartDataEntry::getQuantity)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal averagePerPeriod = chartData.isEmpty() ? BigDecimal.ZERO :
                totalQuantity.divide(BigDecimal.valueOf(chartData.size()), 2, RoundingMode.HALF_UP);

        ReportDto.ChartSummary summary = ReportDto.ChartSummary.builder()
                .totalQuantity(totalQuantity)
                .averagePerPeriod(averagePerPeriod)
                .periods(chartData.size())
                .build();

        return ReportDto.ChartDataResponse.builder()
                .data(chartData)
                .summary(summary)
                .build();
    }

    public Resource exportReport(ReportDto.ExportRequest request) {
        // Validate format
        if (!request.getFormat().equalsIgnoreCase("csv") && !request.getFormat().equalsIgnoreCase("pdf")) {
            throw new BusinessException("Invalid export format. Only 'csv' and 'pdf' are supported.");
        }

        // Fetch report data
        ReportDto.ReportDataResponse reportData = getReports(
                request.getReportType(),
                request.getFromDate(),
                request.getToDate(),
                request.getFilters() != null ? request.getFilters().getWarehouse() : null,
                request.getFilters() != null ? request.getFilters().getPaddyType() : null,
                request.getFilters() != null ? request.getFilters().getRiceType() : null,
                request.getFilters() != null ? request.getFilters().getSupplier() : null,
                1,
                1000
        );

        if (request.getFormat().equalsIgnoreCase("csv")) {
            return generateCsv(request, reportData);
        } else {
            return generatePdf(request, reportData);
        }
    }

    private Resource generateCsv(ReportDto.ExportRequest request, ReportDto.ReportDataResponse reportData) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);

            // Add BOM for Excel compatibility
            writer.write('\ufeff');

            // Write header
            String reportLabel = getReportLabel(request.getReportType());
            writer.write(reportLabel + "\n");
            writer.write("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + "\n");

            if (request.getFromDate() != null && request.getToDate() != null) {
                writer.write(String.format("Date Range: %s to %s\n",
                        request.getFromDate().format(DateTimeFormatter.ISO_LOCAL_DATE),
                        request.getToDate().format(DateTimeFormatter.ISO_LOCAL_DATE)));
            }

            writer.write("Total Records: " + reportData.getSummary().getTotalRecords() + "\n\n");

            // Write column headers
            if (request.getReportType().startsWith("paddy")) {
                writer.write("Paddy Type,Quantity,Unit,Moisture %,Warehouse,Supplier,Action Type,Date\n");

                // Write data rows
                for (ReportDto.ReportDataEntry entry : reportData.getData()) {
                    writer.write(String.format("%s,%s,%s,%s,%s,%s,%s,%s\n",
                            escapeCsv(entry.getPaddyType()),
                            entry.getQuantity(),
                            entry.getUnit(),
                            entry.getMoistureLevel() != null ? entry.getMoistureLevel() : "",
                            escapeCsv(entry.getWarehouse()),
                            escapeCsv(entry.getSupplier()),
                            escapeCsv(entry.getActionType()),
                            entry.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
                }
            } else {
                writer.write("Rice Type,Quantity,Unit,Warehouse,Action Type,Date\n");

                // Write data rows
                for (ReportDto.ReportDataEntry entry : reportData.getData()) {
                    writer.write(String.format("%s,%s,%s,%s,%s,%s\n",
                            escapeCsv(entry.getRiceType()),
                            entry.getQuantity(),
                            entry.getUnit(),
                            escapeCsv(entry.getWarehouse()),
                            escapeCsv(entry.getActionType()),
                            entry.getDate().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)));
                }
            }

            writer.flush();
            writer.close();

            byte[] data = outputStream.toByteArray();
            return new ByteArrayResource(data);

        } catch (Exception e) {
            throw new BusinessException("Failed to generate CSV: " + e.getMessage());
        }
    }

    private Resource generatePdf(ReportDto.ExportRequest request, ReportDto.ReportDataResponse reportData) {
        // For PDF generation, you would typically use a library like iText or Apache PDFBox
        // For now, we'll throw an exception indicating it needs to be implemented
        throw new BusinessException("PDF export is not yet implemented. Please use CSV format.");
    }

    public List<ReportDto.WarehouseListResponse> getAllActiveWarehouses() {
        List<Warehouse> warehouses = warehouseRepository.findByDeletedAtIsNullAndActiveTrue();
        return warehouses.stream()
                .map(this::toWarehouseListResponse)
                .collect(Collectors.toList());
    }

    public List<ReportDto.SupplierListResponse> getAllActiveSuppliers() {
        List<Supplier> suppliers = supplierRepository.findByDeletedAtIsNullAndActiveTrue();
        return suppliers.stream()
                .map(this::toSupplierListResponse)
                .collect(Collectors.toList());
    }

    public List<ReportDto.ReportTypeInfo> getReportTypes() {
        return Arrays.asList(
                ReportDto.ReportTypeInfo.builder()
                        .value("paddy_threshing")
                        .label("Paddy Threshing Report")
                        .category("paddy")
                        .description("All threshing operations with moisture levels")
                        .columns(Arrays.asList("paddyType", "quantity", "moistureLevel", "warehouse", "supplier", "actionType", "date"))
                        .build(),
                ReportDto.ReportTypeInfo.builder()
                        .value("paddy_sale")
                        .label("Paddy Sale Report")
                        .category("paddy")
                        .description("Paddy sales transactions")
                        .columns(Arrays.asList("paddyType", "quantity", "moistureLevel", "warehouse", "supplier", "pricePerKg", "actionType", "date"))
                        .build(),
                ReportDto.ReportTypeInfo.builder()
                        .value("paddy_add_stock")
                        .label("Paddy Add Stock Report")
                        .category("paddy")
                        .description("Paddy stock additions")
                        .columns(Arrays.asList("paddyType", "quantity", "moistureLevel", "warehouse", "supplier", "actionType", "date"))
                        .build(),
                ReportDto.ReportTypeInfo.builder()
                        .value("rice_sale")
                        .label("Rice Sale Report")
                        .category("rice")
                        .description("Rice sales transactions")
                        .columns(Arrays.asList("riceType", "quantity", "warehouse", "pricePerKg", "actionType", "date"))
                        .build(),
                ReportDto.ReportTypeInfo.builder()
                        .value("rice_add_stock")
                        .label("Rice Add Stock Report")
                        .category("rice")
                        .description("Rice stock additions")
                        .columns(Arrays.asList("riceType", "quantity", "warehouse", "actionType", "date"))
                        .build()
        );
    }

    public String generateFileName(String reportType, String format) {
        String reportLabel = getReportLabel(reportType).replace(" ", "_");
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        return String.format("%s_%s.%s", reportLabel, timestamp, format.toLowerCase());
    }

    // Helper methods

    private ReportDto.MovementEntry toMovementEntry(StockMovement movement) {
        return ReportDto.MovementEntry.builder()
                .performedAt(movement.getPerformedAt())
                .movementType(movement.getMovementType().name())
                .type(movement.getType())
                .quantity(movement.getQuantity())
                .warehouseFrom(movement.getWarehouseFrom() != null ? movement.getWarehouseFrom().getName() : null)
                .warehouseTo(movement.getWarehouseTo() != null ? movement.getWarehouseTo().getName() : null)
                .batchCode(movement.getBatch().getBatchCode())
                .referenceNo(movement.getReferenceNo())
                .performedBy(movement.getPerformedBy().getFullName())
                .build();
    }
    
    private ReportDto.ProcessingEntry toProcessingEntry(ProcessingRecord record) {
        return ReportDto.ProcessingEntry.builder()
                .performedAt(record.getPerformedAt())
                .inputBatchCode(record.getInputBatch().getBatchCode())
                .outputBatchCode(record.getOutputBatch().getBatchCode())
                .warehouseName(record.getWarehouse().getName())
                .inputQty(record.getInputQty())
                .outputQty(record.getOutputQty())
                .wasteQty(record.getWasteQty())
                .yieldPercent(record.getYieldPercent())
                .referenceNo(record.getReferenceNo())
                .performedBy(record.getPerformedBy().getFullName())
                .build();
    }

    private ReportDto.ReportDataEntry toReportDataEntry(StockMovement movement) {
        return ReportDto.ReportDataEntry.builder()
                .id(movement.getId())
                .paddyType(movement.getType() == ProductType.PADDY ? movement.getBatch().getVariety() : null)
                .riceType(movement.getType() == ProductType.RICE ? movement.getBatch().getVariety() : null)
                .quantity(movement.getQuantity())
                .unit(movement.getUnit())
                .moistureLevel(movement.getBatch().getMoisture())
                .warehouse(movement.getWarehouseTo() != null ? movement.getWarehouseTo().getName() :
                          (movement.getWarehouseFrom() != null ? movement.getWarehouseFrom().getName() : null))
                .supplier(movement.getSupplier() != null ? movement.getSupplier().getName() : null)
                .pricePerKg(null) // Price data would need to be added to StockMovement entity
                .actionType(movement.getMovementType().name())
                .date(movement.getPerformedAt())
                .createdAt(movement.getCreatedAt())
                .updatedAt(movement.getUpdatedAt())
                .build();
    }

    private ReportDto.WarehouseListResponse toWarehouseListResponse(Warehouse warehouse) {
        return ReportDto.WarehouseListResponse.builder()
                .id(warehouse.getId())
                .name(warehouse.getName())
                .location(warehouse.getLocation())
                .capacity(warehouse.getCapacity())
                .currentStock(BigDecimal.ZERO) // Would need to be calculated from inventory
                .active(warehouse.getActive())
                .build();
    }

    private ReportDto.SupplierListResponse toSupplierListResponse(Supplier supplier) {
        return ReportDto.SupplierListResponse.builder()
                .id(supplier.getId())
                .name(supplier.getName())
                .contactNumber(supplier.getPhone())
                .email(null) // Email field not in current Supplier entity
                .address(supplier.getAddress())
                .active(supplier.getActive())
                .build();
    }

    private DateTimeFormatter getFormatterForGrouping(String groupBy) {
        return switch (groupBy.toLowerCase()) {
            case "day" -> DateTimeFormatter.ofPattern("yyyy-MM-dd");
            case "week" -> DateTimeFormatter.ofPattern("yyyy-'W'ww");
            case "month" -> DateTimeFormatter.ofPattern("yyyy-MM");
            default -> DateTimeFormatter.ofPattern("yyyy-MM");
        };
    }

    private String getLabel(LocalDateTime dateTime, String groupBy) {
        return switch (groupBy.toLowerCase()) {
            case "day" -> dateTime.format(DateTimeFormatter.ofPattern("MMM dd"));
            case "week" -> "Week " + dateTime.format(DateTimeFormatter.ofPattern("ww"));
            case "month" -> dateTime.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
            default -> dateTime.getMonth().getDisplayName(TextStyle.SHORT, Locale.ENGLISH);
        };
    }

    private String getReportLabel(String reportType) {
        return switch (reportType) {
            case "paddy_threshing" -> "Paddy Threshing Report";
            case "paddy_sale" -> "Paddy Sale Report";
            case "paddy_add_stock" -> "Paddy Add Stock Report";
            case "rice_sale" -> "Rice Sale Report";
            case "rice_add_stock" -> "Rice Add Stock Report";
            default -> "Report";
        };
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
