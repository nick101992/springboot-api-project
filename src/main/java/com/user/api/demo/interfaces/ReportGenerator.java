package com.user.api.demo.interfaces;

import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface ReportGenerator<T> {
    File generateReport(List<T> objects) throws ParserConfigurationException, IOException,
            SAXException, IllegalAccessException, ExecutionException, InterruptedException;
}