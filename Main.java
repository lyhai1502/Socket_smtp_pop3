import java.io.IOException;
import java.net.UnknownHostException;
import SendMail.SendMail;

public class Main {
    public static void main(String[] args) throws UnknownHostException, IOException {

        String[] recipients = { "lyhai1502@gmail.com" };
        String[] ccRecipients = { "lyhai1502.work@gmail.com" };
        String[] bccRecipients = {};
        String subject = "Test2";
        String body = "Test2";
        String[] attachmentFilePaths = { "/Users/vanlyhai/Downloads/doc.docx" };
        SendMail sendMail = new SendMail(recipients, ccRecipients, bccRecipients, subject, body, attachmentFilePaths);
        sendMail.sendMail();
    }
}