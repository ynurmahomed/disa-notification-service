package disa.notification.service.utils;

import disa.notification.service.service.interfaces.PendingHealthFacilitySummary;
import disa.notification.service.service.interfaces.ViralLoaderResultSummary;
import disa.notification.service.service.interfaces.ViralLoaderResults;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
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
    private static String[][] dictionaries=new String[10][2];
    static{
        dictionaries[0][0]="Total Recebidos";
        dictionaries[0][1]="Número total de resultados de Carga Viral (CV) criados no staging server";
        dictionaries[1][0]="No. Processados";
        dictionaries[1][1]="Número de resultados de CV criados no staging server que foram processados (NID identificado e FSR criado no OpenMRS/EPTS)";
        dictionaries[2][0]="Não Processados: No. Sem Resultados";
        dictionaries[2][1]="Número de resultados de CV criados no staging server que não foram processados (não tem FSR criado no OpenMRS/EPTS) porque o resultado não tem valor.";
        dictionaries[3][0]="Não Processados: No. NID não encontrado";
        dictionaries[3][1]="Número de resultados de CV criados no staging server que não foram processados (não tem FSR criado no OpenMRS/EPTS) porque o NID do paciente não foi encontrado no OpenMRS/EPTS.";
        dictionaries[4][0]="No. Pendentes";
        dictionaries[4][1]="Número de resultados de CV criados no staging server que ainda não foram sincronizados com OpenMRS/EPTS";
        dictionaries[5][0]="Data de Entrada";
        dictionaries[5][1]="Data que o resultado de CV foi criado no staging server";
        dictionaries[6][0]="Data de Sincronização";
        dictionaries[6][1]="Data que o staging server sincronizou os resultados de CV com OpenMRS/EPTS";
        dictionaries[7][0]="Estado";
        dictionaries[7][1]="O estado actual do resultado de CV no staging server, incluindo: Processado (FSR criado em OpenMRS/EPTS); Não Processado (sem FSR criado no OpenMRS/EPTS) ou Pendentes (ainda não foi sincronizado com OpenMRS/EPTS).";
        dictionaries[8][0]="Motivo não envio";
        dictionaries[8][1]="Se estado for Não Processado, o motivo pode ser NID não encontrado ou Sem Resultados.";
        dictionaries[9][0]="Data da última sincronização ";
        dictionaries[9][1]="Data da última sincronização feita entre o staging server e OpenMRS/EPTS na US";
    }

    private static void composeDictionarySheet( Workbook workbook) {
        Sheet sheet = workbook.createSheet("Dicionario");
        createRowHeader(workbook, sheet, DICTIONARY_HEADER);
        AtomicInteger counter = new AtomicInteger(2);
        CellStyle cellStyle=getBoldStyle(workbook);
        for(int i=0;i<dictionaries.length;i++){
            Row row = sheet.createRow(counter.getAndIncrement());
            Cell cell=  row.createCell(0);
            cell.setCellValue(dictionaries[i][0]);
            cell.setCellStyle(cellStyle);
            row.createCell(1).setCellValue(dictionaries[i][1]);
            cell.getRow()
                    .setHeightInPoints(cell.getSheet().getDefaultRowHeightInPoints() * 2);
            wrapText(cell);
            CellRangeAddress region = new CellRangeAddress(i, i, 0, 1);
            RegionUtil.setBorderTop(BorderStyle.THIN.ordinal(), region, sheet);
        }

        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        RegionUtil.setBorderBottom(BorderStyle.THIN.ordinal(), new CellRangeAddress(9, 9, 0, 1), sheet);
        RegionUtil.setBorderBottom(BorderStyle.THIN.ordinal(), new CellRangeAddress(10, 10, 0, 1), sheet);
        RegionUtil.setBorderBottom(BorderStyle.THIN.ordinal(), new CellRangeAddress(11, 11, 0, 1), sheet);
        RegionUtil.setBorderBottom(BorderStyle.THIN.ordinal(), new CellRangeAddress(12, 12, 0, 1), sheet);

    }

    private static void wrapText(Cell cell){
        CellStyle cellStyle = cell.getSheet().getWorkbook().createCellStyle();
        cellStyle.setWrapText(true);
        cell.setCellStyle(cellStyle);
    }

    public static Resource getViralResultXLS(
            List<ViralLoaderResultSummary> viralLoaderResultSummary, List<ViralLoaderResults> viralLoadResults,
            List<ViralLoaderResults> unsyncronizedViralLoadResults, List<PendingHealthFacilitySummary> pendingHealthFacilitySummaries) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream stream = new ByteArrayOutputStream();) {
            composeDictionarySheet(workbook);
            composeFirstSheet(viralLoaderResultSummary, workbook);
            composeSecondSheet(viralLoadResults, workbook);
            composeThirdSheet(unsyncronizedViralLoadResults, workbook);
            composeFourthSheet(pendingHealthFacilitySummaries, workbook);
            workbook.write(stream);
            return new ByteArrayResource(stream.toByteArray());
        } catch (IOException ioe) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not generate the file");
        }
    }

    private static void composeFourthSheet(List<PendingHealthFacilitySummary> pendingViralResultSummaries, Workbook workbook) {
        Sheet sheet4= workbook.createSheet("CV Pendentes por US");
        createFirstRow(workbook,sheet4,PENDING_VIRAL_RESULT_SUMMARY,4);
        createRowHeader(workbook, sheet4, PENDING_VIRAL_RESULT_SUMMARY_HEADER);
        AtomicInteger counter4 = new AtomicInteger(2);
        pendingViralResultSummaries.stream()
                .forEach(pendingViralResultSummary -> {
                    Row row = sheet4.createRow(counter4.getAndIncrement());
                    createPendingViralResultSummaryRow(row, pendingViralResultSummary);
                });
        sheet4.autoSizeColumn(0);
        sheet4.autoSizeColumn(1);
        sheet4.autoSizeColumn(2);
        sheet4.autoSizeColumn(3);
        sheet4.autoSizeColumn(4);
    }

    private static void composeThirdSheet(List<ViralLoaderResults> unsyncronizedViralLoadResults, Workbook workbook) {
        Sheet sheet3= workbook.createSheet("CV Pendentes por NID");
        createFirstRow(workbook,sheet3,NOT_SYNCRONIZED_VIRAL_RESULTS,6);
        createRowHeader(workbook, sheet3, UNSYNCRONIZED_VIRAL_RESULTS_HEADER);
        AtomicInteger counter3 = new AtomicInteger(2);
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
    }

    private static void composeSecondSheet(List<ViralLoaderResults> viralLoadResults, Workbook workbook) {
        DateInterval lastWeekInterval=DateTimeUtils.getLastWeekInterVal();
        String startDateFormatted=lastWeekInterval.getStartDateTime().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String endDateFormatted=lastWeekInterval.getEndDateTime().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        Sheet sheet2 = workbook.createSheet("CV Recebidas por NID");
        createFirstRow(workbook,sheet2,VIRAL_RESULT_TITLE+startDateFormatted+" a "+endDateFormatted,8);
        createRowHeader(workbook, sheet2, VIRAL_RESULTS_HEADER);
        AtomicInteger counter2 = new AtomicInteger(2);
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
        sheet2.autoSizeColumn(8);
    }

    private static void composeFirstSheet(List<ViralLoaderResultSummary> viralLoaderResultSummary, Workbook workbook) {
        DateInterval lastWeekInterval=DateTimeUtils.getLastWeekInterVal();
        String startDateFormatted=lastWeekInterval.getStartDateTime().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String endDateFormatted=lastWeekInterval.getEndDateTime().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        Sheet sheet = workbook.createSheet("CV Recebidas por US");
        createFirstRow(workbook,sheet,VIRAL_RESULT_SUMMARY_TITLE+startDateFormatted+" a "+endDateFormatted+" por US",6);
        createSummaryRowHeader(workbook, sheet, VIRAL_RESULT_SUMMARY_HEADER);
        AtomicInteger counter = new AtomicInteger(3);
        viralLoaderResultSummary.stream()
                .forEach(viralResult -> {
                    Row row = sheet.createRow(counter.getAndIncrement());
                    createViralResultSummaryRow(row, viralResult);
                    setCellStyle(workbook);
                });
        sheet.autoSizeColumn(0);
        sheet.autoSizeColumn(1);
        sheet.autoSizeColumn(2);
        sheet.autoSizeColumn(3);
        sheet.autoSizeColumn(4);
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);
        sheet.autoSizeColumn(7);
    }

    private static void createFirstRow(Workbook workbook, Sheet sheet, String title, int lastCol) {
        Row headerRow = sheet.createRow(FIRST_ROW);
        CellStyle headerCellStyle = setHeaderCellStyle(workbook);
        Cell cell = headerRow.createCell(0);
        cell.setCellValue(title);
        sheet.addMergedRegion(new CellRangeAddress(FIRST_ROW, FIRST_ROW, FIRST_COL, lastCol));
        cell.setCellStyle(headerCellStyle);
    }

    private static void createSummaryRowHeader(Workbook workbook, Sheet sheet, String[] columnHeaders) {
        CellStyle headerCellStyle = setHeaderCellStyle(workbook);
        Row headerRow2 = sheet.createRow(SECOND_ROW);
        Cell cell2 = headerRow2.createCell(6);
        cell2.setCellValue("Não Processados");
        cell2.setCellStyle(headerCellStyle);

        Row headerRow3 = sheet.createRow(THIRD_ROW);
        for (int col = 0; col < columnHeaders.length; col++) {
            Cell cell = headerRow3.createCell(col);
            cell.setCellValue(columnHeaders[col]);
            cell.setCellStyle(headerCellStyle);
        }
        sheet.addMergedRegion(new CellRangeAddress(SECOND_ROW, SECOND_ROW, 6, 7));

    }

    private static void createRowHeader(Workbook workbook, Sheet sheet, String[] columnHeaders) {
        Row headerRow = sheet.createRow(SECOND_ROW);
        CellStyle headerCellStyle = setHeaderCellStyle(workbook);
        for (int col = 0; col < columnHeaders.length; col++) {
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(columnHeaders[col]);
            cell.setCellStyle(headerCellStyle);
        }
    }


    private static CellStyle getBoldStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        return headerStyle;
    }
    private static CellStyle setHeaderCellStyle(Workbook workbook) {
        CellStyle headerStyle = workbook.createCellStyle();
        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        headerStyle.setAlignment(HorizontalAlignment.CENTER);
        return headerStyle;
    }
    private static CellStyle setCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.CENTER);
        return style;
    }


    private static void createViralResultSummaryRow(Row row, ViralLoaderResultSummary viralLoaderResult) {
        row.createCell(COL0_DISTRICT).setCellValue(viralLoaderResult.getRequestingDistrictName());
        row.createCell(COL1_HEALTH_FACILITY_CODE).setCellValue(StringUtils.center(viralLoaderResult.getHealthFacilityLabCode(),11," "));
        row.createCell(COL2_HEALTH_FACILITY_NAME).setCellValue(viralLoaderResult.getFacilityName());
        row.createCell(COL3_TOTAL_RECEIVED).setCellValue(StringUtils.center(viralLoaderResult.getTotalReceived()+"",24,' '));
        row.createCell(COL4_TOTAL_PROCESSED).setCellValue(StringUtils.center(viralLoaderResult.getProcessed()+"",18, ' '));
        row.createCell(COL5_TOTAL_PENDING).setCellValue(StringUtils.center(viralLoaderResult.getTotalPending()+"",15,' '));
        row.createCell(COL6_NOT_PROCESSED_NO_RESULT).setCellValue(viralLoaderResult.getNotProcessedNoResult());
        row.createCell(COL7_NOT_PROCESSED_NID_NOT_FOUND).setCellValue(viralLoaderResult.getNotProcessedNidNotFount());

    }

    private static void createViralResultRow(Row row, ViralLoaderResults viralLoaderResult) {
        row.createCell(COL0_REQUEST_ID).setCellValue(viralLoaderResult.getRequestId());
        row.createCell(COL1_NID).setCellValue(viralLoaderResult.getNID());
        row.createCell(COL2_DISTRICT).setCellValue(viralLoaderResult.getRequestingDistrictName());
        row.createCell(COL3_HEALTH_FACILITY_CODE).setCellValue(viralLoaderResult.getHealthFacilityLabCode());
        row.createCell(COL4_HEALTH_FACILITY_NAME).setCellValue(viralLoaderResult.getRequestingFacilityName());
        row.createCell(COL5_CREATED_AT).setCellValue(viralLoaderResult.getCreatedAt().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        row.createCell(COL6_UPDATED_AT).setCellValue(viralLoaderResult.getUpdatedAt()!=null?viralLoaderResult.getUpdatedAt().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")):"");
        row.createCell(COL7_VIRAL_RESULT_STATUS).setCellValue(viralLoaderResult.getViralLoadStatus());
        row.createCell(COL8_VIRAL_RESULT_STATUS_CAUSE).setCellValue(viralLoaderResult.getNotProcessingCause());
    }

    private static void createPendingViralResultSummaryRow(Row row, PendingHealthFacilitySummary pendingViralResultSummary) {
        row.createCell(COL0_DISTRICT).setCellValue(pendingViralResultSummary.getRequestingDistrictName());
        row.createCell(COL1_HEALTH_FACILITY_CODE).setCellValue(pendingViralResultSummary.getHealthFacilityLabCode());
        row.createCell(COL2_HEALTH_FACILITY_NAME).setCellValue(pendingViralResultSummary.getFacilityName());
        row.createCell(COL3_TOTAL_PENDING).setCellValue(pendingViralResultSummary.getTotalPending());
        row.createCell(COL4_LAST_SYNC_DATE).setCellValue(pendingViralResultSummary.getLastSyncDate()!=null?pendingViralResultSummary.getLastSyncDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")):"");
    }

    private static void createUnsyncronizedViralResultRow(Row row, ViralLoaderResults viralLoaderResult) {
        row.createCell(COL0_REQUEST_ID).setCellValue(viralLoaderResult.getRequestId());
        row.createCell(COL1_NID).setCellValue(viralLoaderResult.getNID());
        row.createCell(COL2_DISTRICT).setCellValue(viralLoaderResult.getRequestingDistrictName());
        row.createCell(COL3_HEALTH_FACILITY_CODE).setCellValue(viralLoaderResult.getHealthFacilityLabCode());
        row.createCell(COL4_HEALTH_FACILITY_NAME).setCellValue(viralLoaderResult.getRequestingFacilityName());
        row.createCell(COL5_SENT_DATE).setCellValue(viralLoaderResult.getCreatedAt().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        row.createCell(COL6_STATUS).setCellValue((viralLoaderResult.getViralLoadStatus()));
    }

}
