package SendMail;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import Config.Static;

public class SendMail {

    String[] recipients = {"lyhai1502@gmail.com", "lyhaiclone@gmail.com"};
    String[] ccRecipients = { "godzerohai1222@gmail.com" };
    String[] bccRecipients = { "godzerohai1221@gmail.com", "lyhai1502.work@gmail.com" }; // Add BCC recipient
    String subject = "Hello";
    String body = "Ly hai";
    String[] attachmentFilePaths = { "/Users/vanlyhai/Downloads/attachment2.pdf", "/Users/vanlyhai/Downloads/attachment1.txt" }; // Replace with the actual file path

    Socket socket;
    BufferedReader reader;
    BufferedWriter writer;
    
    public SendMail() throws UnknownHostException, IOException {
        // Connect to the SMTP server
        socket = new Socket(Static.SERVER, Static.STMP_PORT);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }
    
    public SendMail(String[] ccRecipients, String[] bccRecipients, String subject, String body,
            String[] attachmentFilePaths) {
        
        this.ccRecipients = ccRecipients;
        this.bccRecipients = bccRecipients;
        this.subject = subject;
        this.body = body;
        this.attachmentFilePaths = attachmentFilePaths;

    }
   
            

    public void sendMail() {
        try {

            // Read the server's response
            String response = reader.readLine();
            System.out.println("Server: " + response);

            // Send the HELO command
            writer.write("HELO " + Static.SERVER + "\r\n");
            writer.flush();
            response = reader.readLine();
            System.out.println("Server: " + response);

            // Send the MAIL FROM command
            writer.write("MAIL FROM: <" + Static.SENDER + ">\r\n");
            writer.flush();
            response = reader.readLine();
            System.out.println("Server: " + response);

            // Send the RCPT TO command for the main recipient
            for (String recipient : recipients) {
                writer.write("RCPT TO: <" + recipient + ">\r\n");
                writer.flush();
                response = reader.readLine();
                System.out.println("Server: " + response);
            }

            // Send the RCPT TO command for the CC recipient
            for (String ccRecipient : ccRecipients) {
                writer.write("RCPT TO: <" + ccRecipient + ">\r\n");
                writer.flush();
                response = reader.readLine();
                System.out.println("Server: " + response);
            }

            // Send the RCPT TO command for the BCC recipient
            for (String bccRecipient : bccRecipients) {
                writer.write("RCPT TO: <" + bccRecipient + ">\r\n");
                writer.flush();
                response = reader.readLine();
                System.out.println("Server: " + response);
            }

            // Send the DATA command
            writer.write("DATA\r\n");
            writer.flush();
            response = reader.readLine();
            System.out.println("Server: " + response);

            // Send the email headers and body
            writer.write("Subject: " + subject + "\r\n");
            writer.write("From: " + Static.SENDER + "\r\n");
            for (String recipient : recipients) {
                writer.write("To: " + recipient + "\r\n");
            }
            for (String ccRecipient : ccRecipients) {
                writer.write("Cc: " + ccRecipient + "\r\n");
            }
            // writer.write("Bcc: " + bccRecipient + "\r\n");
            writer.write("Content-Type: multipart/mixed; boundary=boundary\r\n");
            writer.write("\r\n");
            writer.write("--boundary\r\n");
            writer.write("Content-Type: text/plain; charset=UTF-8\r\n");
            writer.write("\r\n");
            writer.write(body + "\r\n");
            writer.write("--boundary\r\n");
            writer.write("Content-Type: application/pdf; charset=UTF-8\r\n");
            writer.write("Content-Disposition: attachment; filename=\"" + attachmentFilePaths[0] + "\"\r\n");
            writer.write("Content-Transfer-Encoding: base64\r\n");
            writer.write("\r\n");

            
            readFileContent(attachmentFilePaths);


            writer.write("\r\n");
            writer.write("--boundary--\r\n");
            writer.write(".\r\n");
            writer.flush();
            response = reader.readLine();
            System.out.println("Server: " + response);

            // Send the QUIT command
            writer.write("QUIT\r\n");
            writer.flush();
            response = reader.readLine();
            System.out.println("Server: " + response);

            // Close the socket
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void readFileContent(String[] attachmentFilePaths) {
        for (String attachmentFilePath : attachmentFilePaths) {
            try {
                 byte[] fileContent = Files.readAllBytes(Paths.get(attachmentFilePath));
            String encodedString = Base64.getEncoder().encodeToString(fileContent);
            int chunkSize = 72;
            for (int i = 0; i < encodedString.length(); i += chunkSize) {
                int endIndex = Math.min(i + chunkSize, encodedString.length());
                String chunk = encodedString.substring(i, endIndex);
                writer.write(chunk + "\r\n");
            }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}

    
