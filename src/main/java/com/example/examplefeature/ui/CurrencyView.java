package com.example.examplefeature.ui;

import com.example.base.ui.component.ViewToolbar;
import com.example.examplefeature.backend.CurrencyService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Route("currency")
@PageTitle("Currency Converter")
@Menu(order = 1, icon = "vaadin:currency", title = "Currency Converter")
public class CurrencyView extends Main {

    private final CurrencyService currencyService;

    private final NumberField amount;
    private final ComboBox<String> from;
    private final ComboBox<String> to;
    private final Button convertBtn;
    private final Span result;

    public CurrencyView(CurrencyService currencyService) {
        this.currencyService = currencyService;

        // ----- UI -----
        amount = new NumberField("Montante");
        amount.setPlaceholder("ex.: 100");
        amount.setMin(0);
        amount.setStep(1d);
        amount.setClearButtonVisible(true);
        amount.setWidth("12rem");

        from = new ComboBox<>("De");
        to   = new ComboBox<>("Para");

        // Podes expandir esta lista ou carregá-la dinamicamente via serviço (/symbols)
        List<String> codes = List.of("EUR", "USD", "GBP", "CHF", "JPY", "BRL");
        from.setItems(codes);
        to.setItems(codes);
        from.setValue("EUR");
        to.setValue("USD");
        from.setClearButtonVisible(true);
        to.setClearButtonVisible(true);
        from.setWidth("10rem");
        to.setWidth("10rem");

        convertBtn = new Button("Converter", e -> convert());
        convertBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        result = new Span("Insere um montante e seleciona as moedas.");
        result.addClassNames(LumoUtility.FontSize.MEDIUM, LumoUtility.FontWeight.SEMIBOLD);

        // Layout base
        setSizeFull();
        addClassNames(
                LumoUtility.BoxSizing.BORDER,
                LumoUtility.Display.FLEX,
                LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Padding.MEDIUM,
                LumoUtility.Gap.SMALL
        );

        // Toolbar com os controlos
        add(new ViewToolbar(
                "Currency Converter",
                ViewToolbar.group(amount, from, to, convertBtn)
        ));

        // Área de resultado ao centro
        var center = new com.vaadin.flow.component.orderedlayout.VerticalLayout(result);
        center.setAlignItems(FlexComponent.Alignment.START);
        center.setPadding(false);
        center.setSpacing(false);
        add(center);
    }

    private void convert() {
        Double val = amount.getValue();
        String fromCode = from.getValue();
        String toCode = to.getValue();

        if (val == null || val <= 0) {
            notifyError("Indica um montante > 0.");
            return;
        }
        if (fromCode == null || toCode == null) {
            notifyError("Seleciona as duas moedas.");
            return;
        }
        if (fromCode.equals(toCode)) {
            result.setText("Moedas iguais — nada a converter.");
            return;
        }

        try {
            BigDecimal out = currencyService.convert(BigDecimal.valueOf(val), fromCode, toCode);
            // arredonda a 2 casas por defeito; podes melhorar por moeda (ex.: JPY 0 casas)
            result.setText(val + " " + fromCode + " ≈ " + out.setScale(2, RoundingMode.HALF_UP) + " " + toCode);
            Notification.show("Conversão efetuada", 2000, Notification.Position.BOTTOM_END)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception ex) {
            notifyError("Falha na conversão: " + ex.getMessage());
        }
    }

    private void notifyError(String msg) {
        Notification.show(msg, 3000, Notification.Position.BOTTOM_END)
                .addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}

