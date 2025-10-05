package com.parabank.utils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader {
 private final String resourcePath;

 public ExcelReader(String resourcePath) {
     if (resourcePath == null || resourcePath.isBlank()) {
         throw new IllegalArgumentException("resourcePath must not be null/blank");
     }
     this.resourcePath = resourcePath;
 }

 public Object[][] getTestData(String sheetName) {
     if (sheetName == null || sheetName.isBlank()) {
         throw new IllegalArgumentException("sheetName must not be null/blank");
     }

     try (InputStream is = Thread.currentThread()
             .getContextClassLoader()
             .getResourceAsStream(resourcePath)) {

         if (is == null) {
             throw new RuntimeException("Excel not found on classpath: " + resourcePath +
                     " (place it under src/test/resources/)");
         }

         try (Workbook wb = new XSSFWorkbook(is)) {
             Sheet sheet = wb.getSheet(sheetName);
             if (sheet == null) throw new RuntimeException("Sheet not found: " + sheetName + " in " + resourcePath);

             int rows = sheet.getPhysicalNumberOfRows();
             if (rows < 2) return new Object[0][0];

             Row header = sheet.getRow(0);
             int cols = header.getPhysicalNumberOfCells();
             Object[][] data = new Object[rows - 1][cols];

             DataFormatter fmt = new DataFormatter();
             for (int i = 1; i < rows; i++) {
                 Row r = sheet.getRow(i);
                 for (int j = 0; j < cols; j++) {
                     Cell c = (r == null) ? null : r.getCell(j);
                     data[i - 1][j] = (c == null) ? "" : fmt.formatCellValue(c).trim();
                 }
             }
             return data;
         }
     } catch (IOException e) {
         throw new RuntimeException("Excel read error: " + e.getMessage(), e);
     }
 }
}
