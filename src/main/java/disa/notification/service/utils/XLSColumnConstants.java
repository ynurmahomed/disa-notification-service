package disa.notification.service.utils;

public interface XLSColumnConstants {
    int FIRST_ROW = 0;
    int FIRST_COL=0;
    int SECOND_ROW = 1;
    int THIRD_ROW = 2;
    String[] VIRAL_RESULTS_HEADER = {"REQUEST_ID", "NID",  "Distrito","Codigo da US","Nome da US","Data de Entrada","Data de Sincronizacao","Estado","Motivo de Nao envio"};
    int COL0_REQUEST_ID = 0;
    int COL1_NID= 1;
    int COL2_DISTRICT = 2;
    int COL3_HEALTH_FACILITY_CODE = 3;
    int COL4_HEALTH_FACILITY_NAME = 4;
    int COL5_CREATED_AT= 5;
    int COL6_UPDATED_AT = 6;
    int COL7_VIRAL_RESULT_STATUS= 7;
    int COL8_VIRAL_RESULT_STATUS_CAUSE = 8;

    String[] VIRAL_RESULT_SUMMARY_HEADER = {"Distrito","Código da US","Nome da US","Total Recebidos","No. Processados ", "No. Pendentes", "No. Sem Resultados", "No. NID nao encontrado"};
    int COL0_DISTRICT = 0;
    int COL1_HEALTH_FACILITY_CODE= 1;
    int COL2_HEALTH_FACILITY_NAME = 2;
    int COL3_TOTAL_RECEIVED= 3;
    int COL4_TOTAL_PROCESSED = 4;
    int COL5_TOTAL_PENDING= 5;
    int COL6_NOT_PROCESSED_NO_RESULT= 6;
    int COL7_NOT_PROCESSED_NID_NOT_FOUND= 7;

    String[] UNSYNCRONIZED_VIRAL_RESULTS_HEADER = {"REQUEST_ID", "NID", "Distrito","Codigo da US","Nome da US","Data de Envio","Estado"};
    int COL5_SENT_DATE= 5;
    int COL6_STATUS = 6;

    String VIRAL_RESULT_SUMMARY_TITLE="Resultados de CV Recebidos no Staging Server no Periodo de ";
    String VIRAL_RESULT_TITLE="Resultados de CV Recebidos no Staging Server no Periodo de  ";
    String NOT_SYNCRONIZED_VIRAL_RESULTS="Resultados de CV Pendentes no Staging Server há mais de 48 horas por NID (Cumulativo)";
    String PENDING_VIRAL_RESULT_SUMMARY="Resultados de CV Pendentes no Staging Server há mais de 48 horas por US ";


    String[] PENDING_VIRAL_RESULT_SUMMARY_HEADER = {"Distrito","Código da US","Nome da US","No. CVs Pendentes", "Data da Última Sincronização"};
    int COL3_TOTAL_PENDING= 3;
    int COL4_LAST_SYNC_DATE = 4;

    String [] DICTIONARY_HEADER={"Variável","Definição"};


}
