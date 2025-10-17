package com.example.pdf;

import com.lowagie.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.example.examplefeature.TaskService;
import org.springframework.data.domain.Pageable;

import java.util.List;

@RestController
public class PdfController {
    @Autowired
    private PdfService pdfService;

    @Autowired
    private TaskService taskService;

    @GetMapping("/api/pdf")
    public ResponseEntity<byte[]> getPdf(@RequestParam(defaultValue = "Exemplo de PDF") String content) throws DocumentException {
        byte[] pdfBytes = pdfService.generateSimplePdf(content);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=exemplo.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }

    @GetMapping("/api/pdf/tasks")
    public ResponseEntity<byte[]> getAllTasksPdf() throws DocumentException {
        List<com.example.examplefeature.Task> tasks = taskService.list(Pageable.unpaged());
        byte[] pdfBytes = pdfService.generateTasksPdf(tasks);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=tasks.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}
