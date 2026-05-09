package com.apartment.maintenance.controller;

import com.apartment.maintenance.dto.FlatStatementDTO;
import com.apartment.maintenance.service.FlatStatementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/flats")
@RequiredArgsConstructor
public class FlatStatementController {

    private final FlatStatementService flatStatementService;

    /**
     * Get Flat Statement
     */
    @GetMapping("/{flatId}/statement")
    public ResponseEntity<List<FlatStatementDTO>> getFlatStatement(
            @PathVariable UUID flatId) {

        List<FlatStatementDTO> response =
                flatStatementService.getFlatStatement(flatId);

        return ResponseEntity.ok(response);
    }
}
