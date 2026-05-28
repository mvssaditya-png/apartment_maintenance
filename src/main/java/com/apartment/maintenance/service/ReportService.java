package com.apartment.maintenance.service;

import com.apartment.maintenance.dto.DefaulterResponse;
import com.apartment.maintenance.dto.FlatStatementDTO;
import com.apartment.maintenance.dto.MonthlySummaryResponse;
import com.apartment.maintenance.entity.User;
import com.apartment.maintenance.exception.UnauthorizedActionException;
import com.apartment.maintenance.repository.*;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
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
    private final FlatRepository flatRepo;
    private final FlatService flatService;
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
        User user = userRepo.findById(userId)
                .orElseThrow();

        UUID siteId = user.getSiteId();
        List<DefaulterResponse> defaulters =
                paymentRepo.findDefaulters(siteId);

        System.out.println("EXPORT DEFAULTERS COUNT = " + defaulters.size());

        for (DefaulterResponse d : defaulters) {
            System.out.println(
                    "Flat=" + d.getFlatNumber()
                            + ", Owner=" + d.getOwnerName()
                            + ", MaintenanceMonths=" + d.getMaintenancePendingMonths()
                            + ", MaintenanceDue=" + d.getMaintenanceDue()
                            + ", SpecialCount=" + d.getSpecialRequestCount()
                            + ", SpecialDue=" + d.getSpecialRequestDue()
                            + ", OtherCount=" + d.getOtherPendingCount()
                            + ", OtherDue=" + d.getOtherDue()
                            + ", TotalDue=" + d.getTotalDue()
            );
        }

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet("Defaulters");

            Row headerRow = sheet.createRow(0);

            String[] headers = {
                    "S.No",
                    "Flat Number",
                    "Owner Name",
                    "Maintenance Pending Months",
                    "Maintenance Due",
                    "Special Request Count",
                    "Special Request Due",
                    "Other Pending Count",
                    "Other Due",
                    "Total Due"
            };

            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIndex = 1;
            int serialNo = 1;

            double grandMaintenanceDue = 0;
            double grandSpecialRequestDue = 0;
            double grandOtherDue = 0;
            double grandTotalDue = 0;

            for (DefaulterResponse defaulter : defaulters) {

                long maintenanceMonths =
                        defaulter.getMaintenancePendingMonths() != null
                                ? defaulter.getMaintenancePendingMonths()
                                : 0L;

                double maintenanceDue =
                        defaulter.getMaintenanceDue() != null
                                ? defaulter.getMaintenanceDue()
                                : 0D;

                long specialRequestCount =
                        defaulter.getSpecialRequestCount() != null
                                ? defaulter.getSpecialRequestCount()
                                : 0L;

                double specialRequestDue =
                        defaulter.getSpecialRequestDue() != null
                                ? defaulter.getSpecialRequestDue()
                                : 0D;

                long otherPendingCount =
                        defaulter.getOtherPendingCount() != null
                                ? defaulter.getOtherPendingCount()
                                : 0L;

                double otherDue =
                        defaulter.getOtherDue() != null
                                ? defaulter.getOtherDue()
                                : 0D;

                double totalDue =
                        defaulter.getTotalDue() != null
                                ? defaulter.getTotalDue()
                                : 0D;

                grandMaintenanceDue += maintenanceDue;
                grandSpecialRequestDue += specialRequestDue;
                grandOtherDue += otherDue;
                grandTotalDue += totalDue;

                Row row = sheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(serialNo++);
                row.createCell(1).setCellValue(defaulter.getFlatNumber() != null ? defaulter.getFlatNumber() : "-");
                row.createCell(2).setCellValue(defaulter.getOwnerName() != null ? defaulter.getOwnerName() : "-");
                row.createCell(3).setCellValue(maintenanceMonths);
                row.createCell(4).setCellValue(maintenanceDue);
                row.createCell(5).setCellValue(specialRequestCount);
                row.createCell(6).setCellValue(specialRequestDue);
                row.createCell(7).setCellValue(otherPendingCount);
                row.createCell(8).setCellValue(otherDue);
                row.createCell(9).setCellValue(totalDue);
            }

            Row totalRow = sheet.createRow(rowIndex + 1);

            CellStyle totalStyle = workbook.createCellStyle();
            Font totalFont = workbook.createFont();
            totalFont.setBold(true);
            totalStyle.setFont(totalFont);

            Cell totalLabelCell = totalRow.createCell(2);
            totalLabelCell.setCellValue("Grand Total");
            totalLabelCell.setCellStyle(totalStyle);

            Cell maintenanceTotalCell = totalRow.createCell(4);
            maintenanceTotalCell.setCellValue(grandMaintenanceDue);
            maintenanceTotalCell.setCellStyle(totalStyle);

            Cell specialRequestTotalCell = totalRow.createCell(6);
            specialRequestTotalCell.setCellValue(grandSpecialRequestDue);
            specialRequestTotalCell.setCellStyle(totalStyle);

            Cell otherTotalCell = totalRow.createCell(8);
            otherTotalCell.setCellValue(grandOtherDue);
            otherTotalCell.setCellStyle(totalStyle);

            Cell grandTotalCell = totalRow.createCell(9);
            grandTotalCell.setCellValue(grandTotalDue);
            grandTotalCell.setCellStyle(totalStyle);

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);

            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Unable to export defaulters report", e);
        }
    }

    public byte[] exportAllFlatStatementsExcel(UUID userId) {

        try {
            User user = userRepo.findById(userId).orElseThrow();

            if (!user.getRole().equalsIgnoreCase("ADMIN")
                    && !user.getRole().equalsIgnoreCase("CASHIER")) {
                throw new UnauthorizedActionException(
                        "Only Admin or Cashier can export all flat statements"
                );
            }

            List<Object[]> flats = flatRepo.findFlatOptions(user.getSiteId());

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("All Flat Statements");

            Row headerRow = sheet.createRow(0);

            String[] headers = {
                    "Flat Number",
                    "Owner Name",
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

            for (Object[] flatRow : flats) {

                UUID flatId = (UUID) flatRow[0];
                String flatNumber = (String) flatRow[1];
                String ownerName = (String) flatRow[2];

                List<FlatStatementDTO> statement =
                        flatService.getFlatStatement(flatId);

                for (FlatStatementDTO item : statement) {

                    Row row = sheet.createRow(rowIndex++);

                    row.createCell(0).setCellValue(flatNumber);
                    row.createCell(1).setCellValue(ownerName != null ? ownerName : "");
                    row.createCell(2).setCellValue(
                            item.getDate() != null ? item.getDate().toString() : ""
                    );
                    row.createCell(3).setCellValue(item.getDescription());
                    row.createCell(4).setCellValue(item.getDebit().doubleValue());
                    row.createCell(5).setCellValue(item.getCredit().doubleValue());
                    row.createCell(6).setCellValue(item.getBalanceAfter().doubleValue());
                }
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to export all flat statements", e);
        }
    }
}