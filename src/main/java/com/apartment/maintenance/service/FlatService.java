package com.apartment.maintenance.service;
import com.apartment.maintenance.dto.FlatOptionDTO;
import com.apartment.maintenance.dto.FlatStatementDTO;
import com.apartment.maintenance.entity.User;
import com.apartment.maintenance.repository.FlatRepository;
import com.apartment.maintenance.repository.MaintenancePaymentRepository;
import com.apartment.maintenance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FlatService {

    private final FlatRepository flatRepo;
    private final UserRepository userRepo;
    private final MaintenancePaymentRepository paymentRepo;
    public List<FlatOptionDTO> getFlatOptions(UUID userId) {

        User user = userRepo.findById(userId).orElseThrow();

        List<Object[]> rows = flatRepo.findFlatOptions(user.getSiteId());

        return rows.stream()
                .map(row -> new FlatOptionDTO(
                        (UUID) row[0],
                        (String) row[1],
                        (String) row[2]
                ))
                .toList();
    }

    public byte[] exportFlatStatementExcel(UUID flatId) {

        try {
            List<FlatStatementDTO> statement = getFlatStatement(flatId);

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Flat Statement");

            Row headerRow = sheet.createRow(0);

            String[] headers = {
                    "Date",
                    "Description",
                    "Debit",
                    "Credit",
                    "Balance After"
            };

            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            int rowIndex = 1;

            for (FlatStatementDTO item : statement) {
                Row row = sheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(
                        item.getDate() != null ? item.getDate().toString() : ""
                );
                row.createCell(1).setCellValue(item.getDescription());
                row.createCell(2).setCellValue(item.getDebit().doubleValue());
                row.createCell(3).setCellValue(item.getCredit().doubleValue());
                row.createCell(4).setCellValue(item.getBalanceAfter().doubleValue());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to export flat statement", e);
        }
    }

    public List<FlatStatementDTO> getFlatStatement(UUID flatId) {

        List<Object[]> rows =
                paymentRepo.getFlatStatementEntries(flatId);

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
