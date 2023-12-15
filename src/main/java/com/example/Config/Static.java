package com.example.Config;

/**
 * Static
 */

public class Static {
    public static String SERVER;
    public static int STMP_PORT;
    public static int POP3_PORT;
    public static String PASSWORD;
    public static String USERNAME;
    public static String SENDER;
    public static int AUTOLOAD;

    public static String BOUNDARY = "------------" + System.currentTimeMillis();

    public static String CONFIG_PATH = "./src/main/java/com/example/Config/config.json";

    public static void setProperties(String server, int stmp_port, int pop3_port, String password, String username,
            String sender, int autoload) {
        SERVER = server;
        STMP_PORT = stmp_port;
        POP3_PORT = pop3_port;
        PASSWORD = password;
        USERNAME = username;
        SENDER = sender;
        AUTOLOAD = autoload;
    }
}