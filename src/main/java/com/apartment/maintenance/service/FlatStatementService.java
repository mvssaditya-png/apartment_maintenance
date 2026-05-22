package com.apartment.maintenance.service;
import com.apartment.maintenance.dto.FlatStatementDTO;
import com.apartment.maintenance.repository.MaintenancePaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FlatStatementService {

    private final MaintenancePaymentRepository repo;

    public List<FlatStatementDTO> getFlatStatement(UUID flatId) {

        List<Object[]> rows =
                repo.getFlatStatementEntries(flatId);

        List<FlatStatementDTO> response = new ArrayList<>();

        BigDecimal balance = BigDecimal.ZERO;

        for (Object[] row : rows) {

            LocalDateTime date =
                    ((LocalDateTime) row[0]);

            String description = (String) row[1];

            BigDecimal debit = (BigDecimal) row[2];
            BigDecimal credit = (BigDecimal) row[3];

            balance = balance
                    .subtract(debit)
                    .add(credit);

            response.add(
                    new FlatStatementDTO(
                            date,
                            description,
                            debit,
                            credit,
                            balance
                    )
            );
        }

        return response;
    }
}