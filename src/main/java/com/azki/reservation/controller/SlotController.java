package com.azki.reservation.controller;

import com.azki.reservation.dto.http.HttpResponse;
import com.azki.reservation.dto.SlotDto;
import com.azki.reservation.service.SlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/slots")
@AllArgsConstructor
@Tag(name = "Slots", description = "Endpoints for viewing available slots")
public class SlotController {

    private final SlotService slotService;

    @GetMapping
    @Operation(
            summary = "Get available slots",
            description = "Returns a paginated list of available time slots, fetched efficiently from the cache."
    )
    @ApiResponse(responseCode = "200",
            description = "Successfully retrieved list",
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = Page.class)))
    public HttpResponse<Page<SlotDto>> list(Pageable pageable) {
        return new HttpResponse<>(slotService.getAvailable(pageable));
    }
}
