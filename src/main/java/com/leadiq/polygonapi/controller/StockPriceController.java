package com.leadiq.polygonapi.controller;

import com.leadiq.polygonapi.config.OpenApiTagConfig;
import com.leadiq.polygonapi.dto.StockPriceResponseDTO;
import com.leadiq.polygonapi.entity.StockPrice;
import com.leadiq.polygonapi.exception.ErrorResponse;
import com.leadiq.polygonapi.service.StockPriceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * StockPriceController is a REST controller responsible for managing stock price data.
 * It provides endpoints for fetching and saving stock prices as well as retrieving stock prices
 * for a specific company symbol on a given date.
 */
@RestController
@RequestMapping("/api/v1/stocks")
@RequiredArgsConstructor
@Tag(name = OpenApiTagConfig.TAG_STOCK_PRICES)
public class StockPriceController {

    private final StockPriceService stockPriceService;

    /**
     * Fetches stock price data for a given company symbol within the specified date range,
     * saves the data to the database, and returns the list of saved stock prices.
     */
    @Operation(
            summary = "Fetch and save stock prices",
            description = "Fetches stock price data for a given company symbol within the specified date range, " +
                    "saves the data to the database, and returns a paginated list of saved stock prices."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved stock prices",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input parameters",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "503",
                    description = "Polygon API service unavailable",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/fetch")
    public ResponseEntity<Page<StockPriceResponseDTO>> fetchAndSaveStockPrices(
            @Parameter(description = "Stock symbol (e.g., AAPL)", required = true, example = "AAPL")
            @RequestParam String companySymbol,

            @Parameter(description = "Start date in ISO format (YYYY-MM-DD)", required = true, example = "2023-01-01")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,

            @Parameter(description = "End date in ISO format (YYYY-MM-DD)", required = true, example = "2023-01-31")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,

            @Parameter(description = "Page number (zero-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of items per page", example = "20")
            @RequestParam(defaultValue = "20") int size
    ) {
        if (fromDate.isAfter(toDate)) {
            throw new IllegalArgumentException("From date cannot be after to date");
        }

        Page<StockPrice> stockPrices = stockPriceService.fetchAndSavePrices(
                companySymbol, fromDate.toString(), toDate.toString(), PageRequest.of(page, size));

        // Convert to DTO page
        Page<StockPriceResponseDTO> dtoPage = stockPrices.map(this::convertToDTO);

        return ResponseEntity.ok(dtoPage);
    }

    /**
     * Retrieves the stock price for a specific company symbol on a given date.
     */
    @Operation(
            summary = "Get stock price by symbol and date",
            description = "Retrieves the stock price for a specific company symbol on a given date."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved stock price",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = StockPriceResponseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Stock price not found for the given symbol and date",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input parameters",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/{symbol}")
    public ResponseEntity<StockPriceResponseDTO> getStockPriceBySymbolAndDate(
            @Parameter(description = "Stock symbol (e.g., AAPL)", required = true, example = "AAPL")
            @PathVariable("symbol") String symbol,

            @Parameter(description = "Date in ISO format (YYYY-MM-DD)", required = true, example = "2023-01-15")
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        StockPrice stockPrice = stockPriceService.getStockPrice(symbol, date);
        return ResponseEntity.ok(convertToDTO(stockPrice));
    }

    /**
     * Converts a StockPrice entity to a StockPriceResponseDTO
     */
    private StockPriceResponseDTO convertToDTO(StockPrice stockPrice) {
        return new StockPriceResponseDTO(
                stockPrice.getCompanySymbol(),
                stockPrice.getDate(),
                stockPrice.getOpenPrice(),
                stockPrice.getClosePrice(),
                stockPrice.getHighPrice(),
                stockPrice.getLowPrice(),
                stockPrice.getVolume()
        );
    }
}