package SendMail;
import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import Config.Static;

public class SendMail {

    String[] recipients = {};
    String[] ccRecipients = {};
    String[] bccRecipients = {}; // Add BCC recipient
    String subject = "";
    String body = "";
    String[] attachmentFilePaths = {}; // Replace with the actual file path

    Socket socket;
    BufferedReader reader;
    BufferedWriter writer;
    
    public SendMail() throws UnknownHostException, IOException {
        // Connect to the SMTP server
        socket = new Socket(Static.SERVER, Static.STMP_PORT);
        reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
    }

   
    public SendMail(String[] recipients, String[] ccRecipients, String[] bccRecipients, String subject, String body,
            String[] attachmentFilePaths) {
        this.recipients = recipients;
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
            writer.write("EHLO " + "[" + Static.SERVER + "]" + "\r\n");
            writer.flush();
            response = reader.readLine();
            System.out.println("Server: " + response);

            // Send the MAIL FROM command
            writer.write("MAIL FROM:<" + Static.SENDER + ">\r\n");
            writer.flush();
            response = reader.readLine();
            System.out.println("Server: " + response);

            // Send the RCPT TO command for the main recipient
            for (String recipient : recipients) {
                writer.write("RCPT TO:<" + recipient + ">\r\n");
                writer.flush();
                response = reader.readLine();
                System.out.println("Server: " + response);
            }

            // Send the RCPT TO command for the CC recipient
            for (String ccRecipient : ccRecipients) {
                writer.write("RCPT TO:<" + ccRecipient + ">\r\n");
                writer.flush();
                response = reader.readLine();
                System.out.println("Server: " + response);
            }

            // Send the RCPT TO command for the BCC recipient
            for (String bccRecipient : bccRecipients) {
                writer.write("RCPT TO:<" + bccRecipient + ">\r\n");
                writer.flush();
                response = reader.readLine();
                System.out.println("Server: " + response);
            }

            // Send the DATA command
            writer.write("DATA\r\n");
            writer.flush();
            response = reader.readLine();
            System.out.println("Server: " + response);

            writer.write("Content-Type: multipart/mixed; boundary=\"------------\"\r\n");


            // Send the email headers and body
            for (String recipient : recipients) {
                writer.write("To: " + recipient + ", ");
            }
            writer.write("\r\n");

              for (String ccRecipient : ccRecipients) {
                writer.write("Cc: " + ccRecipient + ", ");
            }
            writer.write("\r\n");


            writer.write("From: \"" + Static.USERNAME + "\" <" + Static.SENDER + ">\r\n");
            writer.write("Subject: " + subject + "\r\n");
            
            for (String ccRecipient : ccRecipients) {
                writer.write("Cc: " + ccRecipient + "\r\n");
            }

            // writer.write("Bcc: " + bccRecipient + "\r\n");
            writer.write("Content-Type: multipart/mixed; boundary=\"boundary\"\r\n");
            writer.write("\r\n");
            writer.write("--boundary\r\n");
            writer.write("Content-Type: text/plain; charset=UTF-8; format=flowed\r\n");
            writer.write("Content-Transfer-Encoding: 7bit\r\n");
            writer.write("\r\n");
            writer.write(body + "\r\n");
            writer.write("--boundary\r\n");
            

            
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
    
    private void readFileContent(String[] attachmentFilePaths){
        
        long totalFileSize = 0;
            for (String attachmentFilePath : attachmentFilePaths) {
                File file = new File(attachmentFilePath);
                totalFileSize += file.length();
            }

            // Check if total file size is greater than 3MB
            long threeMB = 3 * 1024 * 1024; // 3MB in bytes: 1MB = 1024KB, 1KB = 1024 bytes
            if (totalFileSize > threeMB) {
                // Perform actions if total file size is greater than 3MB
                System.out.println("Total file size is greater than 3MB");
                return;
            }

        for (String attachmentFilePath : attachmentFilePaths) {
            try {
                if(attachmentFilePath.endsWith(".txt"))
                    writer.write("Content-Type: text/plain; charset=UTF-8; name=\"" + attachmentFilePath + "\r\n");
                else if (attachmentFilePath.endsWith(".pdf"))
                    writer.write("Content-Type: application/pdf; name=\"" + attachmentFilePath + "\r\n");
                else if (attachmentFilePath.endsWith(".jpg"))
                    writer.write("Content-Type: image/jpeg; name=\"" + attachmentFilePath + "\r\n");
                else if (attachmentFilePath.endsWith(".zip"))
                    writer.write("Content-Type: application/zip; name=\"" + attachmentFilePath + "\r\n");
                else if (attachmentFilePath.endsWith(".docx"))
                    writer.write(
                            "Content-Type: application/vnd.openxmlformats-officedocument.wordprocessingml.document; name=\""
                                    + attachmentFilePath + "\r\n");
                
                writer.write("Content-Disposition: attachment; filename=\"" + new File(attachmentFilePath).getName() + "\"\r\n");
                writer.write("Content-Transfer-Encoding: base64\r\n");
                writer.write("\r\n");

                byte[] fileContent = Files.readAllBytes(Paths.get(attachmentFilePath));
                String encodedString = Base64.getEncoder().encodeToString(fileContent);
                int chunkSize = 72;
                for (int i = 0; i < encodedString.length(); i += chunkSize) {
                    int endIndex = Math.min(i + chunkSize, encodedString.length());
                    String chunk = encodedString.substring(i, endIndex);
                    writer.write(chunk + "\r\n");
                }
                writer.write("--boundary\r\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

    
