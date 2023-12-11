import java.io.IOException;
import java.net.UnknownHostException;
import SendMail.SendMail;

public class Main {
    public static void main(String[] args) throws UnknownHostException, IOException {

        // Replace with the actual recipient address
        String[] recipients = { "lyhai1502@gmail.com" };
        // Add CC recipient
        String[] ccRecipients = {};
        // Add BCC recipient
        String[] bccRecipients = {};
        // Replace with the actual subject
        String subject = "Test";
        // Replace with the actual body of the message
        String body = "Test";
        // Replace with the actual file path
        String[] attachmentFilePaths = { "/Users/vanlyhai/Downloads/doc.docx" };

        SendMail sendMail = new SendMail(recipients, ccRecipients, bccRecipients, subject, body, attachmentFilePaths);
        sendMail.sendMail();
    }
}