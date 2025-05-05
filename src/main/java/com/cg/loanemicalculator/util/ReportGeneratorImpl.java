package com.cg.loanemicalculator.util;

import com.cg.loanemicalculator.dto.LoanSummaryDto;
import com.cg.loanemicalculator.dto.LoanSummaryEntryDto;
import com.cg.loanemicalculator.exception.ReportGenerationException;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;

@Component
@Slf4j
public class ReportGeneratorImpl implements ReportGenerator {

    @Override
    public byte[] generateLoanSummaryPdf(LoanSummaryDto summary) {
        try (var baos = new ByteArrayOutputStream()) {
            // 1) Init PDF
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf   = new PdfDocument(writer);
            Document doc      = new Document(pdf);

            // 2) Title with bold font
            Paragraph title = new Paragraph("Loan Summary for " + summary.getUserName())
                    .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                    .setFontSize(16);
            doc.add(title);

            // 3) Table setup
            float[] colWidths = {40f, 60f, 60f, 60f, 60f, 60f, 60f, 60f};
            Table table = new Table(colWidths);

            String[] headers = {
                    "Loan ID", "Principal", "Outstanding",
                    "Total Interest", "Start Date", "End Date",
                    "EMI", "Prepayments"
            };
            for (String h : headers) {
                table.addHeaderCell(new Cell().add(new Paragraph(h)));
            }

            // 4) Rows
            for (LoanSummaryEntryDto e : summary.getLoans()) {
                table.addCell(e.getLoanId().toString());
                table.addCell(e.getPrincipalAmount().toPlainString());
                table.addCell(e.getOutstandingBalance().toPlainString());
                table.addCell(e.getTotalInterestPaid().toPlainString());
                table.addCell(e.getStartDate().toString());
                table.addCell(e.getEndDate().toString());
                table.addCell(e.getEmiAmount().toPlainString());
                table.addCell(e.getTotalPrepayments().toPlainString());
            }

            doc.add(table);
            doc.close();

            return baos.toByteArray();
        } catch (Exception ex) {
            log.error("Failed to generate PDF", ex);
            throw new ReportGenerationException("Error generating PDF", ex);
        }
    }
}
