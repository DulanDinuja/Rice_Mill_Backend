package com.ricemill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class RiceMillApplication {

    private final Environment env;

    public RiceMillApplication(Environment env) {
        this.env = env;
    }

    public static void main(String[] args) {
        SpringApplication.run(RiceMillApplication.class, args);
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        String appName = env.getProperty("spring.application.name", "Rice_mill_Backend");

        String[] banner = {
                "  _____                           __    ",
                " / ____|                         /_ |   ",
                "| (___   __ _ _ __ ___   ___  __ _| |   ",
                " \\___ \\ / _` | '_ ` _ \\ / _ \\/ _` | |   ",
                " ____) | (_| | | | | | |  __/ (_| | |   ",
                "|_____/ \\__,_|_| |_| |_|\\___|\\__,_|_|   ",
                "",
                "   R I C E   M I L L"
        };

        int width = Math.max(appName.length() + 10, maxLen(banner) + 6);
        String border = repeat("=", width);

        System.out.println();
        System.out.println(border);
        for (String line : banner) System.out.println(center(line, width));
        System.out.println(center("Sameera rice mill", width));
        System.out.println(center("App: " + appName, width));
        System.out.println(border);
        System.out.println();
    }

    private static int maxLen(String[] lines) {
        int max = 0;
        for (String s : lines) max = Math.max(max, s.length());
        return max;
    }

    private static String center(String s, int width) {
        if (s == null) s = "";
        if (s.length() >= width) return s;
        int left = (width - s.length()) / 2;
        return " ".repeat(left) + s;
    }

    private static String repeat(String s, int count) {
        return s.repeat(Math.max(0, count));
    }
}