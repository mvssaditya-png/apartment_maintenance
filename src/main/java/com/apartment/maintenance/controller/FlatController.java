package com.apartment.maintenance.controller;

import com.apartment.maintenance.dto.FlatOptionDTO;
import com.apartment.maintenance.service.FlatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/flats")
@RequiredArgsConstructor
public class FlatController {

    private final FlatService flatService;

    @GetMapping("/list")
    public List<FlatOptionDTO> getFlats(
            @AuthenticationPrincipal UUID userId
    ) {
        return flatService.getFlatOptions(userId);
    }

    @GetMapping("/{flatId}/statement/export")
    public ResponseEntity<byte[]> exportFlatStatement(
            @PathVariable UUID flatId
    ) {
        byte[] excelBytes = flatService.exportFlatStatementExcel(flatId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        ));
        headers.setContentDisposition(
                ContentDisposition.attachment()
                        .filename("flat-statement.xlsx")
                        .build()
        );

        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }
}
