package com.user.api.demo.controller;

import com.user.api.demo.model.*;
import com.user.api.demo.service.AccessAttemptService;
import com.user.api.demo.service.ReportService;
import com.user.api.demo.service.UserService;
import org.apache.poi.xwpf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class ReportController {

    @Autowired
    UserService userService;
    @Autowired
    ReportService reportService;
    @Autowired
    AccessAttemptService accessAttemptService;
    @Autowired
    private AccessAttemptsReport accessAttemptsReport;
    @Autowired
    private UserReport userReport;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ActionGeneratedObserver actionGeneratedObserver;

    // Restituisce la lista di tutti i report
    @GetMapping("/reports")
    public List<Action> getAllGenerateReports() {
        return reportService.findAll();
    }

    // Endpoint richiamato nel front-end per registrare l'evento di download.
    @PostMapping("/register-action-event")
    public void registerActionEvent(@RequestBody Action action) {
        // Registra il successo o l'interruzione del download sul database
        applicationContext.publishEvent(new ActionGeneratedEvent
                (this, action.getUsername(), action.getDescription(), action.isSuccess(), action.getActionType()));
    }


    /* Endpoint che genera il report degli accessi in locale*/
    @GetMapping("/create-access-report/{userId}")
    public ResponseEntity<?> createAccessReport(@PathVariable Integer userId) {
        try {
            //Parte differente per ogni endpoint
            Optional<User> userOptional = userService.findById(userId);
            User user = userOptional.get();
            List<AccessAttemptsReport> accessAttempts = accessAttemptService.findByUserId(user.getId());

            File pdfFile = accessAttemptsReport.generateReport(accessAttempts);

            return new ResponseEntity<String>(pdfFile.getPath(), HttpStatus.OK);
        } catch (Exception e) {
            // Gestisci qui l'eccezione generica
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Generate user report in Pdf file with username,status and number of login attempts in local
    // http://localhost:8080/create-users-report/
    @GetMapping("/create-users-report/")
    public ResponseEntity<?> createUserReport() {
        try {
            //Recupera tutti gli utenti dal database
            List<UserReport> users = userService.findSelectedUsers();
            File pdfFile = userReport.generateReport(users);
            return new ResponseEntity<String>(pdfFile.getPath(), HttpStatus.OK);
        } catch (Exception e) {
            // Gestisci qui l'eccezione generica
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /* Scarico il file creato in locale passando come parametro nel body il path del file in locale.
    In react questo endpoint scatta dopo l' 'localhost:8080/create-access-report/{userId}' che crea il file pdf in locale"
     */
    @PostMapping("/download")
    public ResponseEntity<Resource> download(@RequestBody Map<String, String> body) {
        try {
            String path = body.get("path");

            // Creazione della risorsa
            FileSystemResource resource = new FileSystemResource(path);
            int lastIndex = path.lastIndexOf("\\");
            String filename = path.substring(lastIndex + 1);


            // Impostazione dell'intestazione per il download
            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);

            return new ResponseEntity<>(resource, headers, HttpStatus.OK);
        } catch (Exception e) {
            // Gestisci qui l'eccezione generica
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    // Create a report of user login attempts in docx file
    // http://localhost:8080/report/{userId}
    @GetMapping("/report/{userId}")
    public ResponseEntity<byte[]> generateReport(@PathVariable Integer userId) throws IOException {

        //Recupera i dati dal database in base all'id utente
        Optional<User> userOptional = userService.findById(userId);
        User user = userOptional.get();
        List<AccessAttempt> accessAttempts = user.getAccessAttempts();

        //Creazione del documento word utilizzando Apache POI
        XWPFDocument document = new XWPFDocument();

        //Aggiungo titolo con dettagli utente e orario del report
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'alle ore' HH:mm");
        String nowFormatted = now.format(formatter);
        run.setText("REPORT DEGLI ACCESSI DELL' UTENTE: ");
        run.setText(user.getUsername());
        run.setItalic(true);
        run.setText(" generato il " + nowFormatted);
        run.setFontSize(20);
        run.setBold(true);

        // Creo la tabella l'addatto alla pagina e la centro
        XWPFTable table = document.createTable();
        table.setWidth("100%");
        table.setTableAlignment(TableRowAlign.CENTER);

        // Crea l'intestazione della tabella
        String[] columns = {"Id Access", "Timestamp", "AccessResult"};
        XWPFTableRow headerRow = table.getRow(0);

        for (int i = 0; i < columns.length; i++) {
            if (i != 0) {
                headerRow.addNewTableCell();
            }
            XWPFRun run2 = headerRow.getCell(i).getParagraphArray(0).createRun();
            run2.setText(columns[i]);
            run2.setBold(true);
        }

        // Itera sugli oggetti AccessAttempt per creare le righe della tabella
        for (AccessAttempt accessAttempt : accessAttempts) {
            XWPFTableRow tableRow = table.createRow();
            tableRow.getCell(0).setText(String.valueOf(accessAttempt.getId()));
            tableRow.getCell(1).setText(accessAttempt.getTimestamp().toString());
            tableRow.getCell(2).setText(accessAttempt.getAccessResult().toString());

        }
        //Creazione del file
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        document.write(byteArrayOutputStream);
        document.close();
        byte[] bytes = byteArrayOutputStream.toByteArray();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
        String filename = "report.docx";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
    }
}
