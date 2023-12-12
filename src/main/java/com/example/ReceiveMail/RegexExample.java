package com.example.ReceiveMail;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexExample {

    public static void main(String[] args) {
        String input = "This is the first block.\n\n" +
                "This is the second block.\n\n" +
                "And this is the third block.";

        // Biểu thức chính quy để tìm kiếm các chuỗi giữa hai dấu xuống dòng
        String regex = "(?s)\n\n(.*?)\n\n";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);

        int blockNumber = 1;

        while (matcher.find()) {
            String extractedText = matcher.group(1);
            System.out.println("Block " + blockNumber + ":\n" + extractedText.trim());
            blockNumber++;
        }
    }
}

