package com.example.ReceiveMail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.example.Config.Static;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

public class ReceiveMail {
    private static final String pop3 = "127.0.0.1";
    private static final int port = 3335;
    // private static ArrayList<EmailSocket> listEmail = new
    // ArrayList<EmailSocket>();
    private static ArrayList<String> listEmail = new ArrayList<>();

    private static void saveData(ArrayList<HashMap<String, String>> data, String pathName, String fileName) {
        System.out.println(data);
        File directory = new File(pathName);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, fileName);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try (FileWriter writer = new FileWriter(file)) {
            Gson gson = new Gson();
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static Socket connectToPOP3() {
        Socket socket = null;
        try {
            socket = new Socket(pop3, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return socket;
    }

    private static void loginUser(BufferedReader reader, PrintWriter writer, String email, String password)
            throws IOException {
        String response = "";
        writer.println("USER " + email);
        response = reader.readLine();

        writer.println("PASS " + password);
        response = reader.readLine();
    }

    private static void getNumberOfEmails(BufferedReader reader, PrintWriter writer) throws IOException {
        String response = "";
        writer.println("STAT");
        response = reader.readLine();
        System.out.println(response);
    }

    private static ArrayList<HashMap<String, String>> getListEmails(BufferedReader reader, PrintWriter writer)
            throws IOException {
        String response = "";
        writer.println("LIST");
        ArrayList<HashMap<String, String>> emailList = new ArrayList<>();
        while (!(response = reader.readLine()).equals(".")) {
            if (response.equals("+OK")) {
                continue;
            }

            String[] parts = response.split(" ", 2);
            HashMap<String, String> email = new HashMap<>();
            email.put("id", parts[0]);
            email.put("size", parts[1]);
            emailList.add(email);
        }
        System.out.println(emailList);
        return emailList;
    }

    public static void getEmailFromThePOP3(String email, String password) {
        Socket socket = connectToPOP3();

        if (socket == null) {
            System.out.println("Can't connect to POP3 server");
            return;
        } else {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter writer = new PrintWriter(socket.getOutputStream(), true);
                String response = reader.readLine();

                // loadDataFromJson(email);

                loginUser(reader, writer, email, password);

                getNumberOfEmails(reader, writer);

                ArrayList<HashMap<String, String>> listEmail = getListEmails(reader, writer);

                getEmailFromServer(reader, writer, listEmail);

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    public static String generateeFileName(String dateString) {
        String result = "";
        try {
            // Định dạng ngày giờ ban đầu
            String pattern = "EEE, dd MMM yyyy HH:mm:ss Z";
            SimpleDateFormat format = new SimpleDateFormat(pattern);
            format.setTimeZone(TimeZone.getTimeZone("GMT"));

            // Phân tích chuỗi ngày giờ
            Date date = format.parse(dateString);

            // Định dạng ngày giờ mới
            SimpleDateFormat newFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            newFormat.setTimeZone(TimeZone.getTimeZone("GMT+7"));

            // Chuyển đổi đối tượng Date thành chuỗi ngày giờ mới
            result = newFormat.format(date) + ".msg";

        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public static ArrayList<HashMap<String, String>> loadData(String pathName, String fileName) {
        File directory = new File(pathName);
        File file = new File(directory, fileName);

        ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();

        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<HashMap<String, String>>>() {
                }.getType();
                data = gson.fromJson(reader, type);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if (data == null) {
            return new ArrayList<>();
        }
        return data;
    }

    public static void updateStatus(File directory, String id, String status) throws IOException, ParseException {
        File file = new File(directory, "status.json");
        ArrayList<HashMap<String, String>> listStatus = new ArrayList<>();

        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try (FileReader reader = new FileReader(file)) {
                Gson gson = new Gson();
                Type type = new TypeToken<ArrayList<HashMap<String, String>>>() {
                }.getType();
                listStatus = gson.fromJson(reader, type);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (listStatus == null) {
            listStatus = new ArrayList<>();
        }

        HashMap<String, String> json = new HashMap<>();

        json.put("id", id);
        json.put("status", status);


        boolean isExist = false;

        for (int index = 0; index < listStatus.size(); index++) {
            if (listStatus.get(index).get("id").equals(id)) {
                listStatus.set(index, json);
                isExist = true;
                break;
            }
        }

        if (!isExist)
            listStatus.add(json);


        saveData(listStatus, "Data/xuanchien@gmail.com/PROJECT", "status.json");
    }

    private static void saveEmail(File directory, String fileName, String content) {
        File file = new File(directory, fileName);
        if (file.exists()) {
            return;
        }

        try {
            if (file.createNewFile()) {
                try (PrintWriter fileWriter = new PrintWriter(file)) {
                    fileWriter.println(content);
                    updateStatus(directory, fileName, "unRead");
                }
            } else {
                System.out.println("Không thể tạo file");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private static boolean isSubMap(HashMap<String, String> subMap, HashMap<String, String> superMap) {
        for (Map.Entry<String, String> entry : subMap.entrySet()) {
            if (!superMap.containsKey(entry.getKey()) || !superMap.get(entry.getKey()).equals(entry.getValue())) {
                return false;
            }
        }
        return true;
    }

    private static boolean isContain(HashMap<String, String> email, ArrayList<HashMap<String, String>> data) {
        for (HashMap<String, String> e : data) {
            if (isSubMap(email, e)) {
                return true;
            }
        }
        return false;
    }

    private static void getEmailFromServer(
            BufferedReader reader,
            PrintWriter writer,
            ArrayList<HashMap<String, String>> listEmail) throws IOException {

        ArrayList<HashMap<String, String>> data = loadData("Data/xuanchien@gmail.com", "data.json");

        for (HashMap<String, String> email : listEmail) {
            if (data == null || !isContain(email, data)) {
                writer.println("RETR " + email.get("id"));
                StringBuilder emailContent = new StringBuilder();
                String line = reader.readLine();
                while (!(line = reader.readLine()).equals(".")) {
                    emailContent.append(line).append("\n");
                }

                EmailSocket emailSocket = new EmailSocket(emailContent.toString());
                System.out.println(emailSocket.get("Date"));
                // System.out.println(emailSocket.filter());
                File directory = new File("Data/xuanchien@gmail.com/" + emailSocket.filter());
                if (!directory.exists()) {
                    directory.mkdir();
                }
                System.out.println(directory);
                saveEmail(directory, generateeFileName(emailSocket.get("Date")),
                        emailContent.toString());

                // email.put("Status", "unRead");
                data.add(email);
            }
        }

        saveData(data, "Data/xuanchien@gmail.com", "data.json");
    }

    public static void readEmail(EmailSocket email, int index) {
        String content = email.get("Content");
        if (!email.get("Content-Type").isEmpty() &&
                email.get("Content-Type").contains("multipart")) {
            String boundary = email.get("boundary").replaceAll("\"", "");
            String[] lines = content.split("\n");
            readMultipart(lines, boundary, index);
        } else {
            if (!email.get("filename").isEmpty()) {
                System.out.println(email.get("Content-Disposition") + " " + email.get("filename"));

                System.out.print("Email này có attached file, bạn có muốn save không: ");
                Scanner scanner = new Scanner(System.in);
                String input = scanner.nextLine();
                if (input.equals("co")) {
                    System.out.print("Cho biết đường dẫn bạn muốn lưu: ");
                    input = scanner.nextLine();
                    extractFile(input, email.get("filename").replaceAll("\"", ""),
                            email.get("Content"));
                } else {

                }
            } else if (!email.get("Content-Type").isEmpty() &&
                    email.get("Content-Type").contains("text") &&
                    !email.get("Content-Type").contains("html")) {
                System.out.println("Noi dung cua email thu " + index + ":\n" + email.get("Content"));
            }
        }
    }

    public static void readMultipart(String[] lines, String boundary, int index) {
        StringBuilder emailContent = new StringBuilder();
        for (String line : lines) {
            if (line.contains(boundary)) {
                EmailSocket subContent = new EmailSocket(emailContent.toString());
                readEmail(subContent, index);
                emailContent.setLength(0);
            } else {
                emailContent.append(line).append("\n");
            }
        }
    }

    public static void extractFile(String path, String fileName, String content) {
        try {
            File file = new File(path, fileName);
            file.createNewFile();

            String encodedString = content.trim();
            FileOutputStream fos = new FileOutputStream(file);
            encodedString = encodedString.replaceAll("\n", "");

            byte[] decodedBytes = Base64.getDecoder().decode(encodedString);

            fos.write(decodedBytes);

            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
