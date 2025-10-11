package com.example.pdf;

import com.lowagie.text.DocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PdfController {
    @Autowired
    private PdfService pdfService;

    @GetMapping("/api/pdf")
    public ResponseEntity<byte[]> getPdf(@RequestParam(defaultValue = "Exemplo de PDF") String content) throws DocumentException {
        byte[] pdfBytes = pdfService.generateSimplePdf(content);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=exemplo.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfBytes);
    }
}

