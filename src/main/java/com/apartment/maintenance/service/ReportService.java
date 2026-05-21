package com.apartment.maintenance.service;

import com.apartment.maintenance.dto.DefaulterResponse;
import com.apartment.maintenance.dto.MonthlySummaryResponse;
import com.apartment.maintenance.entity.User;
import com.apartment.maintenance.exception.UnauthorizedActionException;
import com.apartment.maintenance.repository.ExpenseRepository;
import com.apartment.maintenance.repository.MaintenancePaymentRepository;
import com.apartment.maintenance.repository.SocietyBalanceRepository;
import com.apartment.maintenance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final MaintenancePaymentRepository paymentRepo;
    private final UserRepository userRepo;
    private final ExpenseRepository expenseRepo;
    private final SocietyBalanceRepository balanceRepo;

    public List<DefaulterResponse> getDefaulters(UUID userId) {

        User user = userRepo.findById(userId)
                .orElseThrow();

        if (!user.getRole().equalsIgnoreCase("ADMIN")
                && !user.getRole().equalsIgnoreCase("CASHIER")) {

            throw new UnauthorizedActionException(
                    "Only Admin or Cashier can view defaulters"
            );
        }

        return paymentRepo.findDefaulters(user.getSiteId());
    }

    public MonthlySummaryResponse getMonthlySummary(
            UUID userId,
            String month,
            String year
    ) {

        User user = userRepo.findById(userId)
                .orElseThrow();

        UUID siteId = user.getSiteId();

        double collections =
                paymentRepo.getMonthlyCollections(siteId, month, year);

        double expenses =
                expenseRepo.getMonthlyExpenses(siteId, month, year);

        double previousCollections =
                paymentRepo.getCollectionsBefore(siteId, month, year);

        double previousExpenses =
                expenseRepo.getExpensesBefore(siteId, month, year);

        double siteOpeningBalance =
                balanceRepo.getOpeningBalance(siteId);

        double openingBalance =
                siteOpeningBalance + previousCollections - previousExpenses;

        double closingBalance =
                openingBalance + collections - expenses;

        return new MonthlySummaryResponse(
                openingBalance,
                collections,
                expenses,
                closingBalance
        );
    }

    public byte[] exportDefaultersExcel(UUID userId) {

        try {

            List<DefaulterResponse> defaulters =
                    getDefaulters(userId);

            Workbook workbook = new XSSFWorkbook();

            Sheet sheet = workbook.createSheet("Defaulters");

            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("Defaulters Report");

            Row headerRow = sheet.createRow(2);

            String[] headers = {
                    "S.No",
                    "Flat Number",
                    "Owner Name",
                    "Pending Months",
                    "Total Due"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
            }

            int rowIndex = 3;
            int serialNo = 1;

            for (DefaulterResponse defaulter : defaulters) {

                Row row = sheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(serialNo++);
                row.createCell(1).setCellValue(defaulter.getFlatNumber());
                row.createCell(2).setCellValue(defaulter.getOwnerName());
                row.createCell(3).setCellValue(defaulter.getPendingMonths());
                row.createCell(4).setCellValue(defaulter.getTotalDue());
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream =
                    new ByteArrayOutputStream();

            workbook.write(outputStream);
            workbook.close();

            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to export defaulters report",
                    e
            );
        }
    }
}