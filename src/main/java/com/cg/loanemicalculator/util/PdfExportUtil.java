package com.cg.loanemicalculator.util;

import com.cg.loanemicalculator.dto.AmortizationScheduleEntryDto;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;

import java.io.ByteArrayOutputStream;
import java.util.List;

public class PdfExportUtil {

    public static byte[] exportToPdf(List<AmortizationScheduleEntryDto> schedule) {
        // Create a document with A4 page size (portrait)
        Document document = new Document(PageSize.A4);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            // Create PdfWriter to write the document content to the byte output stream
            PdfWriter.getInstance(document, baos);
            document.open();

            // Title setup
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16);
            Paragraph title = new Paragraph("Amortization Schedule", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE); // Add extra space after the title

            // Create a table with 7 columns
            PdfPTable table = new PdfPTable(7);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{1f, 2f, 2f, 2f, 2f, 2f, 2f}); // Set relative column widths

            // Header Row: Define columns names with bold font
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10);
            String[] headers = {"Month", "Payment Date", "Beginning Balance", "EMI", "Principal", "Interest", "Ending Balance"};

            // Adding header cells to the table
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER); // Align text to the center
                cell.setBackgroundColor(BaseColor.LIGHT_GRAY); // Set background color
                table.addCell(cell);
            }

            // Content Row: Adding data from schedule
            Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 9); // Regular font for content
            for (AmortizationScheduleEntryDto entry : schedule) {
                table.addCell(new PdfPCell(new Phrase(String.valueOf(entry.getMonth()), contentFont)));
                table.addCell(new PdfPCell(new Phrase(entry.getPaymentDate().toString(), contentFont)));
                table.addCell(new PdfPCell(new Phrase(entry.getBeginningBalance().toPlainString(), contentFont)));
                table.addCell(new PdfPCell(new Phrase(entry.getEmi().toPlainString(), contentFont)));
                table.addCell(new PdfPCell(new Phrase(entry.getPrincipalComponent().toPlainString(), contentFont)));
                table.addCell(new PdfPCell(new Phrase(entry.getInterestComponent().toPlainString(), contentFont)));
                table.addCell(new PdfPCell(new Phrase(entry.getEndingBalance().toPlainString(), contentFont)));
            }

            // Add table to document
            document.add(table);

        } catch (Exception e) {
            throw new RuntimeException("Error generating amortization PDF", e);
        } finally {
            document.close();
        }

        return baos.toByteArray();
    }
}
