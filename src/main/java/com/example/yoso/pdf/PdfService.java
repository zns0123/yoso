package com.example.yoso.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class PdfService {

    /**
     * Generate a simple PDF containing the provided text.
     * Each line (\n) will be rendered on its own line. This is intentionally minimal
     * so it stays dependency-light and easy to adapt.
     */
    public byte[] generatePdf(String text) throws IOException {
        try (PDDocument doc = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            doc.addPage(page);

            PDPageContentStream content = new PDPageContentStream(doc, page);
            content.beginText();
            content.setFont(PDType1Font.HELVETICA, 12);
            float margin = 50;
            float yStart = page.getMediaBox().getHeight() - margin;
            content.newLineAtOffset(margin, yStart);

            String[] lines = (text == null ? "" : text).split("\n");
            float leading = 14; // line spacing
            float currentY = yStart;
            for (String line : lines) {
                // If we would run off the page, create a new page
                if (currentY <= margin) {
                    content.endText();
                    content.close();
                    page = new PDPage(PDRectangle.LETTER);
                    doc.addPage(page);
                    content = new PDPageContentStream(doc, page);
                    content.beginText();
                    content.setFont(PDType1Font.HELVETICA, 12);
                    currentY = page.getMediaBox().getHeight() - margin;
                    content.newLineAtOffset(margin, currentY);
                }

                content.showText(line);
                content.newLineAtOffset(0, -leading);
                currentY -= leading;
            }

            content.endText();
            content.close();

            try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                doc.save(baos);
                return baos.toByteArray();
            }
        }
    }

}
