package pt.ulisboa.tecnico.surething.prover.utils;

public class Constants {

    public static final String BASE_ADDRESS = "https://10.0.2.2:";
    public static final String MAPPING = "/api/v1";
    public static final String LEDGER = "/ledger";
    public static final String VERIFIER = "/verifier";

    public static int LEDGER_PORT_HTTPS = 8443;
    public static int VERIFIER_PORT_HTTPS = 8444;
    public static final int LEDGER_PORT = 8081;
    public static final int VERIFIER_PORT = 8082;

    public static final String REGISTER = BASE_ADDRESS + VERIFIER_PORT_HTTPS + MAPPING + "/user/register";
    public static final String LOGIN = BASE_ADDRESS + VERIFIER_PORT_HTTPS + MAPPING  + "/user/login";
    public static final String BASE_URL_LEDGER = BASE_ADDRESS + LEDGER_PORT_HTTPS + "/api/v1/ledger";
    public static final String BASE_URL_VERIFIER = BASE_ADDRESS + VERIFIER_PORT_HTTPS + "/api/v1/verifier";
    public static final String BASE_URL_USER = BASE_ADDRESS + VERIFIER_PORT_HTTPS + "/api/v1/user";
    public static final String BASE_URL_REFRESH = BASE_ADDRESS + VERIFIER_PORT + "/api/v1/refresh";
}
