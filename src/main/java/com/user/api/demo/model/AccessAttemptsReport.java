package com.user.api.demo.model;

import com.user.api.demo.enumerations.AccessResult;
import com.user.api.demo.interfaces.ReportGenerator;
import com.user.api.demo.service.ReportService;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ExecutionException;
@Component
public class AccessAttemptsReport implements ReportGenerator<AccessAttemptsReport> {

    private int id;
    private LocalDateTime timestamp;
    private AccessResult accessResult;

    public AccessAttemptsReport() {
    }
    public AccessAttemptsReport(int id, LocalDateTime timestamp, AccessResult accessResult) {
        this.id = id;
        this.timestamp = timestamp;
        this.accessResult = accessResult;
    }

    public int getId() {
        return id;
    }


    public String getTimeFormat() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        return timestamp.format(dateFormatter);
    }

    public AccessResult getAccessResult() {
        return accessResult;
    }

    @Override
    public File generateReport(List<AccessAttemptsReport> list) throws ParserConfigurationException, IOException, SAXException,
            IllegalAccessException, ExecutionException, InterruptedException {

        ReportService reportService = new ReportService();

        File pdfFile = reportService.generateBaseReport(list);

        return pdfFile;
    }

}
