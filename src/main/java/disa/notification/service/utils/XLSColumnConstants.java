package disa.notification.service.utils;

public interface XLSColumnConstants {
    int FIRST_ROW = 0;
    String[] VIRAL_RESULTS_HEADER = {"REQUEST_ID", "NID", "Nome", "Apelido", "US da Colheita","Codigo da US","Estado","Motivo de Nao envio"};
    int COL0_REQUEST_ID = 0;
    int COL1_NID= 1;
    int COL2_NAME = 2;
    int COL3_LAST_NAME= 3;
    int COL4_HEALTH_FACILITY_NAME = 4;
    int COL5_HEALTH_FACILITY_CODE = 5;
    int COL6_VIRAL_RESULT_STATUS= 6;
    int COL7_VIRAL_RESULT_STATUS_CAUSE = 7;

    String[] VIRAL_RESULT_SUMMARY_HEADER = {"Unidade Sanitaria","Total Recebidos", "Processados", "Sem Resultados", "NID nao encontrado"};
    int COL0_HEALTH_FACILITY = 0;
    int COL1_TOTAL_RECEIVED= 1;
    int COL2_TOTAL_PROCESSED = 2;
    int COL3_NOT_PROCESSED_NO_RESULT= 3;
    int COL4_NOT_PROCESSED_NID_NOT_FOUND= 4;

    String[] UNSYNCRONIZED_VIRAL_RESULTS_HEADER = {"REQUEST_ID", "NID", "Nome", "Apelido", "US da Colheita","Codigo da US","Data de Envio","Estado"};
    int COL6_SENT_DATE= 6;
    int COL7_STATUS = 7;

}
