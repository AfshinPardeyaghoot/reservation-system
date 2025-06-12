package com.azki.reservation.controller;

import com.azki.reservation.dto.http.HttpResponse;
import com.azki.reservation.dto.SlotDto;
import com.azki.reservation.service.SlotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved list",
                    content = @Content(
                            schema = @Schema(implementation = HttpResponse.class),
                            examples = @ExampleObject(
                                    value = """
                                            {
                                              "success": true,
                                              "message": "OK",
                                              "code": 200,
                                              "data": {
                                                "content": [
                                                  {
                                                    "id": 1,
                                                    "startTime": "2025-06-13T09:00:00",
                                                    "endTime": "2025-06-13T10:00:00"
                                                  },
                                                  {
                                                    "id": 2,
                                                    "startTime": "2025-06-13T10:00:00",
                                                    "endTime": "2025-06-13T11:00:00"
                                                  }
                                                ],
                                                "pageable": {
                                                  "pageNumber": 0,
                                                  "pageSize": 2,
                                                  "sort": {
                                                    "sorted": true,
                                                    "unsorted": false,
                                                    "empty": false
                                                  },
                                                  "offset": 0,
                                                  "paged": true,
                                                  "unpaged": false
                                                },
                                                "totalPages": 5,
                                                "totalElements": 10,
                                                "last": false,
                                                "first": true,
                                                "numberOfElements": 2,
                                                "size": 2,
                                                "number": 0,
                                                "sort": {
                                                  "sorted": true,
                                                  "unsorted": false,
                                                  "empty": false
                                                },
                                                "empty": false
                                              }
                                            }
                                            """
                            )
                    )
            )
    })
    public HttpResponse<Page<SlotDto>> list(Pageable pageable) {
        return new HttpResponse<>(slotService.getAvailable(pageable));
    }
}
