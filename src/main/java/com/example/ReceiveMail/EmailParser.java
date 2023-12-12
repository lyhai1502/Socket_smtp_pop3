package com.example.ReceiveMail;
import java.util.HashMap;
import java.util.Map;

public class EmailParser {

    public static Map<String, String> extractEmailHeaders(String emailString) {
        Map<String, String> headers = new HashMap<>();
        String[] lines = emailString.split("\n");

        for (String line : lines) {
            if (line.contains(":")) {
                String[] parts = line.split(":", 2);
                String key = parts[0].trim();
                String value = parts[1].trim();
                headers.put(key, value);
            }
        }

        return headers;
    }

    public static void main(String[] args) {
        // Chuỗi email
        String emailString = "Message-ID: <e306ddb1-e8df-4456-9067-3effa0e49f56@gmail.com>\n" +
                "Date: Sun, 10 Dec 2023 18:23:56 +0700\n" +
                "MIME-Version: 1.0\n" +
                "User-Agent: Mozilla Thunderbird\n" +
                "Content-Language: en-US\n" +
                "To: xuanchien@gmail.com\n" +
                "From: =?UTF-8?B?TmfDtCBYdcOibiBDaGnhur9u?= <xuanchien@gmail.com>\n" +
                "Subject: Test\n" +
                "Content-Type: text/plain; charset=UTF-8; format=flowed\n" +
                "Content-Transfer-Encoding: 7bit\n" +
                "\n" +
                "Test";

        // Gọi hàm và in kết quả
        Map<String, String> headers = extractEmailHeaders(emailString);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
