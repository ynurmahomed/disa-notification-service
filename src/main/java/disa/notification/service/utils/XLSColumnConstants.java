package disa.notification.service.utils;

public interface XLSColumnConstants {

    String[] DICTIONARY_HEADER = { "Variável", "Definição" };

    String VIRAL_RESULT_SUMMARY_TITLE = "Resultados Recebidos no Servidor de Integração no Período de Domingo (%s) a Sábado (%s) da semana enterior por US";
    String VIRAL_RESULT_TITLE = "Resultados Recebidos no Servidor de Integração no Periodo de Domingo (%s) a Sábado (%s) da semana anterior";
    String RESULTS_PENDING_BY_NID_TITLE = "Resultados Pendentes no Servidor de Integração há mais de 48 horas por NID (Cumulativo)";
    String RESULTS_PENDING_BY_US_TITLE = "Resultados Pendentes no Servidor de Integração há mais de 48 horas por US ";
    String STATS_TITLE = "Resultados recebidos por Distrito no Período de Domingo (%s) a Sábado (%s) da semana anterior";

    int FIRST_ROW = 0;
    int FIRST_COL = 0;
    int SECOND_ROW = 1;
    int THIRD_ROW = 2;

    enum ResultsReceivedByNid {
        REQUEST_ID("ID da Requisição"),
        TYPE_OF_RESULT("Tipo de Resultado"),
        NID("NID"),
        DISTRICT("Distrito"),
        HEALTH_FACILITY_CODE("Codigo da US"),
        HEALTH_FACILITY_NAME("Nome da US"),
        CREATED_AT("Data de Entrada"),
        UPDATED_AT("Data de Sincronização"),
        VIRAL_RESULT_STATUS("Estado"),
        NOT_PROCESSING_CAUSE("Motivo de Não envio"),
        OBS("Observacoes");

        private final String header;

        ResultsReceivedByNid(String header) {
            this.header = header;
        }

        public String header() {
            return header;
        }
    }

    enum ResultsByHFSummary {
        PROVINCE("Província"),
        DISTRICT("Distrito"),
        HEALTH_FACILITY_CODE("Código da US"),
        HEALTH_FACILITY_NAME("Nome da US"),
        TYPE_OF_RESULT("Tipo de Resultado"),
        TOTAL_RECEIVED("Total Recebidos"),
        TOTAL_PROCESSED("No. Processados "),
        TOTAL_PENDING("No. Pendentes"),
        NOT_PROCESSED_INVALID_RESULT("No. Resultado inválido"),
        NOT_PROCESSED_NID_NOT_FOUND("No. NID nao encontrado"),
        NOT_PROCESSED_DUPLICATED_NID("No. NID duplicado"),
        NOT_PROCESSED_DUPLICATED_REQUEST_ID("No. ID da requisição duplicado");

        private final String header;

        ResultsByHFSummary(String header) {
            this.header = header;
        }

        public String header() {
            return header;
        }
    }

    enum ResultsPendingByUs {
        PROVINCE("Província"),
        DISTRICT("Distrito"),
        US_CODE("Código da US"),
        US_NAME("Nome da US"),
        TOTAL_PENDING("No. Resultados Pendentes"),
        LAST_SYNC("Data da Última Sincronização");

        private final String header;

        ResultsPendingByUs(String header) {
            this.header = header;
        }

        public String header() {
            return header;
        }
    }

    enum ResultsByDistrictSummary {
        PROVINCE("Província"),
        DISTRICT("Distrito"),
        TYPE_OF_RESULT("Tipo de Resultado"),
        TOTAL_PROCESSED("No. Processados "),
        PERCENTAGE_PROCESSED("% Processados "),
        TOTAL_PENDING("No. Pendentes"),
        PERCENTAGE_PENDING("% Pendentes "),
        NOT_PROCESSED_INVALID_RESULT("No. Resultado inválido"),
        PERCENTAGE_NOT_PROCESSED_INVALID_RESULT("% Resultado inválido"),
        NOT_PROCESSED_NID_NOT_FOUND("No. NID não encontrado"),
        PERCENTAGE_NOT_PROCESSED_NID_NOT_FOUND("% NID não encontrado"),
        NOT_PROCESSED_DUPLICATED_NID("No. NID duplicado"),
        PERCENTAGE_NOT_PROCESSED_DUPLICATED_NID("% NID duplicado"),
        NOT_PROCESSED_DUPLICATED_REQUEST_ID("No. ID da requisição duplicado"),
        PERCENTAGE_NOT_PROCESSED_DUPLICATED_REQUEST_ID("% ID da requisição duplicado"),
        TOTAL_RECEIVED("Total Recebidos");

        private final String header;

        ResultsByDistrictSummary(String header) {
            this.header = header;
        }

        public String header() {
            return header;
        }
    }

    enum ResultsPendingByNid {
        REQUEST_ID("ID da Requisição"),
        NID("NID"),
        DISTRICT("Distrito"),
        HEALTH_FACILITY_CODE("Codigo da US"),
        HEALTH_FACILITY_NAME("Nome da US"),
        SENT_DATE("Data de Entrada"),
        STATUS("Estado");

        private final String header;

        ResultsPendingByNid(String header) {
            this.header = header;
        }

        public String header() {
            return header;
        }
    }
}
