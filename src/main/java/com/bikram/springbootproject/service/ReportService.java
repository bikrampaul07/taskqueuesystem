package com.bikram.springbootproject.service;
import com.bikram.springbootproject.model.Task;
import com.bikram.springbootproject.repo.TaskRepo;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class ReportService {
    @Autowired
    private  TaskRepo taskRepository;

    private static final String[] HEADERS = {
            "ID", "Task Name", "Type", "Status",
            "Execution Time (ms)", "Result", "Error", "Created At"
    };
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public ByteArrayInputStream generatePdfReport() {
        List<Task> tasks = taskRepository.findAll();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter writer = new PdfWriter(out);
            PdfDocument pdfDoc = new PdfDocument(writer);
            Document document = new Document(pdfDoc);
            PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont regularFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // Title
            Paragraph title = new Paragraph("Task Report")
                    .setFont(boldFont)
                    .setFontSize(20)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(new DeviceRgb(33, 37, 41))
                    .setMarginBottom(4);
            document.add(title);
            // Subtitle
            Paragraph subtitle = new Paragraph(
                    "Generated: " + java.time.LocalDateTime.now().format(FORMATTER)
                            + "     |     Total Tasks: " + tasks.size())
                    .setFont(regularFont)
                    .setFontSize(9)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.GRAY)
                    .setMarginBottom(16);
            document.add(subtitle);
            // Table
            Table table = new Table(UnitValue.createPercentArray(
                    new float[]{4, 14, 10, 9, 10, 12, 12, 13}))
                    .useAllAvailableWidth();
            // Header row
            DeviceRgb headerBg = new DeviceRgb(52, 58, 64);
            for (String header : HEADERS) {
                Cell cell = new Cell()
                        .add(new Paragraph(header).setFont(boldFont).setFontSize(8))
                        .setBackgroundColor(headerBg)
                        .setFontColor(ColorConstants.WHITE)
                        .setPadding(6)
                        .setTextAlignment(TextAlignment.CENTER);
                table.addHeaderCell(cell);
            }
            // Data rows
            DeviceRgb altRowBg = new DeviceRgb(248, 249, 250);
            for (int i = 0; i < tasks.size(); i++) {
                Task task = tasks.get(i);
                DeviceRgb rowBg = (i % 2 == 0) ? (DeviceRgb) ColorConstants.WHITE : altRowBg;

                addCell(table, safeStr(task.getId() != null ? task.getId().toString() : ""), regularFont, rowBg, 7);
                addCell(table, safeStr(task.getTaskName()), regularFont, rowBg, 8);
                addCell(table, safeStr(task.getType()), regularFont, rowBg, 8);
                addCell(table, safeStr(task.getStatus()), regularFont, rowBg, 8);
                addCell(table, task.getExecutionTime() != null ? task.getExecutionTime().toString() : "-", regularFont, rowBg, 8);
                addCell(table, safeStr(task.getResult()), regularFont, rowBg, 8);
                addCell(table, safeStr(task.getError()), regularFont, rowBg, 8);
                addCell(table, task.getCreatedAt() != null ? task.getCreatedAt().format(FORMATTER) : "-", regularFont, rowBg, 7);
            }
            document.add(table);
            // Footer
            //this is an optional thing we can use it or not that does not matter at all.
            document.add(new Paragraph("© " + java.time.Year.now() + " Bikram's Spring Boot Project")
                    .setFont(regularFont)
                    .setFontSize(8)
                    .setFontColor(ColorConstants.GRAY)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setMarginTop(12));

            document.close();
            log.info("PDF report generated with {} tasks", tasks.size());
        } catch (IOException e) {
            throw new RuntimeException("Failed to generate PDF report: " + e.getMessage(), e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

    private void addCell(Table table, String value, PdfFont font, DeviceRgb bg, float fontSize) {
        table.addCell(new Cell()
                .add(new Paragraph(value).setFont(font).setFontSize(fontSize))
                .setBackgroundColor(bg)
                .setPadding(5));
    }
    private String safeStr(Object value) {
        return value != null ? value.toString() : "-";
    }
}
