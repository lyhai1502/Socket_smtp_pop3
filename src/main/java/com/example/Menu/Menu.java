package com.example.Menu;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

import com.example.SendMail.SendMail;
import com.example.Config.Static;

public class Menu {
    public static void viewMenu() {
        Scanner scanner = new Scanner(System.in);

        // Step 1: Login
        System.out.print("Enter email: ");
        String enteredUsername = scanner.nextLine();
        System.out.print("Enter password: ");
        String enteredPassword = scanner.nextLine();

        // Step 2: Read config file
        try {
            String content = new String(Files.readAllBytes(Paths.get(Static.CONFIG_PATH)));
            JSONParser parser = new JSONParser();
            JSONObject config = (JSONObject) parser.parse(content);

            boolean isUserExist = false;
            String email = "";
            String password = "";

            // Get the users array from the config object
            JSONArray users = (JSONArray) config.get("Users");
            for (Object userObj : users) {
                JSONObject user = (JSONObject) userObj;
                String fullUsername = (String) user.get("Username");
                email = fullUsername.substring(fullUsername.indexOf("<") + 1, fullUsername.lastIndexOf(">"));
                password = (String) user.get("Password");

                if (email.equals(enteredUsername) && password.equals(enteredPassword)) {
                    isUserExist = true;
                    break;
                }
            }

            // Check if entered username and password match with the ones in the config file
            if (isUserExist == false) {
                // Create a new user object
                JSONObject newUser = new JSONObject();
                newUser.put("Username", enteredUsername + " <" + enteredUsername + ">");
                newUser.put("Password", enteredPassword);
                JSONArray mailboxes = new JSONArray();
                mailboxes.add("INBOX");
                mailboxes.add("PROJECT");
                mailboxes.add("IMPORTANT");
                mailboxes.add("WORK");
                mailboxes.add("SPAM");
                newUser.put("Mailboxes", mailboxes);

                // Add the new user to the users array
                users.add(newUser);

                // Write the updated config object back to the file
                Files.write(Paths.get(Static.CONFIG_PATH), config.toJSONString().getBytes());

            }

            JSONObject serverInformation = (JSONObject) config.get("ServerInformation");
            // Use the properties here...
            String mailServer = (String) serverInformation.get("MailServer");
            int smtp = ((Long) serverInformation.get("SMTP")).intValue();
            int pop3 = ((Long) serverInformation.get("POP3")).intValue();
            int autoload = ((Long) serverInformation.get("Autoload")).intValue();

            // Print the variables
            System.out.println("Name: " + enteredUsername);
            System.out.println("Email: " + enteredPassword);
            System.out.println("Mail Server: " + mailServer);
            System.out.println("SMTP: " + smtp);
            System.out.println("POP3: " + pop3);
            System.out.println("Autoload: " + autoload);

            // Set the properties
            Static.setProperties(mailServer, (int) smtp, (int) pop3, password, email, email, (int) autoload);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Step 3: Show menu
        int choice;

        do {
            System.out.println("\nVui lòng chọn Menu:");
            System.out.println("1. Để gửi email");
            System.out.println("2. Để xem danh sách các email đã nhận");
            System.out.println("3. Thoát");
            System.out.print("Bạn chọn: ");

            choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.println("Đây là thông tin soạn email: (nếu không điền vui lòng nhấn enter để bỏ qua)");
                    scanner.nextLine(); // Consume newline left-over
                    System.out.print("To (nếu muốn gửi cho nhiều người hãy phân tách các email bởi dấu phẩy): ");
                    String toInput = scanner.nextLine();
                    String[] to = toInput.isEmpty() ? new String[0] : toInput.split(",\\s*");

                    System.out.print("CC (nếu muốn gửi cho nhiều người hãy phân tách các email bởi dấu phẩy): ");
                    String ccInput = scanner.nextLine();
                    String[] cc = ccInput.isEmpty() ? new String[0] : ccInput.split(",\\s*");

                    System.out.print("BCC (nếu muốn gửi cho nhiều người hãy phân tách các email bởi dấu phẩy): ");
                    String bccInput = scanner.nextLine();
                    String[] bcc = bccInput.isEmpty() ? new String[0] : bccInput.split(",\\s*");

                    System.out.print("Subject: ");
                    String subject = scanner.nextLine();

                    System.out.print("Content: ");
                    String content = scanner.nextLine();

                    System.out.print("Có gửi kèm file (1. có, 2. không): ");
                    int hasFile = scanner.nextInt();
                    String[] filePaths = new String[0];
                    if (hasFile == 1) {
                        System.out.print("Số lượng file muốn gửi: ");
                        int fileCount = scanner.nextInt();
                        scanner.nextLine(); // Consume newline left-over
                        filePaths = new String[fileCount];
                        for (int i = 0; i < fileCount; i++) {
                            System.out.print("Cho biết đường dẫn file thứ " + (i + 1) + ": ");
                            filePaths[i] = scanner.nextLine();
                        }
                    }

                    try {
                        SendMail sendMail = new SendMail(to, cc, bcc, subject, content, filePaths);
                        sendMail.sendMail();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                case 2:
                    System.out.println("Đây là danh sách các folder trong mailbox của bạn:");
                    // Add your code here to handle read mail
                    break;
                case 3:
                    System.out.println("Thoát");
                    break;
                default:
                    System.out.println("Lựa chọn không hợp lệ. Vui lòng nhập một số từ 1 đến 3.");
            }
        } while (choice != 3);

        scanner.close();
    }
}