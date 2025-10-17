package com.example.examplefeature.ui;

import com.example.base.ui.component.ViewToolbar;
import com.example.examplefeature.Task;
import com.example.examplefeature.TaskService;
import com.example.exampleutils.QRCodeGenerator;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Main;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayInputStream;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Base64;
import java.util.Optional;

import static com.vaadin.flow.spring.data.VaadinSpringDataHelpers.toSpringPageRequest;

@Route("")
@PageTitle("Task List")
@Menu(order = 0, icon = "vaadin:clipboard-check", title = "Task List")
class TaskListView extends Main {

    private final TaskService taskService;

    final TextField description;
    final DatePicker dueDate;
    final Button createBtn;
    final Grid<Task> taskGrid;

    TaskListView(TaskService taskService) {
        this.taskService = taskService;

        description = new TextField();
        description.setPlaceholder("What do you want to do?");
        description.setAriaLabel("Task description");
        description.setMaxLength(Task.DESCRIPTION_MAX_LENGTH);
        description.setMinWidth("20em");

        dueDate = new DatePicker();
        dueDate.setPlaceholder("Due date");
        dueDate.setAriaLabel("Due date");

        createBtn = new Button("Create", event -> createTask());
        createBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        var dateTimeFormatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM).withLocale(getLocale())
                .withZone(ZoneId.systemDefault());
        var dateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withLocale(getLocale());

        taskGrid = new Grid<>();
        taskGrid.setItems(query -> taskService.list(toSpringPageRequest(query)).stream());
        taskGrid.addColumn(Task::getDescription).setHeader("Description");
        taskGrid.addColumn(task -> Optional.ofNullable(task.getDueDate()).map(dateFormatter::format).orElse("Never"))
                .setHeader("Due Date");
        taskGrid.addColumn(task -> dateTimeFormatter.format(task.getCreationDate())).setHeader("Creation Date");
        taskGrid.addComponentColumn(task -> {
            Button qrButton = new Button("Generate QR Code", event -> {
                try {
                    String taskInfo = formatTaskInfo(task);
                    byte[] qrBytes = QRCodeGenerator.generateQRCodeBytes(taskInfo);
                    String base64 = Base64.getEncoder().encodeToString(qrBytes);
                    String dataUrl = "data:image/png;base64," + base64;
                    Image qrImage = new Image(dataUrl, "QR Code");
                    qrImage.setWidth("250px");
                    qrImage.setHeight("250px");
                    Dialog dialog = new Dialog();
                    dialog.add(qrImage);
                    Button closeBtn = new Button("Close", e -> dialog.close());
                    dialog.add(closeBtn);
                    dialog.open();
                } catch (Exception e) {
                    Notification.show("Error generating QR Code: " + e.getMessage(), 4000, Notification.Position.BOTTOM_END)
                            .addThemeVariants(NotificationVariant.LUMO_ERROR);
                }
            });
            qrButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            return qrButton;
        }).setHeader("QR Code");
        taskGrid.setSizeFull();

        Button generateAllBtn = new Button("Generate All QR Codes", event -> {
            try {
                StringBuilder allTasksInfo = new StringBuilder();
                taskService.list(Pageable.unpaged()).forEach(task -> {
                    allTasksInfo.append(formatTaskInfo(task)).append("\n---\n");
                });
                byte[] qrBytes = QRCodeGenerator.generateQRCodeBytes(allTasksInfo.toString());
                String base64 = Base64.getEncoder().encodeToString(qrBytes);
                String dataUrl = "data:image/png;base64," + base64;
                Image qrImage = new Image(dataUrl, "All Tasks QR Code");
                qrImage.setWidth("250px");
                qrImage.setHeight("250px");
                Dialog dialog = new Dialog();
                dialog.add(qrImage);
                Button closeBtn = new Button("Close", e -> dialog.close());
                dialog.add(closeBtn);
                dialog.open();
            } catch (Exception e) {
                Notification.show("Error generating QR Code: " + e.getMessage(), 4000, Notification.Position.BOTTOM_END)
                        .addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        });
        generateAllBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        // Botão para download de todas as tasks em PDF
        var downloadAllTasksLink = new Anchor("/api/pdf/tasks", "Download PDF de todas as tasks");
        downloadAllTasksLink.getElement().setAttribute("download", true);
        var downloadAllTasksBtn = new Button("Imprimir todas as tasks", event -> downloadAllTasksLink.getElement().callJsFunction("click"));
        downloadAllTasksBtn.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        add(downloadAllTasksBtn, downloadAllTasksLink);

        setSizeFull();
        addClassNames(LumoUtility.BoxSizing.BORDER, LumoUtility.Display.FLEX, LumoUtility.FlexDirection.COLUMN,
                LumoUtility.Padding.MEDIUM, LumoUtility.Gap.SMALL);

        add(new ViewToolbar("Task List", ViewToolbar.group(description, dueDate, createBtn, generateAllBtn)));
        add(taskGrid);
    }

    private void createTask() {
        taskService.createTask(description.getValue(), dueDate.getValue());
        taskGrid.getDataProvider().refreshAll();
        description.clear();
        dueDate.clear();
        Notification.show("Task added", 3000, Notification.Position.BOTTOM_END)
                .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }

    private String formatTaskInfo(Task task) {
        return "ID: " + task.getId() +
                "\nDescription: " + task.getDescription() +
                "\nCreation Date: " + (task.getCreationDate() != null ? task.getCreationDate().toString() : "-") +
                "\nDue Date: " + (task.getDueDate() != null ? task.getDueDate().toString() : "-");
    }


}
