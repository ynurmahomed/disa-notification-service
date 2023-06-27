package disa.notification.service.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import disa.notification.service.entity.ViralResultStatistics;
import disa.notification.service.enums.ViralLoadStatus;
import disa.notification.service.service.interfaces.PendingHealthFacilitySummary;
import disa.notification.service.service.interfaces.ViralLoaderResultSummary;
import disa.notification.service.service.interfaces.ViralLoaderResults;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils implements XLSColumnConstants {

    private static String[][] dictionaries = new String[13][2];

    static {
        dictionaries[0][0] = "Total Recebidos";
        dictionaries[0][1] = "Número total de resultados de Carga Viral (CV) criados no Servidor de Integração";
        dictionaries[1][0] = "No. Processados";
        dictionaries[1][1] = "Número de resultados de CV criados no Servidor de Integração que foram processados (NID identificado e FSR criado no SESP)";
        dictionaries[2][0] = "Não Processados: No. Sem Resultados";
        dictionaries[2][1] = "Número de resultados de CV criados no Servidor de Integração que não foram processados (não tem FSR criado no SESP) porque o resultado não tem valor.";
        dictionaries[3][0] = "Não Processados: No. NID não encontrado";
        dictionaries[3][1] = "Número de resultados de CV criados no Servidor de Integração que não foram processados (não tem FSR criado no SESP) porque o NID do paciente não foi encontrado no SESP.";
        dictionaries[4][0] = "Não Processados: No. NID duplicado";
        dictionaries[4][1] = "Número de resultados de CV criados no Servidor de Integração que não foram processados (não tem FSR criado no SESP) porque o NID do paciente está duplicado no SESP.";
        dictionaries[5][0] = "Não Processados: No. ID da requisição duplicado";
        dictionaries[5][1] = "Número de resultados de CV criados no servidor de integração que não foram processados (não tem FSR criado no SESP) porque há um processo com o mesmo código de requisição que já foi processado com sucesso anteriormente.";
        dictionaries[6][0] = "Não Processados: Sinalizado para revisão";
        dictionaries[6][1] = "Número de resultados de CV criados no Servidor de Integração que não foram processados (não tem FSR criado no SESP) porque o resultado não tem valor valido.";
        dictionaries[7][0] = "No. Pendentes";
        dictionaries[7][1] = "Número de resultados de CV criados no Servidor de Integração que ainda não foram sincronizados com SESP";
        dictionaries[8][0] = "Data de Entrada";
        dictionaries[8][1] = "Data que o resultado de CV foi criado no Servidor de Integração";
        dictionaries[9][0] = "Data de Sincronização";
        dictionaries[9][1] = "Data que o Servidor de Integração sincronizou os resultados de CV com SESP";
        dictionaries[10][0] = "Estado";
        dictionaries[10][1] = "O estado actual do resultado de CV no Servidor de Integração, incluindo: Processado (FSR criado em SESP); Não Processado (sem FSR criado no SESP) ou Pendentes (ainda não foi sincronizado com SESP).";
        dictionaries[11][0] = "Motivo não envio";
        dictionaries[11][1] = "Se estado for Não Processado, o motivo pode ser NID não encontrado, NID duplicado, ID de requisição duplicado, Sinalizado para revisão, Sem Resultados.";
        dictionaries[12][0] = "Data da última sincronização ";
        dictionaries[12][1] = "Data da última sincronização feita entre o Servidor de Integração e SESP na US";
    }

    private static void composeDictionarySheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet("Dicionario");
        createRowHeader(workbook, sheet, DICTIONARY_HEADER);
        AtomicInteger counter = new AtomicInteger(2);
        CellStyle cellStyle = getBoldStyle(workbook);
        for (int i = 0; i < dictionaries.length; i++) {
            Row row = sheet.createRow(counter.getAndIncrement());
            Cell cell = row.createCell(0);
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

    private static void wrapText(Cell cell) {
        CellStyle cellStyle = cell.getSheet().getWorkbook().createCellStyle();
        cellStyle.setWrapText(true);
        cell.setCellStyle(cellStyle);
    }

    public static ByteArrayResource getViralResultXLS(
            List<ViralLoaderResultSummary> viralLoaderResultSummary, List<ViralLoaderResults> viralLoadResults,
            List<ViralLoaderResults> unsyncronizedViralLoadResults,
            List<PendingHealthFacilitySummary> pendingHealthFacilitySummaries) {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream stream = new ByteArrayOutputStream();) {
            composeDictionarySheet(workbook);
            composeReceivedByDistrictSheet(viralLoaderResultSummary, workbook);
            composeReceivedByUSSheet(viralLoaderResultSummary, workbook);
            composeReceivedByNIDSheet(viralLoadResults, workbook);
            composePendingByNIDSheet(unsyncronizedViralLoadResults, workbook);
            composePendingByUSSheet(pendingHealthFacilitySummaries, workbook);
            workbook.write(stream);
            return new ByteArrayResource(stream.toByteArray());
        } catch (IOException ioe) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not generate the file");
        }
    }

    public static void composeReceivedByDistrictSheet(List<ViralLoaderResultSummary> viralLoaderResultSummaryList,
            Workbook workbook) {
        DateInterval lastWeekInterval = DateTimeUtils.getLastWeekInterVal();
        String startDateFormatted = lastWeekInterval.getStartDateTime().toLocalDate()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String endDateFormatted = lastWeekInterval.getEndDateTime().toLocalDate()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        Sheet sheet4 = workbook.createSheet("CV Recebidas por Distrito");

        createFirstRow(workbook, sheet4, String.format(STATS_TITLE, startDateFormatted, endDateFormatted), 5);

        // Create headers
        Row headerRow = sheet4.createRow(SECOND_ROW);
        CellStyle headerCellStyle = setHeaderCellStyle(workbook);
        for (ResultsByDistrictSummary r : ResultsByDistrictSummary.values()) {
            Cell cell = headerRow.createCell(r.ordinal());
            cell.setCellValue(r.header());
            cell.setCellStyle(headerCellStyle);
        }

        AtomicInteger counter4 = new AtomicInteger(2);
        Map<String, ViralResultStatistics> groupedByDistrict = viralLoaderResultSummaryList.stream()
                .collect(
                        Collectors.groupingBy(
                                ViralLoaderResultSummary::getRequestingDistrictName,
                                ViralResultStatisticsCollector.toVlResultStatistics()));

        groupedByDistrict.entrySet().stream()
                .forEach(e -> {
                    Row row = sheet4.createRow(counter4.getAndIncrement());
                    createStatResultRow(workbook, row, e.getKey(), e.getValue());
                });

        ViralResultStatistics totals = groupedByDistrict.values().stream()
                .collect(ViralResultStatistics::new,
                        (a, b) -> a.accumulate(b),
                        (a, b) -> a.combine(b));

        Row row = sheet4.createRow(counter4.getAndIncrement());
        createStatLastResultRow(workbook, row, "Total", totals);

        sheet4.autoSizeColumn(0);
        sheet4.autoSizeColumn(1);
        sheet4.autoSizeColumn(2);
        sheet4.autoSizeColumn(3);
        sheet4.autoSizeColumn(4);
        sheet4.autoSizeColumn(5);
        sheet4.autoSizeColumn(6);
        sheet4.autoSizeColumn(7);
        sheet4.autoSizeColumn(8);
        sheet4.autoSizeColumn(9);
        sheet4.autoSizeColumn(10);
        sheet4.autoSizeColumn(11);
        sheet4.autoSizeColumn(12);
        sheet4.autoSizeColumn(13);
        sheet4.autoSizeColumn(14);
        sheet4.autoSizeColumn(15);
    }

    private static void composePendingByUSSheet(List<PendingHealthFacilitySummary> pendingViralResultSummaries,
            Workbook workbook) {
        Sheet sheet4 = workbook.createSheet("CV Pendentes por US");
        createFirstRow(workbook, sheet4, PENDING_VIRAL_RESULT_SUMMARY, 4);
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

    private static void composePendingByNIDSheet(List<ViralLoaderResults> unsyncronizedViralLoadResults,
            Workbook workbook) {
        Sheet sheet3 = workbook.createSheet("CV Pendentes por NID");
        createFirstRow(workbook, sheet3, NOT_SYNCRONIZED_VIRAL_RESULTS, 7);
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
        sheet3.autoSizeColumn(7);
    }

    private static void composeReceivedByNIDSheet(List<ViralLoaderResults> viralLoadResults, Workbook workbook) {
        DateInterval lastWeekInterval = DateTimeUtils.getLastWeekInterVal();
        String startDateFormatted = lastWeekInterval.getStartDateTime().toLocalDate()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String endDateFormatted = lastWeekInterval.getEndDateTime().toLocalDate()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        Sheet sheet2 = workbook.createSheet("CV Recebidas por NID");
        createFirstRow(workbook, sheet2, String.format(VIRAL_RESULT_TITLE, startDateFormatted, endDateFormatted), 9);
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

    private static void composeReceivedByUSSheet(List<ViralLoaderResultSummary> viralLoaderResultSummary,
            Workbook workbook) {
        DateInterval lastWeekInterval = DateTimeUtils.getLastWeekInterVal();
        String startDateFormatted = lastWeekInterval.getStartDateTime().toLocalDate()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        String endDateFormatted = lastWeekInterval.getEndDateTime().toLocalDate()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        Sheet sheet = workbook.createSheet("CV Recebidas por US");
        createFirstRow(workbook, sheet, String.format(VIRAL_RESULT_SUMMARY_TITLE, startDateFormatted, endDateFormatted),
                6);
        createSummaryRowHeader(workbook, sheet);
        AtomicInteger counter = new AtomicInteger(3);
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
        sheet.autoSizeColumn(5);
        sheet.autoSizeColumn(6);
        sheet.autoSizeColumn(7);
        sheet.autoSizeColumn(8);
        sheet.autoSizeColumn(9);
        sheet.autoSizeColumn(10);
    }

    private static void createFirstRow(Workbook workbook, Sheet sheet, String title, int lastCol) {
        Row headerRow = sheet.createRow(FIRST_ROW);
        CellStyle headerCellStyle = setHeaderCellStyle(workbook);
        Cell cell = headerRow.createCell(0);
        cell.setCellValue(title);
        sheet.addMergedRegion(new CellRangeAddress(FIRST_ROW, FIRST_ROW, FIRST_COL, lastCol));
        cell.setCellStyle(headerCellStyle);
    }

    private static void createSummaryRowHeader(Workbook workbook, Sheet sheet) {
        CellStyle headerCellStyle = setHeaderCellStyle(workbook);
        Row headerRow2 = sheet.createRow(SECOND_ROW);
        Cell cell2 = headerRow2.createCell(6);
        cell2.setCellValue("Não Processados");
        cell2.setCellStyle(headerCellStyle);

        Row headerRow3 = sheet.createRow(THIRD_ROW);
        for (ResultsByHFSummary byHfSummary : ResultsByHFSummary.values()) {
            Cell cell = headerRow3.createCell(byHfSummary.ordinal());
            cell.setCellValue(byHfSummary.header());
            cell.setCellStyle(headerCellStyle);
        }
        // Merge not processed header
        sheet.addMergedRegion(new CellRangeAddress(SECOND_ROW, SECOND_ROW, 6, 10));

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

    private static CellStyle getPercentCellStyle(Workbook workbook) {
        CellStyle percent = workbook.createCellStyle();
        DataFormat df = workbook.createDataFormat();
        percent.setDataFormat(df.getFormat("0%"));
        return percent;
    }

    private static CellStyle getBoldPercentCellStyle(Workbook workbook) {
        CellStyle boldPercent = workbook.createCellStyle();
        DataFormat df = workbook.createDataFormat();
        boldPercent.cloneStyleFrom(getBoldStyle(workbook));
        boldPercent.setDataFormat(df.getFormat("0%"));
        return boldPercent;
    }

    private static void createViralResultSummaryRow(Row row, ViralLoaderResultSummary viralLoaderResult) {
        for (ResultsByHFSummary byHfSummary : ResultsByHFSummary.values()) {
            Cell cell = row.createCell(byHfSummary.ordinal());
            switch (byHfSummary) {
                case DISTRICT:
                    cell.setCellValue(viralLoaderResult.getRequestingDistrictName());
                    break;
                case HEALTH_FACILITY_CODE:
                    cell.setCellValue(StringUtils.center(viralLoaderResult.getHealthFacilityLabCode(), 11, " "));
                    break;
                case HEALTH_FACILITY_NAME:
                    cell.setCellValue(viralLoaderResult.getFacilityName());
                    break;
                case TOTAL_RECEIVED:
                    cell.setCellValue(viralLoaderResult.getTotalReceived());
                    break;
                case TOTAL_PROCESSED:
                    cell.setCellValue(viralLoaderResult.getProcessed());
                    break;
                case TOTAL_PENDING:
                    cell.setCellValue(viralLoaderResult.getTotalPending());
                    break;
                case NOT_PROCESSED_NO_RESULT:
                    cell.setCellValue(viralLoaderResult.getNotProcessedNoResult());
                    break;
                case NOT_PROCESSED_NID_NOT_FOUND:
                    cell.setCellValue(viralLoaderResult.getNotProcessedNidNotFount());
                    break;
                case NOT_PROCESSED_DUPLICATED_NID:
                    cell.setCellValue(viralLoaderResult.getNotProcessedDuplicateNid());
                    break;
                case NOT_PROCESSED_DUPLICATED_REQUEST_ID:
                    cell.setCellValue(viralLoaderResult.getNotProcessedDuplicatedRequestId());
                    break;
                case NOT_PROCESSED_FLAGGED_FOR_REVIEW:
                    cell.setCellValue(viralLoaderResult.getNotProcessedFlaggedForReview());
                    break;
                default:
                    break;
            }
        }
    }

    private static void createViralResultRow(Row row, ViralLoaderResults viralLoaderResult) {
        row.createCell(COL0_REQUEST_ID).setCellValue(viralLoaderResult.getRequestId());
        row.createCell(COL1_NID).setCellValue(viralLoaderResult.getNID());
        row.createCell(COL2_DISTRICT).setCellValue(viralLoaderResult.getRequestingDistrictName());
        row.createCell(COL3_HEALTH_FACILITY_CODE).setCellValue(viralLoaderResult.getHealthFacilityLabCode());
        row.createCell(COL4_HEALTH_FACILITY_NAME).setCellValue(viralLoaderResult.getRequestingFacilityName());
        row.createCell(COL5_CREATED_AT).setCellValue(
                viralLoaderResult.getCreatedAt().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        row.createCell(COL6_UPDATED_AT).setCellValue(viralLoaderResult.getUpdatedAt() != null
                ? viralLoaderResult.getUpdatedAt().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                : "");
        row.createCell(COL7_VIRAL_RESULT_STATUS).setCellValue(viralLoaderResult.getViralLoadStatus());
        row.createCell(COL8_VIRAL_RESULT_STATUS_CAUSE).setCellValue(viralLoaderResult.getNotProcessingCause());
        row.createCell(COL9_VIRAL_RESULT_STATUS_CAUSE).setCellValue(viralLoaderResult.getNotProcessingCause() != null
                && viralLoaderResult.getNotProcessingCause().trim().equals("NID_NOT_FOUND")
                && viralLoaderResult.getViralLoadStatus().equals(ViralLoadStatus.PROCESSED.name())
                        ? "Reprocessado apos a correcao do NID"
                        : " ");
    }

    private static void createPendingViralResultSummaryRow(Row row,
            PendingHealthFacilitySummary pendingViralResultSummary) {
        row.createCell(COL0_DISTRICT).setCellValue(pendingViralResultSummary.getRequestingDistrictName());
        row.createCell(COL1_HEALTH_FACILITY_CODE).setCellValue(pendingViralResultSummary.getHealthFacilityLabCode());
        row.createCell(COL2_HEALTH_FACILITY_NAME).setCellValue(pendingViralResultSummary.getFacilityName());
        row.createCell(COL3_TOTAL_PENDING).setCellValue(pendingViralResultSummary.getTotalPending());
        row.createCell(COL4_LAST_SYNC_DATE)
                .setCellValue(pendingViralResultSummary.getLastSyncDate() != null ? pendingViralResultSummary
                        .getLastSyncDate().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")) : "");
    }

    private static void createUnsyncronizedViralResultRow(Row row, ViralLoaderResults viralLoaderResult) {
        row.createCell(COL0_REQUEST_ID).setCellValue(viralLoaderResult.getRequestId());
        row.createCell(COL1_NID).setCellValue(viralLoaderResult.getNID());
        row.createCell(COL2_DISTRICT).setCellValue(viralLoaderResult.getRequestingDistrictName());
        row.createCell(COL3_HEALTH_FACILITY_CODE).setCellValue(viralLoaderResult.getHealthFacilityLabCode());
        row.createCell(COL4_HEALTH_FACILITY_NAME).setCellValue(viralLoaderResult.getRequestingFacilityName());
        row.createCell(COL5_SENT_DATE).setCellValue(
                viralLoaderResult.getCreatedAt().toLocalDate().format(DateTimeFormatter.ofPattern("dd-MM-yyyy")));
        row.createCell(COL6_STATUS).setCellValue((viralLoaderResult.getViralLoadStatus()));
    }

    private static void createStatResultRow(Workbook workbook, Row row, String district,
            ViralResultStatistics viralResultStatistics) {

        for (ResultsByDistrictSummary r : ResultsByDistrictSummary.values()) {
            Cell cell = row.createCell(r.ordinal());
            switch (r) {
                case DISTRICT:
                    cell.setCellValue(district);
                    break;
                case TOTAL_PROCESSED:
                    cell.setCellValue(viralResultStatistics.getProcessed());
                    break;
                case PERCENTAGE_PROCESSED:
                    cell.setCellStyle(getPercentCellStyle(workbook));
                    cell.setCellValue(viralResultStatistics.getProcessedPercentage());
                    break;
                case TOTAL_PENDING:
                    cell.setCellValue(viralResultStatistics.getPending());
                    break;
                case PERCENTAGE_PENDING:
                    cell.setCellStyle(getPercentCellStyle(workbook));
                    cell.setCellValue(viralResultStatistics.getPendingPercentage());
                    break;
                case NOT_PROCESSED_NO_RESULT:
                    cell.setCellValue(viralResultStatistics.getNoProcessedNoResult());
                    break;
                case PERCENTAGE_NOT_PROCESSED_NO_RESULT:
                    cell.setCellStyle(getPercentCellStyle(workbook));
                    cell.setCellValue(viralResultStatistics.getNoProcessedNoResultPercentage());
                    break;
                case NOT_PROCESSED_NID_NOT_FOUND:
                    cell.setCellValue(viralResultStatistics.getNoProcessedNidNotFound());
                    break;
                case PERCENTAGE_NOT_PROCESSED_NID_NOT_FOUND:
                    cell.setCellStyle(getPercentCellStyle(workbook));
                    cell.setCellValue(viralResultStatistics.getNoProcessedNidNotFoundPercentage());
                    break;
                case NOT_PROCESSED_DUPLICATED_NID:
                    cell.setCellValue(viralResultStatistics.getNotProcessedDuplicateNid());
                    break;
                case PERCENTAGE_NOT_PROCESSED_DUPLICATED_NID:
                    cell.setCellStyle(getPercentCellStyle(workbook));
                    cell.setCellValue(viralResultStatistics.getNotProcessedDuplicateNidPercentage());
                    break;
                case NOT_PROCESSED_DUPLICATED_REQUEST_ID:
                    cell.setCellValue(viralResultStatistics.getNotProcessedDuplicatedReqId());
                    break;
                case PERCENTAGE_NOT_PROCESSED_DUPLICATED_REQUEST_ID:
                    cell.setCellStyle(getPercentCellStyle(workbook));
                    cell.setCellValue(viralResultStatistics.getNotProcessedDuplicatedReqIdPercentage());
                    break;
                case NOT_PROCESSED_FLAGGED_FOR_REVIEW:
                    cell.setCellValue(viralResultStatistics.getNotProcessedFlaggedForReview());
                    break;
                case PERCENTAGE_NOT_PROCESSED_FLAGGED_FOR_REVIEW:
                    cell.setCellStyle(getPercentCellStyle(workbook));
                    cell.setCellValue(viralResultStatistics.getNotProcessedFlaggedForReviewPercentage());
                    break;
                case TOTAL_RECEIVED:
                    cell.setCellValue(viralResultStatistics.getTotal());
                    break;
                default:
                    break;
            }
        }
    }

    private static void createStatLastResultRow(Workbook workbook, Row row,
            String district,
            ViralResultStatistics viralResultStatistics) {

        for (ResultsByDistrictSummary r : ResultsByDistrictSummary.values()) {
            Cell cell = row.createCell(r.ordinal());
            switch (r) {
                case DISTRICT:
                    cell.setCellValue(district);
                    cell.setCellStyle(getTotalsCellStyle(workbook));
                    break;
                case TOTAL_PROCESSED:
                    cell.setCellValue(viralResultStatistics.getProcessed());
                    cell.setCellStyle(getBoldStyle(workbook));
                    break;
                case PERCENTAGE_PROCESSED:
                    cell.setCellValue(viralResultStatistics.getProcessedPercentage());
                    cell.setCellStyle(getBoldPercentCellStyle(workbook));
                    break;
                case TOTAL_PENDING:
                    cell.setCellValue(viralResultStatistics.getPending());
                    cell.setCellStyle(getBoldStyle(workbook));
                    break;
                case PERCENTAGE_PENDING:
                    cell.setCellValue(viralResultStatistics.getPendingPercentage());
                    cell.setCellStyle(getBoldPercentCellStyle(workbook));
                    break;
                case NOT_PROCESSED_NO_RESULT:
                    cell.setCellValue(viralResultStatistics.getNoProcessedNoResult());
                    cell.setCellStyle(getBoldStyle(workbook));
                    break;
                case PERCENTAGE_NOT_PROCESSED_NO_RESULT:
                    cell.setCellValue(viralResultStatistics.getNoProcessedNoResultPercentage());
                    cell.setCellStyle(getBoldPercentCellStyle(workbook));
                    break;
                case NOT_PROCESSED_NID_NOT_FOUND:
                    cell.setCellValue(viralResultStatistics.getNoProcessedNidNotFound());
                    cell.setCellStyle(getBoldStyle(workbook));
                    break;
                case PERCENTAGE_NOT_PROCESSED_NID_NOT_FOUND:
                    cell.setCellValue(viralResultStatistics.getNoProcessedNidNotFoundPercentage());
                    cell.setCellStyle(getBoldPercentCellStyle(workbook));
                    break;
                case NOT_PROCESSED_DUPLICATED_NID:
                    cell.setCellValue(viralResultStatistics.getNotProcessedDuplicateNid());
                    cell.setCellStyle(getBoldStyle(workbook));
                    break;
                case PERCENTAGE_NOT_PROCESSED_DUPLICATED_NID:
                    cell.setCellValue(viralResultStatistics.getNotProcessedDuplicateNidPercentage());
                    cell.setCellStyle(getBoldPercentCellStyle(workbook));
                    break;
                case NOT_PROCESSED_DUPLICATED_REQUEST_ID:
                    cell.setCellValue(viralResultStatistics.getNotProcessedDuplicatedReqId());
                    cell.setCellStyle(getBoldStyle(workbook));
                    break;
                case PERCENTAGE_NOT_PROCESSED_DUPLICATED_REQUEST_ID:
                    cell.setCellValue(viralResultStatistics.getNotProcessedDuplicatedReqIdPercentage());
                    cell.setCellStyle(getBoldPercentCellStyle(workbook));
                    break;
                case NOT_PROCESSED_FLAGGED_FOR_REVIEW:
                    cell.setCellValue(viralResultStatistics.getNotProcessedFlaggedForReview());
                    cell.setCellStyle(getBoldStyle(workbook));
                    break;
                case PERCENTAGE_NOT_PROCESSED_FLAGGED_FOR_REVIEW:
                    cell.setCellValue(viralResultStatistics.getNotProcessedFlaggedForReviewPercentage());
                    cell.setCellStyle(getBoldPercentCellStyle(workbook));
                    break;
                case TOTAL_RECEIVED:
                    cell.setCellValue(viralResultStatistics.getTotal());
                    cell.setCellStyle(getBoldStyle(workbook));
                    break;
                default:
                    break;
            }
        }
    }

    private static CellStyle getTotalsCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.RIGHT);
        XSSFFont font = ((XSSFWorkbook) workbook).createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

}
