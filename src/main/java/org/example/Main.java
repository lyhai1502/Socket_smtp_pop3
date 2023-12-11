package org.example;

import java.io.*;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    private static ArrayList<EmailSocket> inbox = new ArrayList<>();
    private static ArrayList<EmailSocket> trunk = new ArrayList<>();

    public static void moveToFolder(EmailSocket email, String folder) {
        email.addKeyValue("Folder", folder);
    }

    public static void markAsRead(EmailSocket emailSocket, boolean isRead) {
        emailSocket.addKeyValue("IsRead", String.valueOf(isRead));
    }

    public static void filterEmailWithSubject(ArrayList<EmailSocket> emailSockets, String subject) {
        int count = 0;
        for (EmailSocket emailSocket : emailSockets) {
            String emailSubject = emailSocket.get("Subject");
            if (emailSubject == null) continue;

            emailSubject = emailSubject.toUpperCase();
            subject = subject.toUpperCase();

            if (emailSubject.contains(subject)) {
                System.out.println("Email: " + count);
                System.out.println("Subject: " + emailSocket.get("Subject"));
                readEmail(emailSocket);
                System.out.println();
            }

            count++;
        }
    }

    public static void filterEmailWithFrom(ArrayList<EmailSocket> emailSockets, String from) {
        for (int index = 0; index < emailSockets.size(); index++) {
            EmailSocket emailSocket = emailSockets.get(index);
            String emailFrom = emailSocket.get("From");
            if (emailFrom == null) continue;

            emailFrom = emailFrom.toUpperCase();
            from = from.toUpperCase();

            if (emailFrom.contains(from)) {
                System.out.println("Email: " + index + 1);
                System.out.println("From: " + emailSocket.get("From"));
                readEmail(emailSocket);
                System.out.println();
            }
        }
    }
    public static void readMultipart(String[] lines, String boundary) {
        StringBuilder emailContent = new StringBuilder();
        for (String line : lines) {
            if (line.contains(boundary)) {
                EmailSocket subContent = new EmailSocket(emailContent.toString());
                readEmail(subContent);
                emailContent.setLength(0);
            } else {
                emailContent.append(line).append("\n");
            }
        }
    }

    public static void readEmail(EmailSocket email) {
        String content = email.get("Content");
        if (email.get("Content-Type") != null &&
            email.get("Content-Type").contains("multipart")
        ) {
            String boundary = email.get("boundary").replaceAll("\"", "");
            String[] lines = content.split("\n");
            readMultipart(lines, boundary);
        } else {
            if (email.get("filename") != null) {
                System.out.println(email.get("Content-Disposition") + " " + email.get("filename"));
                extractFile(email.get("filename").replaceAll("\"", ""), email.get("Content"));
            } else if (email.get("Content-Type") != null &&
                    email.get("Content-Type").contains("text") &&
                    !email.get("Content-Type").contains("html")
            ) {
                System.out.println("Noi dung:" + " " + email.get("Content"));
            }
        }
    }

    public static void extractFile(String fileName, String content) {
        try {
            File file = new File(fileName);
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

    public static void main(String[] args) {
        String host = "127.0.0.1";
        int port = 3335; // Port 110 thường được sử dụng cho POP3

        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            // Đọc phản hồi từ máy chủ
            String response = in.readLine();
            System.out.println("Server: " + response);

            // Gửi lệnh USER để xác thực với tên người dùng
            out.println("USER xuanchien@gmail.com");
            response = in.readLine();
            System.out.println("Server: " + response);

            // Gửi lệnh PASS để xác thực với mật khẩu
            out.println("PASS 123");
            response = in.readLine();
            System.out.println("Server: " + response);

            // Gửi lệnh STAT để lấy thông tin về số lượng email và tổng dung lượng
            out.println("STAT");
            System.out.println("STAT");
            response = in.readLine();
            String[] parts = response.split(" ", 3);
            int size = Integer.parseInt(parts[1]);
            System.out.println("Server: " + response);

            // Gửi lệnh LIST để lấy danh sách các email và kích thước của chúng
            out.println("LIST");
            System.out.println("LIST");
            List<String> listEmail = new ArrayList<>();
            while (!(response = in.readLine()).equals(".")) {
                listEmail.add(response);
            }
            System.out.println("Server: " + listEmail);

            // Gửi lệnh RETR để lấy nội dung của một email cụ thể (thay thế số 1 bằng số của email cần lấy)
//            out.println("RETR 7");
//            System.out.println("RETR 5"
//
//
//            );

            // Đọc nội dung email
//            StringBuilder emailContent = new StringBuilder();
//            String line;
//            while (!(line = in.readLine()).equals(".")) {
//                emailContent.append(line).append("\n");
//            }
            ArrayList<EmailSocket> emailSockets = new ArrayList<>();
            for (int i = 1; i <= size; i++) {
                out.println("RETR " + i);
                StringBuilder emailContent = new StringBuilder();
                String line;
                while (!(line = in.readLine()).equals(".")) {
                    emailContent.append(line).append("\n");
                }

                EmailSocket emailSocket = new EmailSocket(emailContent.toString());
                inbox.add(emailSocket);
            }

//            readEmail(inbox.get(10));

//            int count = 0;
//            for (EmailSocket email : emailSockets) {
//                System.out.println("Email: " + count++);
//                readEmail(email);
//            }
//            readEmail(emailSockets.get(5));

//            filterEmailWithSubject(emailSockets, "Test");
//            filterEmailWithFrom(inbox, "xuanchien");

//            readEmailContent(String.valueOf(emailContent));
//            EmailSocket emailSocket = new EmailSocket(emailContent.toString());
//            System.out.println(emailSocket);
//            System.out.println(emailSocket.getContent());
//            emailSocket.readEmail();

//             Gửi lệnh QUIT để kết thúc phiên làm việc
            out.println("QUIT");
            System.out.println("QUIT");
            response = in.readLine();
            System.out.println("Server: " + response);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}