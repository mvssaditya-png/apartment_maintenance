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

        List<Object[]> rows = repo.getFlatStatement(flatId);

        List<FlatStatementDTO> statement = new ArrayList<>();
        BigDecimal balance = BigDecimal.ZERO;

        for (Object[] r : rows) {

            Integer month = (Integer) r[1];
            Integer year = (Integer) r[2];
            BigDecimal amount = (BigDecimal) r[3];
            String status = (String) r[4];
            LocalDateTime paymentDate = (LocalDateTime) r[5];
            LocalDateTime createdAt = (LocalDateTime) r[6];

            // 🔴 Maintenance Charge (DEBIT)
            balance = balance.subtract(amount);

            statement.add(new FlatStatementDTO(
                    createdAt,
                    "Maintenance Charge - " + month + "/" + year,
                    amount,
                    BigDecimal.ZERO,
                    balance
            ));

            // 🟢 Payment Entry (CREDIT)
            if ("APPROVED".equalsIgnoreCase(status)
                    && paymentDate != null) {

                balance = balance.add(amount);

                statement.add(new FlatStatementDTO(
                        paymentDate,
                        "Maintenance Payment",
                        BigDecimal.ZERO,
                        amount,
                        balance
                ));
            }
        }

        statement.sort(
                Comparator.comparing(FlatStatementDTO::getDate)
        );

        return statement;
    }
}