package com.user.api.demo.model;
import com.google.gson.annotations.SerializedName;
import com.user.api.demo.interfaces.ReportGenerator;
import com.user.api.demo.service.ReportService;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
@Component
public class UserReport implements ReportGenerator<UserReport> {
    @SerializedName("Username")
    private String username;
    @SerializedName("Number of Login Attempts")
    private int numAttempts;
    @SerializedName("Status of User(True=Active & False=Disabled)")
    private Boolean userActive;

    // Costruttore, getters and setters
    public UserReport(String username, int numAttempts, boolean userActive) {
        this.username = username;
        this.numAttempts = numAttempts;
        this.userActive = userActive;
    }

    public UserReport() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getNumAttempts() {
        return numAttempts;
    }

    public void setNumAttempts(int numAttempts) {
        this.numAttempts = numAttempts;
    }

    public Boolean getUserActive() {
        return userActive;
    }

    public void setUserActive(Boolean userActive) {
        this.userActive = userActive;
    }

    @Override
    public File generateReport(List<UserReport> list) throws ParserConfigurationException, IOException, SAXException,
            IllegalAccessException, ExecutionException, InterruptedException {

        ReportService reportService = new ReportService();

        File pdfFile = reportService.generateBaseReport(list);

        return pdfFile;
    }
}

