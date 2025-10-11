package com.example.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;
import com.example.examplefeature.Task;

@Service
public class PdfService {
    public byte[] generateSimplePdf(String content) throws DocumentException {
        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        document.open();
        document.add(new Paragraph(content));
        document.close();
        return baos.toByteArray();
    }

    public byte[] generateTasksPdf(List<Task> tasks) throws DocumentException {
        Document document = new Document();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, baos);
        document.open();
        document.add(new Paragraph("Lista de Tasks:"));
        document.add(new Paragraph("------------------------------"));
        for (Task task : tasks) {
            String line = String.format("Descrição: %s\nCriada em: %s\nDue Date: %s\n------------------------------",
                    task.getDescription(),
                    task.getCreationDate(),
                    task.getDueDate() != null ? task.getDueDate().toString() : "-");
            document.add(new Paragraph(line));
        }
        document.close();
        return baos.toByteArray();
    }
}
