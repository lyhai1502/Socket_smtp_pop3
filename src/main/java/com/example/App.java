package com.example;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.example.Menu.Menu;

public class App {
    public static void main(String[] args) throws UnknownHostException, IOException {
        Menu.viewMenu();
        // try {
        // // Định dạng ngày giờ
        // SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");

        // // Lấy ngày giờ hiện tại
        // Date now = new Date();

        // // Định dạng ngày giờ hiện tại
        // String nowString = format.format(now);

        // // In ra ngày giờ hiện tại
        // System.out.println(nowString);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
    }
}
