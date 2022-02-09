package disa.notification.service.utils;


import disa.notification.service.service.interfaces.ViralLoaderResultSummary;
import disa.notification.service.service.interfaces.ViralLoaderResults;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils implements XLSColumnConstants {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static Resource getViralResultXLS(
            List<ViralLoaderResultSummary> viralLoaderResultSummary, List<ViralLoaderResults> viralLoadResults,
            List<ViralLoaderResults> unsyncronizedViralLoadResults) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream stream = new ByteArrayOutputStream();) {

          //Primeira sheet sera o resumo
            Sheet sheet = workbook.createSheet("Resumo");
            createRowHeader(workbook, sheet, VIRAL_RESULT_SUMMARY_HEADER);
            AtomicInteger counter = new AtomicInteger(1);
            viralLoaderResultSummary.stream()
                    .forEach(viralResult -> {
                        Row row = sheet.createRow(counter.getAndIncrement());
                        createViralResultSummaryRow(row, viralResult);
                    });
            sheet.autoSizeColumn(0);
            sheet.autoSizeColumn(1);
            sheet.autoSizeColumn(2);
            sheet.autoSizeColumn(3);
            sheet.autoSizeColumn(4);
            //Segunda sheet sera os detalhes
            Sheet sheet2 = workbook.createSheet("Resultados");
            createRowHeader(workbook, sheet2, VIRAL_RESULTS_HEADER);
            AtomicInteger counter2 = new AtomicInteger(1);
            viralLoadResults.stream()
                    .forEach(viralResult -> {
                        Row row = sheet2.createRow(counter2.getAndIncrement());
                        createViralResultRow(row, viralResult);
                    });

            sheet2.autoSizeColumn(0);
            sheet2.autoSizeColumn(1);
            sheet2.autoSizeColumn(2);
            sheet2.autoSizeColumn(3);
            sheet2.autoSizeColumn(4);
            sheet2.autoSizeColumn(5);
            sheet2.autoSizeColumn(6);
            sheet2.autoSizeColumn(7);

            //A Terceira sheet sera dos nao sincronizados
            Sheet sheet3= workbook.createSheet("Dados nao Sincronizados");
            createRowHeader(workbook, sheet3, UNSYNCRONIZED_VIRAL_RESULTS_HEADER);
            AtomicInteger counter3 = new AtomicInteger(1);
            unsyncronizedViralLoadResults.stream()
                    .forEach(viralResult -> {
                        Row row = sheet3.createRow(counter3.getAndIncrement());
                        createUnsyncronizedViralResultRow(row, viralResult);
                    });
            sheet3.autoSizeColumn(0);
            sheet3.autoSizeColumn(1);
            sheet3.autoSizeColumn(2);
            sheet3.autoSizeColumn(3);
            sheet3.autoSizeColumn(4);
            sheet3.autoSizeColumn(5);
            sheet3.autoSizeColumn(6);
            sheet3.autoSizeColumn(7);
            workbook.write(stream);
            return new ByteArrayResource(stream.toByteArray());
        } catch (IOException ioe) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not generate the file");
        }
    }


    private static void createRowHeader(Workbook workbook, Sheet sheet, String[] columnHeaders) {
        Row headerRow = sheet.createRow(FIRST_ROW);
        CellStyle headerCellStyle = setHeaderCellStyle(workbook);
        for (int col = 0; col < columnHeaders.length; col++) {
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(columnHeaders[col]);
            cell.setCellStyle(headerCellStyle);
        }
    }

    //HSSFCellStyle style = workbook.createCellStyle();

    private static CellStyle setHeaderCellStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        return headerStyle;
    }

    private static void createViralResultSummaryRow(Row row, ViralLoaderResultSummary viralLoaderResult) {
        row.createCell(COL0_HEALTH_FACILITY).setCellValue(viralLoaderResult.getFacilityName());
        row.createCell(COL1_TOTAL_RECEIVED).setCellValue(viralLoaderResult.getTotalReceived());
        row.createCell(COL2_TOTAL_PROCESSED).setCellValue(viralLoaderResult.getProcessed());
        row.createCell(COL3_NOT_PROCESSED_NO_RESULT).setCellValue(viralLoaderResult.getNotProcessedNoResult());
        row.createCell(COL4_NOT_PROCESSED_NID_NOT_FOUND).setCellValue(viralLoaderResult.getNotProcessedNidNotFount());
    }

    private static void createViralResultRow(Row row, ViralLoaderResults viralLoaderResult) {
        row.createCell(COL0_REQUEST_ID).setCellValue(viralLoaderResult.getRequestId());
        row.createCell(COL1_NID).setCellValue(viralLoaderResult.getNID());
        row.createCell(COL2_NAME).setCellValue(viralLoaderResult.getFirstName());
        row.createCell(COL3_LAST_NAME).setCellValue(viralLoaderResult.getLastName());
        row.createCell(COL4_HEALTH_FACILITY_NAME).setCellValue(viralLoaderResult.getRequestingFacilityName());
        row.createCell(COL5_HEALTH_FACILITY_CODE).setCellValue(viralLoaderResult.getHealthFacilityLabCode());
        row.createCell(COL6_VIRAL_RESULT_STATUS).setCellValue(viralLoaderResult.getViralLoadStatus());
        row.createCell(COL7_VIRAL_RESULT_STATUS_CAUSE).setCellValue(viralLoaderResult.getNotProcessingCause());
    }


    private static void createUnsyncronizedViralResultRow(Row row, ViralLoaderResults viralLoaderResult) {
        row.createCell(COL0_REQUEST_ID).setCellValue(viralLoaderResult.getRequestId());
        row.createCell(COL1_NID).setCellValue(viralLoaderResult.getNID());
        row.createCell(COL2_NAME).setCellValue(viralLoaderResult.getFirstName());
        row.createCell(COL3_LAST_NAME).setCellValue(viralLoaderResult.getLastName());
        row.createCell(COL4_HEALTH_FACILITY_NAME).setCellValue(viralLoaderResult.getRequestingFacilityName());
        row.createCell(COL5_HEALTH_FACILITY_CODE).setCellValue(viralLoaderResult.getHealthFacilityLabCode());
        row.createCell(COL6_SENT_DATE).setCellValue(viralLoaderResult.getCreatedAt().format(DateTimeFormatter.ISO_DATE));
        row.createCell(COL7_STATUS).setCellValue((viralLoaderResult.getViralLoadStatus()));
    }

}
