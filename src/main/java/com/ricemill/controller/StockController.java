package com.ricemill.controller;

import com.ricemill.dto.ApiResponse;
import com.ricemill.dto.StockDto;
import com.ricemill.entity.ProductType;
import com.ricemill.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
@Tag(name = "Stock Management", description = "Inventory and stock operations")
public class StockController {
    
    private final StockService stockService;
    
    @GetMapping
    @Operation(summary = "Get stock list", description = "Get paginated stock inventory")
    public ApiResponse<Page<StockDto.InventoryResponse>> getStocks(
            @RequestParam ProductType type,
            @RequestParam(required = false) UUID warehouseId,
            Pageable pageable) {
        return ApiResponse.success(stockService.getStocks(type, warehouseId, pageable));
    }
    
    @GetMapping("/summary")
    @Operation(summary = "Get stock summary", description = "Get total stock summary by type")
    public ApiResponse<StockDto.SummaryResponse> getSummary(@RequestParam ProductType type) {
        return ApiResponse.success(stockService.getSummary(type));
    }
    
    @PostMapping("/inbound")
    @Operation(summary = "Inbound stock", description = "Add stock to warehouse")
    public ApiResponse<StockDto.InventoryResponse> inbound(@Valid @RequestBody StockDto.InboundRequest request) {
        return ApiResponse.success(stockService.inbound(request));
    }
    
    @PostMapping("/outbound")
    @Operation(summary = "Outbound stock", description = "Remove stock from warehouse")
    public ApiResponse<Void> outbound(@Valid @RequestBody StockDto.OutboundRequest request) {
        stockService.outbound(request);
        return ApiResponse.success(null);
    }
    
    @PostMapping("/transfer")
    @Operation(summary = "Transfer stock", description = "Transfer stock between warehouses")
    public ApiResponse<Void> transfer(@Valid @RequestBody StockDto.TransferRequest request) {
        stockService.transfer(request);
        return ApiResponse.success(null);
    }
    
    @PostMapping("/adjust")
    @Operation(summary = "Adjust stock", description = "Adjust stock quantity")
    public ApiResponse<Void> adjust(@Valid @RequestBody StockDto.AdjustmentRequest request) {
        stockService.adjust(request);
        return ApiResponse.success(null);
    }
    
    @PostMapping("/process")
    @Operation(summary = "Process paddy to rice", description = "Convert paddy to rice")
    public ApiResponse<Void> process(@Valid @RequestBody StockDto.ProcessRequest request) {
        stockService.process(request);
        return ApiResponse.success(null);
    }
}
