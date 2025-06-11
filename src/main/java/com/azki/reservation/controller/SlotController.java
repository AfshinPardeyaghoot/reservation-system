package com.azki.reservation.controller;

import com.azki.reservation.dto.http.HttpResponse;
import com.azki.reservation.dto.SlotDto;
import com.azki.reservation.service.SlotService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/slots")
@AllArgsConstructor
public class SlotController {

    private final SlotService slotService;

    @GetMapping
    public HttpResponse<Page<SlotDto>> list(Pageable pageable) {
        return new HttpResponse<>(slotService.getAvailable(pageable));
    }
}
