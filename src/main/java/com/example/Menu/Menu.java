package com.example.Menu;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.example.SendMail.SendMail;
import com.example.Config.Static;
import com.example.PasswordEncoder.PasswordEncoder;
import com.example.ReceiveMail.EmailSocket;
import com.example.ReceiveMail.ReceiveMail;
import org.json.simple.parser.ParseException;

public class Menu {

    public static void viewMenu() {
        Scanner scanner = new Scanner(System.in);

        Home(scanner);

        // Step 3: Show menu
        int choice;

        do {
            System.out.println("\nVui lòng chọn Menu:");
            System.out.println("1. Để gửi email");
            System.out.println("2. Để xem danh sách các email đã nhận");
            System.out.println("3. Thoát ra màn hình đăng nhập");
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
                    readEmail(scanner);

                    // Add your code here to handle read mail
                    break;
                case 3:
                    scanner.nextLine(); // Consume newline left-over
                    System.out.println("Thoát về màn hình đăng nhập.");
                    viewMenu();
                    break;
                default:
                    System.out.println("Lựa chọn không hợp lệ. Vui lòng nhập một số từ 1 đến 3.");
            }
        } while (choice != 3);

        scanner.close();
    }

    private static void readEmail(Scanner scanner) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.scheduleAtFixedRate(() -> {
            try {
                ReceiveMail.getEmailFromThePOP3(Static.USERNAME, Static.PASSWORD);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, Static.AUTOLOAD, TimeUnit.SECONDS);

        String choice = "";
        scanner.nextLine();
        do {
            System.out.println("Đây là danh sách các folder trong mailbox của bạn:");
            System.out.println("1. INBOX");
            System.out.println("2. PROJECT");
            System.out.println("3. IMPORTANT");
            System.out.println("4. WORK");
            System.out.println("5. SPAM");
            System.out.print("Bạn muốn xem email trong folder nào: ");
            choice = scanner.nextLine();
            File directory;
            switch (choice) {
                case "1":
                    directory = new File("Data/" + Static.USERNAME + "/INBOX");
                    readProjectFolder(scanner, directory);
                    break;
                case "2":
                    directory = new File("Data/" + Static.USERNAME + "/PROJECT");
                    readProjectFolder(scanner, directory);
                    break;
                case "3":
                    directory = new File("Data/" + Static.USERNAME + "/IMPORTANT");
                    readProjectFolder(scanner, directory);
                    break;
                case "4":
                    directory = new File("Data/" + Static.USERNAME + "/WORK");
                    readProjectFolder(scanner, directory);
                    break;
                case "5":
                    directory = new File("Data/" + Static.USERNAME + "/SPAM");
                    readProjectFolder(scanner, directory);
                    break;
                default:
                    System.out.println("out of range");
                    break;
            }
        } while (!choice.isEmpty());
    }

    private static void readProjectFolder(Scanner scanner, File directory) {
        String choice;
        do {
            ArrayList<HashMap<String, String>> listEmail = ReceiveMail.loadData(directory,
                    "status.json");
            ArrayList<EmailSocket> emails = new ArrayList<>();

            int count = 1;
            for (var email : listEmail) {

                EmailSocket emailSocket = new EmailSocket(directory, email.get("name"));
                emails.add(emailSocket);

                System.out.println(count++ + ". (" + email.get("status") + ") " + emailSocket.get("From") + ", "
                        + emailSocket.get("Subject"));

            }

            if (listEmail.isEmpty()) {
                System.out.print("Thư mục rỗng Enter để quay lại: ");
            } else {
                System.out.print("Bạn muốn đọc Email thứ mấy: ");
            }
            choice = scanner.nextLine();
            if (!choice.isEmpty() && Integer.parseInt(choice) <= listEmail.size()) {
                ReceiveMail.readEmail(emails.get(Integer.parseInt(choice) - 1), Integer.parseInt(choice));
                try {
                    ReceiveMail.updateStatus(directory,
                            listEmail.get(Integer.parseInt(choice) - 1).get("id"),
                            listEmail.get(Integer.parseInt(choice) - 1).get("name"),
                            "read");
                } catch (IOException | ParseException e) {
                    throw new RuntimeException(e);
                }
            }
            // scanner.nextLine();
            // choice = scanner.nextLine();
        } while (!choice.isEmpty());
    }

    private static void Login(Scanner scanner) {

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

                if (email.equals(enteredUsername)) {
                    while (!password.equals(PasswordEncoder.encodePassword(enteredPassword))) {
                        System.out.println("Wrong password");

                        System.out.print("Enter password: ");
                        enteredPassword = scanner.nextLine();
                    }
                    isUserExist = true;
                    break;
                }
            }

            // Check if entered username and password match with the ones in the config file
            if (isUserExist == false) {
                System.out.println("User does not existed. Created new user.");

                // Create a new user object
                JSONObject newUser = new JSONObject();
                newUser.put("Username", enteredUsername + " <" + enteredUsername + ">");
                newUser.put("Password", PasswordEncoder.encodePassword(enteredPassword));
                // JSONObject mailboxes = new JSONObject();
                // mailboxes.put("INBOX", new JSONArray());
                // mailboxes.put("PROJECT", new JSONArray());
                // mailboxes.put("IMPORTANT", new JSONArray());
                // mailboxes.put("WORK", new JSONArray());
                // mailboxes.put("SPAM", new JSONArray());

                // newUser.put("Mailboxes", mailboxes);

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
            System.out.println("Email: " + enteredUsername);
            System.out.println("Mail Server: " + mailServer);
            System.out.println("SMTP: " + smtp);
            System.out.println("POP3: " + pop3);
            System.out.println("Autoload: " + autoload);

            // Set the properties
            Static.setProperties(mailServer, (int) smtp, (int) pop3, PasswordEncoder.encodePassword(enteredPassword),
                    enteredUsername, enteredUsername, (int) autoload);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void Home(Scanner scanner) {
        System.out.println("1. Đăng nhập vào hệ thống");
        System.out.println("2. Thoát hệ thống");
        System.out.print("Bạn chọn: ");

        switch (scanner.nextLine()) {
            case "1":
                Login(scanner);
                break;

            default:
                System.exit(0);
                break;
        }
    }
}