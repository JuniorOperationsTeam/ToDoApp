package com.example.examplefeature;

import com.example.exampleutils.EmailService;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final EmailService emailService;

    public TaskService(TaskRepository taskRepository, EmailService emailService) {
        this.taskRepository = taskRepository;
        this.emailService = emailService;
    }

    @Transactional
    public void createTask(String description, @Nullable java.time.LocalDate dueDate) {
        var task = new Task(description, Instant.now());
        task.setDueDate(dueDate);
        taskRepository.saveAndFlush(task);
    }

    @Transactional(readOnly = true)
    public List<Task> list(Pageable pageable) {
        return taskRepository.findAllBy(pageable).toList();
    }

    public boolean sendTaskByEmail(Task task, String destinatario) {
        String subject = "Task: " + (task.getDescription() != null ? task.getDescription() : "Task details");

        // Texto simples (fallback)
        String textBody = "Task: " + task.getDescription() + "\n" +
                "ID: " + (task.getId() != null ? task.getId() : "-") + "\n" +
                "Created: " + (task.getCreationDate() != null ?
                LocalDateTime.ofInstant(task.getCreationDate(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "-") + "\n" +
                "Due: " + (task.getDueDate() != null ? task.getDueDate().toString() : "-");

        // HTML bonito
        String htmlBody = "<h2>Task Details</h2>" +
                "<table border='0' cellpadding='4' style='border-collapse:collapse;'>" +
                "<tr><td><strong>ID</strong></td><td>" + (task.getId() != null ? task.getId() : "-") + "</td></tr>" +
                "<tr><td><strong>Description</strong></td><td>" + escapeHtml(task.getDescription()) + "</td></tr>" +
                "<tr><td><strong>Created</strong></td><td>" +
                (task.getCreationDate() != null ? LocalDateTime.ofInstant(task.getCreationDate(), ZoneId.systemDefault()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "-") +
                "</td></tr>" +
                "<tr><td><strong>Due</strong></td><td>" + (task.getDueDate() != null ? task.getDueDate().toString() : "-") + "</td></tr>" +
                "</table>";

        try {
            return emailService.sendEmail(destinatario, subject, textBody, htmlBody);
        } catch (Exception e) {
            // loga e devolve false
            System.err.println("Erro ao enviar email via Mailgun: " + e.getMessage());
            return false;
        }
    }

    // very small HTML escape
    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("\"","&quot;");
    }
}
