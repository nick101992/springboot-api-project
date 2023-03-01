package com.user.api.demo.service;

import com.documents4j.api.DocumentType;
import com.documents4j.api.IConverter;
import com.documents4j.job.LocalConverter;
import com.user.api.demo.model.Action;
import com.user.api.demo.repo.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import com.google.gson.annotations.SerializedName;

@Service
public class ReportService {

    @Autowired
    ReportRepository repo;

    public List<Action> findAll() {
        return repo.findAll();
    }
    public Action save(Action action) {
        return repo.save(action);
    }

    public Document generateXMLReport(List<?> objectList)
            throws ParserConfigurationException, IOException, SAXException, IllegalAccessException {

        /*Creo un oggetto di tipo File che rappresenta il file "src/main/resources/baseTemplateDocx.xml" in cui ho definito lo stile e
        il template base dei report.
        Poi, utilizzo un'istanza di DocumentBuilderFactory per creare un DocumentBuilder e quindi un Document, che rappresenta il contenuto del file
        XML come un albero di elementi.
        Infine, normalizzo l'elemento radice del documento, che significa che gli elementi vuoti vengono eliminati e gli spazi vengono compattati.
        */
        File inputFile = new File("src/main/resources/baseTemplateDocx.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document document = dBuilder.parse(inputFile);
        document.getDocumentElement().normalize();


        // Seleziono l'elemento <w:body> del mio xml che mi servirà per aggiungere dei tag figli fra cui la tabella creata nel codice successivo
        Element body = (Element) document.getElementsByTagName("w:body").item(0);

        // Recupero l'elemento <w:t> in cui inserirò il nome del mio report in base a quello che sto creando.
        String elemParagraph = "REPORT";
        //Seleziono il secondo elemento <w:t> che trovo partendo dall'alto nel mio file "baseTemplateDocx.xml"
        Element rowParagraphContent = (Element) document.getElementsByTagName("w:t").item(1);
        rowParagraphContent.appendChild(document.createTextNode(elemParagraph));

        // Seleziono l'elemento <w:table> del mio xml
        Element table = (Element) document.getElementsByTagName("w:tbl").item(0);

        /*Richiamo il metodo generateTableRowAndStyle passando le variabili document e table.
        Questo creerà la mia prima riga della tabella che rappresenta la mia intestazione
         */
        Element tableRow = generateTableRowAndStyle(document, table);

        // Seleziono il primo oggetto della lista passata nel mio metodo
        Object firstObject = objectList.get(0);

        /*Qua recupero la classe del mio oggetto, ottengo il nome attributi della classe e l'inserisco in un array di stringhe.
        Se è stato definito un tag @SerializedName che identifica un nome personalizzato per l'attributo viene selezionato quello,
        sennò viene recuperato direttamente il nome della variabile che rappresenta quell'attributo.
        */
        Field[] fields = firstObject.getClass().getDeclaredFields();
        String[] fieldNames = new String[fields.length];
        for (int k = 0; k < fields.length; k++) {
            SerializedName serializedName = fields[k].getAnnotation(SerializedName.class);
            fieldNames[k] = (serializedName != null) ? serializedName.value() : fields[k].getName();
        }

        // Richiamo il metodo generateCellAndStyle che popola le celle relative alla prima riga(intestazione)
        generateCellAndStyle(document,tableRow,fieldNames,true);

        /* Faccio un ciclo for che scorre la lista degli oggetti inviata al mio metodo.
        Per ogni oggetto creo una nuova riga e popolo le celle con i valori degli attributi di questo oggetto.
         */
        for (Object object : objectList) {

            Element tableRowX = generateTableRowAndStyle(document, table);

            /* Definisco un array di stringhe che indentifica il valore in stringa di ogni attributo che inserirò nel mio file xml
            per popolare le celle.
             */
            String[] fieldValue = new String[fields.length];

            for (int j = 0; j < fields.length; j++) {
                Field field = fields[j];
                field.setAccessible(true);
                Object value = field.get(object);
                fieldValue[j] = value.toString();
            }

            generateCellAndStyle(document,tableRowX,fieldValue,false);

        }
        //Inserisco la tabella che ho creato prima del tag w:sectPr che rappresenta il footer del documento word
        NodeList sectPr = body.getElementsByTagName("w:sectPr");
        Element sectPrElement = (Element) sectPr.item(0);
        body.insertBefore(table, sectPrElement);

        return document;
    }

    public static void addDirectory(File source, ZipOutputStream zos, String parentDirectory) throws IOException {
        File[] files = source.listFiles();
        for (File file : files) {
            String fileName = file.getName();
            if (file.isDirectory()) {
                zos.putNextEntry(new ZipEntry(parentDirectory + fileName + "/"));
                addDirectory(file, zos, parentDirectory + fileName + "/");
                continue;
            }

            byte[] buffer = new byte[4096];
            FileInputStream fis = new FileInputStream(file);
            zos.putNextEntry(new ZipEntry(parentDirectory + fileName));
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }
            zos.closeEntry();
            fis.close();
        }
    }

    public static void generateXML(Document document, String xmlFilePath)  {
        try {
            // create the xml file
            //transform the DOM Object to an XML File
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File(xmlFilePath));

            // If you use
            // StreamResult result = new StreamResult(System.out);
            // the output will be pushed to the standard output ...
            // You can use that for debugging

            transformer.transform(domSource, streamResult);
            System.out.println("Done creating XML File");
        } catch (TransformerException pce) {
            pce.printStackTrace();
        }
    }

    public static File generateZipAndConvertToDocxToPdf(String sourceDirectory, String zipFile, String randomString) throws IOException, ExecutionException, InterruptedException {

        FileOutputStream fos = new FileOutputStream(zipFile);
        ZipOutputStream zos = new ZipOutputStream(fos);
        File sourceFile = new File(sourceDirectory);

        addDirectory(sourceFile, zos, "");

        zos.close();
        fos.close();

        File zipFileCreated = new File("C:\\Users\\NBlois\\Desktop\\AccessReport\\"
                                        + randomString + "_Report.zip");

        File docxFile = new File("C:\\Users\\NBlois\\Desktop\\AccessReport\\" + randomString + "_Report.docx");


        if(zipFileCreated.renameTo(docxFile)) {
            System.out.println("File renamed and extension changed successfully");
        } else {
            System.out.println("Failed to rename and change extension of the file");
        }

        File target = new File("C:\\Users\\NBlois\\Desktop\\AccessReport\\"
                + randomString + "_Report.pdf");

        IConverter converter = LocalConverter.builder()
                .baseFolder(new File("C:\\Users\\NBlois\\Desktop\\Nuov"))
                .workerPool(20, 25, 2, TimeUnit.SECONDS)
                .processTimeout(30, TimeUnit.SECONDS)
                .build();

        Future<Boolean> conversion = converter
                .convert(docxFile).as(DocumentType.MS_WORD)
                .to(target).as(DocumentType.PDF)
                .prioritizeWith(1000) // optional
                .schedule();

        if (conversion.get()) {
            System.out.println("File PDF creato");
        } else {
            System.out.println("Errore creazione PDF");
        }

        return target;
    }



    public Element generateTableRowAndStyle (Document document, Element table){
        Element tableRow = document.createElement("w:tr");
        table.appendChild(tableRow);

        Element tablePrEx = document.createElement("w:tblPrEx");
        tableRow.appendChild(tablePrEx);

        Element tblCellMar = document.createElement("w:tblCellMar");
        tablePrEx.appendChild(tblCellMar);

        Element tablePrExTop = document.createElement("w:top");
        tblCellMar.appendChild(tablePrExTop);
        tablePrExTop.setAttribute("w:w", "0");
        tablePrExTop.setAttribute("w:type", "dxa");

        Element tablePrExBottom = document.createElement("w:bottom");
        tblCellMar.appendChild(tablePrExBottom);
        tablePrExBottom.setAttribute("w:w", "0");
        tablePrExBottom.setAttribute("w:type", "dxa");

        Element trPr = document.createElement("w:trPr");
        tableRow.appendChild(trPr);

        Element jc = document.createElement("w:jc");
        trPr.appendChild(jc);
        jc.setAttribute("w:val", "center");

        return tableRow;
    }

    public void generateCellAndStyle (Document document,Element tableRow, String[] contentOfRow ,Boolean styleBold){
        for (String s : contentOfRow) {
            Element Cell = document.createElement("w:tc");
            tableRow.appendChild(Cell);

            Element tcPr = document.createElement("w:tcPr");
            Cell.appendChild(tcPr);

            Element tcW = document.createElement("w:tcW");
            tcPr.appendChild(tcW);
            tcW.setAttribute("w:w", "0");
            tcW.setAttribute("w:type", "auto");

            // paragraph of cell
            Element pCell = document.createElement("w:p");
            Cell.appendChild(pCell);

            Element pPr = document.createElement("w:pPr");
            pCell.appendChild(pPr);

            Element rPr = document.createElement("w:rPr");
            pPr.appendChild(rPr);

            Element sz = document.createElement("w:sz");
            rPr.appendChild(sz);
            sz.setAttribute("w:val", "24");

            Element szCs = document.createElement("w:szCs");
            rPr.appendChild(szCs);
            szCs.setAttribute("w:val", "24");

            // content of cell
            Element rCell = document.createElement("w:r");
            pCell.appendChild(rCell);

            // style of cell
            Element sCell = document.createElement("w:rPr");
            rCell.appendChild(sCell);

            // bold style
            if (styleBold) {
                Element sbCell = document.createElement("w:b");
                sCell.appendChild(sbCell);
                sbCell.setAttribute("w:val", "true");
            }

            Element sz1 = document.createElement("w:sz");
            sCell.appendChild(sz1);
            sz1.setAttribute("w:val", "24");

            Element szCs1 = document.createElement("w:szCs");
            sCell.appendChild(szCs1);
            szCs1.setAttribute("w:val", "24");

            // text of cell
            Element tCell = document.createElement("w:t");
            rCell.appendChild(tCell);
            tCell.appendChild(document.createTextNode(s));
        }
    }

    public Document generateFooter() throws ParserConfigurationException, IOException, SAXException {
        //recupera i dati dal database in base all'id utente

        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy 'alle ore' HH:mm");
        String nowFormatted = now.format(formatter);

        File inputFile = new File("src/main/resources/footerTemplateDocx.xml");
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
        Document document = dBuilder.parse(inputFile);
        document.getDocumentElement().normalize();

        // Get the <w:t> element
        Element wtDateReport = (Element) document.getElementsByTagName("w:t").item(1);

        wtDateReport.appendChild(document.createTextNode(nowFormatted));

        return document;

    }

    public File generateBaseReport (List<?> list) throws ParserConfigurationException, IOException,
            SAXException, IllegalAccessException, ExecutionException, InterruptedException {
        //Prima parte condivisa
        Document documentBody = generateXMLReport(list);
        String xmlFilePath = "src/main/resources/docxTemplate/word/document.xml";
        generateXML(documentBody,xmlFilePath);

        //Seconda parte condivisa
        Document documentFooter = generateFooter();
        String xmlFilePath2 = "src/main/resources/docxTemplate/word/footer1.xml";
        generateXML(documentFooter,xmlFilePath2);

        //Terza parte condivisa
        String randomString = UUID.randomUUID().toString();
        String sourceDirectory = "src/main/resources/docxTemplate";
        String zipFile = "C:\\Users\\NBlois\\Desktop\\AccessReport\\" + randomString + "_Report.zip";
        File pdfFile = generateZipAndConvertToDocxToPdf(sourceDirectory,zipFile,randomString);

        return pdfFile;
    }

}
