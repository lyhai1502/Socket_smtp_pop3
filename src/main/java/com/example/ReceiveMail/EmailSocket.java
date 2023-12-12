package com.example.ReceiveMail;

import java.awt.desktop.AboutEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class EmailSocket {
    private Map<String, String> headers = new HashMap<>();
    private void extractLine(String line, String regex) {
        String[] parts = line.split(regex, 2);
        String key = parts[0].trim();
        String value = parts[1].trim();
        headers.put(key, value);
    }

    public String get(String key) {
        if (headers.containsKey(key)) {
            return headers.get(key);
        } else {
            return null;
        }
    }

    public void addKeyValue(String key, String value) {
        headers.put(key, value);
    }

    private void extractEmailHeaders(String emailString) {
        String[] lines = emailString.split("\n");

        for (String line : lines) {
            if (line.contains(":")) {
                String[] parts = line.split(":", 2);
                String key = parts[0].trim();
                String value = parts[1].trim();
                if (value.contains("; ")) {
                    String[] subValues = value.split("; ", 2);
                    headers.put(key, subValues[0].trim());

                    String subLine = subValues[1].trim();
                    if (subLine.contains("=\"")) {
                        String[] subParts = subLine.split("=", 2);
                        String subKey = subParts[0].trim();
                        String subValue = subParts[1].trim();
                        headers.put(subKey, subValue);
                    }
                } else {
                    headers.put(key, value);
                }
            } else if (line.contains("=\"")) {
                String[] parts = line.split("=", 2);
                String key = parts[0].trim();
                String value = parts[1].trim();
                headers.put(key, value);
            }
        }
    }

    public EmailSocket(String emailContent) {
        String[] parts = emailContent.split("\n\n", 2);
        if (parts.length < 2) return;

        String header = parts[0].trim();
        extractEmailHeaders(header);

        String content = parts[1].trim();
        String contentType = (String) headers.get("Content-Type");
        headers.put("Content", content);
//        headers.put("Content", content);
//        if (headers.containsKey("boundary")) {
//            readEmail();
//        }
    }

    public String getContent() {
        String contentType = (String) headers.get("Content-Type");
        if (contentType.contains("multipart")) {
            String content = headers.get("Content");
            return content.toString();
        } else {
            return (String) headers.get("Content");
        }
    }



    public void readEmail() {
        String content = (String) headers.get("Content");
        System.out.println(headers.get("boundary"));
        String boundary = ((String) headers.get("boundary")).replaceAll("\"", "");
        StringBuilder fileContent = new StringBuilder();
        String[] lines = content.split("\n");
        for (String line: lines) {
            if (!line.contains(boundary)) {
                fileContent.append(line).append("\n");
            } else {
                EmailSocket subEmailSocket = new EmailSocket(fileContent.toString());
                subEmailSocket.parseFile();
                fileContent.setLength(0);
            }
        }
    }

    public void parseFile() {
        if (headers.containsKey("filename")) {
            extractFile(((String) headers.get("filename")).replaceAll("\"", ""));
        }
    }

    public void extractFile(String fileName) {
        if (!headers.containsKey("Content")) {
            return;
        }

        try {
            File file = new File(fileName);

            if (file.createNewFile()) {
                System.out.println("Tệp tin đã được tạo: " + file.getName());
            } else {
                System.out.println("Tệp tin đã tồn tại.");
            }

            String encodedString = ((String) headers.get("Content")).trim();
            FileOutputStream fos = new FileOutputStream(file);
            encodedString = encodedString.replaceAll("\n", "");
//            System.out.println(encodedString);

            byte[] decodedBytes = Base64.getDecoder().decode(encodedString);


            fos.write(decodedBytes);

            fos.close();
            System.out.println("Dữ liệu đã được ghi vào tệp tin.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        String result = "";
        for (String header: headers.keySet()) {
            if (header.equals("Content")) continue;

            result = result + header + ": " + headers.get(header) + "\n";
        }
        return result;
    }
}
