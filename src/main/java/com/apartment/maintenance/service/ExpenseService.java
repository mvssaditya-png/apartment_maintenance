package com.apartment.maintenance.service;

import com.apartment.maintenance.dto.CreateExpenseRequest;
import com.apartment.maintenance.entity.Expense;
import com.apartment.maintenance.entity.User;
import com.apartment.maintenance.exception.UnauthorizedActionException;
import com.apartment.maintenance.repository.ExpenseRepository;
import com.apartment.maintenance.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
public class ExpenseService {

    private final ExpenseRepository expenseRepo;
    private final UserRepository userRepo;
    private final LedgerService ledgerService;
    public Expense createExpense(
            UUID userId,
            CreateExpenseRequest dto) {

        User user = userRepo.findById(userId)
                .orElseThrow();

        // ROLE CHECK
        if (!user.getRole().equalsIgnoreCase("ADMIN")
                && !user.getRole().equalsIgnoreCase("CASHIER")) {

            throw new UnauthorizedActionException(
                    "Only Admin or Cashier can add expense"
            );
        }

        Expense expense = new Expense();

        expense.setSiteId(user.getSiteId());
        expense.setTitle(dto.getTitle());
        expense.setDescription(dto.getDescription());
        expense.setAmount(dto.getAmount());
        expense.setExpenseDate(dto.getExpenseDate());
        expense.setCategory(dto.getCategory());
        expense.setReceiptUrl(dto.getReceiptUrl());
        expense.setCreatedBy(userId);
        ledgerService.addDebit(
                user.getSiteId(),
                expense.getAmount(),
                "EXPENSE",
                expense.getExpenseId(),
                expense.getTitle()
        );
        return expenseRepo.save(expense);
    }

    public List<Expense> getSiteExpenses(UUID userId) {

        UUID siteId = userRepo.findById(userId)
                .orElseThrow()
                .getSiteId();

        return expenseRepo.findBySiteIdOrderByExpenseDateDesc(siteId);
    }

    public byte[] exportExpensesExcel(UUID userId) {

        try {
            User user = userRepo.findById(userId).orElseThrow();

            if (!user.getRole().equalsIgnoreCase("ADMIN")
                    && !user.getRole().equalsIgnoreCase("CASHIER")) {
                throw new UnauthorizedActionException(
                        "Only Admin or Cashier can export expenses"
                );
            }

            List<Expense> expenses =
                    expenseRepo.findBySiteIdOrderByExpenseDateDesc(user.getSiteId());

            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Expenses");

            Row headerRow = sheet.createRow(0);

            String[] headers = {
                    "S.No",
                    "Date",
                    "Title",
                    "Description",
                    "Category",
                    "Amount",
                    "Receipt URL"
            };

            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            int rowIndex = 1;
            int serialNo = 1;

            for (Expense expense : expenses) {
                Row row = sheet.createRow(rowIndex++);

                row.createCell(0).setCellValue(serialNo++);
                row.createCell(1).setCellValue(
                        expense.getExpenseDate() != null
                                ? expense.getExpenseDate().toString()
                                : ""
                );
                row.createCell(2).setCellValue(expense.getTitle());
                row.createCell(3).setCellValue(expense.getDescription());
                row.createCell(4).setCellValue(expense.getCategory());
                row.createCell(5).setCellValue(expense.getAmount().doubleValue());
                row.createCell(6).setCellValue(
                        expense.getReceiptUrl() != null ? expense.getReceiptUrl() : ""
                );
            }

            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            workbook.write(outputStream);
            workbook.close();

            return outputStream.toByteArray();

        } catch (Exception e) {
            throw new RuntimeException("Failed to export expenses", e);
        }
    }
}
