import java.io.IOException;
import java.net.UnknownHostException;
import SendMail.SendMail;

public class Main {
    public static void main(String[] args) throws UnknownHostException, IOException {
       SendMail sendMail = new SendMail();
       sendMail.sendMail();
    }
}