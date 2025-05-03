package com.cg.loanemicalculator.util;

import com.cg.loanemicalculator.dto.AmortizationScheduleEntryDto;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class PdfExportUtil {

    public static byte[] exportToPdf(List<AmortizationScheduleEntryDto> schedule) {
        Document document = new Document(PageSize.A4.rotate()); // Optional: Rotate for wider layout
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph title = new Paragraph("Amortization Schedule", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            // 8 columns now, to include Repayment Status
            PdfPTable table = new PdfPTable(8);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1f, 2f, 2f, 2f, 2f, 2f, 2f, 1.5f});

            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            String[] headers = {
                    "Month", "Payment Date", "Beginning Balance", "EMI",
                    "Principal", "Interest", "Ending Balance", "Repayment"
            };

            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                table.addCell(cell);
            }

            Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 9);
            for (AmortizationScheduleEntryDto entry : schedule) {
                table.addCell(new Phrase(String.valueOf(entry.getMonth()), contentFont));
                table.addCell(new Phrase(entry.getPaymentDate().toString(), contentFont));
                table.addCell(new Phrase(entry.getBeginningBalance().toPlainString(), contentFont));
                table.addCell(new Phrase(entry.getEmi().toPlainString(), contentFont));
                table.addCell(new Phrase(entry.getPrincipalComponent().toPlainString(), contentFont));
                table.addCell(new Phrase(entry.getInterestComponent().toPlainString(), contentFont));
                table.addCell(new Phrase(entry.getEndingBalance().toPlainString(), contentFont));

                // Repayment column: show ✅ if true, ✗ if false
                String status = entry.isRepaymentDone() ? "✅" : "✗";
                PdfPCell statusCell = new PdfPCell(new Phrase(status, contentFont));
                statusCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(statusCell);
            }

            document.add(table);
        } catch (Exception e) {
            throw new RuntimeException("Error generating amortization PDF", e);
        } finally {
            document.close();
        }

        return baos.toByteArray();
    }
}
