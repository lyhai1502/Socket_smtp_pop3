package com.example.Config;

/**
 * Static
 */

public class Static {
    public static String SERVER = "127.0.0.1";
    public static int STMP_PORT = 2225;
    public static int POP3_PORT = 3335;
    public static String PASSWORD = "123456";
    public static String USERNAME = "Van Ly Hai";
    public static String SENDER = "lyhai1502@gmail.com";
    public static int AUTOLOAD = 10;

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