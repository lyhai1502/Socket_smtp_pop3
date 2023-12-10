import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Menu {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Step 1: Login
        System.out.print("Enter username: ");
        String enteredUsername = scanner.nextLine();
        System.out.print("Enter password: ");
        String enteredPassword = scanner.nextLine();

        // Step 2: Read config file
        try {
            String content = new String(Files.readAllBytes(Paths.get("config.json")));
            JSONParser parser = new JSONParser();
            JSONObject config = (JSONObject) parser.parse(content);

            String fullUsername = (String) config.get("Username");
            String email = fullUsername.substring(fullUsername.indexOf("<") + 1, fullUsername.lastIndexOf(">"));
            String name = fullUsername.substring(0, fullUsername.indexOf("<")).trim();
            String password = (String) config.get("Password");

            // Check if entered username and password match with the ones in the config file
            if (!email.equals(enteredUsername) || !password.equals(enteredPassword)) {
       
                System.out.println("Invalid username or password.");
                scanner.close();
                return;
            }

            System.out.println("Name: " + name);
            System.out.println("Email: " + email);

            String mailServer = (String) config.get("MailServer");
            long smtp = (int) config.get("SMTP");
            long pop3 = (int) config.get("POP3");
            long autoload = (int) config.get("Autoload");

            // Use the properties here...


            // Print the variables
            System.out.println("Username: " + email);
            System.out.println("Password: " + password);
            System.out.println("Mail Server: " + mailServer);
            System.out.println("SMTP: " + smtp);
            System.out.println("POP3: " + pop3);
            System.out.println("Autoload: " + autoload);

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
                    // Add your code here to handle send mail
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