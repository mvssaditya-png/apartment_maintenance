package com.apartment.maintenance.service;

import com.apartment.maintenance.entity.MaintenancePayment;
import com.apartment.maintenance.entity.Site;
import com.apartment.maintenance.entity.User;
import com.apartment.maintenance.repository.SiteRepository;
import com.apartment.maintenance.repository.UserRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class ReceiptPdfService {

    private final UserRepository userRepo;
    private final SiteRepository siteRepo;
    private final S3Service s3Service;

    public String generateReceiptPdf(MaintenancePayment payment) {

        try {

            Site site = siteRepo.findById(payment.getSiteId())
                    .orElse(null);

            User user = userRepo.findById(payment.getSubmittedByUserId())
                    .orElse(null);

            String receiptNumber =
                    "RCPT-"
                            + payment.getPaymentYear()
                            + "-"
                            + payment.getPaymentMonth()
                            + "-"
                            + payment.getPaymentId().toString().substring(0, 8);

            String fileName = receiptNumber + ".pdf";

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            Document document = new Document(PageSize.A4);

            PdfWriter.getInstance(document, outputStream);

            document.open();

            Font titleFont = new Font(Font.HELVETICA, 22, Font.BOLD);
            Font headingFont = new Font(Font.HELVETICA, 14, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 12);

            String apartmentName =
                    site != null
                            ? site.getSiteName()
                            : "Apartment";

            Paragraph apartmentTitle =
                    new Paragraph(apartmentName, titleFont);

            apartmentTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(apartmentTitle);

            Paragraph receiptTitle =
                    new Paragraph("Payment Receipt", headingFont);

            receiptTitle.setAlignment(Element.ALIGN_CENTER);
            document.add(receiptTitle);

            document.add(new Paragraph(" "));

            document.add(new Paragraph(
                    "Receipt No: " + receiptNumber,
                    normalFont
            ));

            document.add(new Paragraph(
                    "Payment ID: " + payment.getPaymentId(),
                    normalFont
            ));

            document.add(new Paragraph(" "));

            document.add(new Paragraph(
                    "Resident Details",
                    headingFont
            ));

            document.add(new Paragraph(
                    "Name: "
                            + (user != null
                            ? user.getName()
                            : "-"),
                    normalFont
            ));

            document.add(new Paragraph(
                    "Flat No: "
                            + (user != null
                            ? user.getFlatNumber()
                            : "-"),
                    normalFont
            ));

            document.add(new Paragraph(
                    "Phone: "
                            + (user != null
                            ? user.getPhoneNumber()
                            : "-"),
                    normalFont
            ));

            document.add(new Paragraph(" "));

            document.add(new Paragraph(
                    "Payment Details",
                    headingFont
            ));

            document.add(new Paragraph(
                    "Request Type: "
                            + payment.getRequestType(),
                    normalFont
            ));

            document.add(new Paragraph(
                    "Month/Year: "
                            + payment.getPaymentMonth()
                            + "/"
                            + payment.getPaymentYear(),
                    normalFont
            ));

            document.add(new Paragraph(
                    "Amount Paid: Rs. "
                            + payment.getAmount(),
                    normalFont
            ));

            document.add(new Paragraph(
                    "Payment Mode: "
                            + payment.getPaymentMode(),
                    normalFont
            ));

            if (payment.getPaymentDate() != null) {

                document.add(new Paragraph(
                        "Payment Date: "
                                + payment.getPaymentDate()
                                .format(
                                        DateTimeFormatter.ofPattern(
                                                "dd MMM yyyy, hh:mm a"
                                        )
                                ),
                        normalFont
                ));
            }

            document.add(new Paragraph(" "));

            document.add(new Paragraph(
                    "Status: PAID",
                    headingFont
            ));

            document.add(new Paragraph(" "));

            Paragraph footer =
                    new Paragraph(
                            "This is a system generated receipt.",
                            normalFont
                    );

            footer.setAlignment(Element.ALIGN_CENTER);

            document.add(footer);

            document.close();

            byte[] pdfBytes = outputStream.toByteArray();

            if (pdfBytes.length == 0) {
                throw new RuntimeException("Generated PDF is empty.");
            }

            String receiptUrl =
                    s3Service.uploadBytes(
                            pdfBytes,
                            "receipts",
                            fileName,
                            "application/pdf"
                    );

            payment.setReceiptNumber(receiptNumber);

            return receiptUrl;

        } catch (Exception e) {
            throw new RuntimeException("Failed to generate receipt PDF", e);
        }
    }
}