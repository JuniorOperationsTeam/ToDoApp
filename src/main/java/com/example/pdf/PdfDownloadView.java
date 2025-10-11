package com.example.pdf;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("pdf-download")
public class PdfDownloadView extends VerticalLayout {
    public PdfDownloadView() {
        Anchor downloadLink = new Anchor("/api/pdf?content=PDF%20gerado%20via%20Vaadin", "Baixar PDF");
        downloadLink.getElement().setAttribute("download", true);
        Button button = new Button("Imprimir PDF", event -> downloadLink.getElement().callJsFunction("click"));
        add(button, downloadLink);
    }
}

