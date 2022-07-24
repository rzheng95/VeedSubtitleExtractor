package com.rzheng;

import java.io.*;

public class Main {
    public static void main(String[] args) {
        final String SOURCE_FILE_PATH = "source/first.html",
                     OUTPUT_FILE_PATH = "output/first.srt";

        writeFile(OUTPUT_FILE_PATH, readFile(SOURCE_FILE_PATH));
    }

    public static String readFile(String fileName) {
        String fileContent = "";
        try {
            File f = new File(fileName);
            if (f.isFile() && f.exists()) {
                InputStreamReader read = new InputStreamReader(new FileInputStream(f), "UTF-8");
                BufferedReader reader = new BufferedReader(read);
                String line;

                int lineCount = 1;
                String subtitle = "";
                String startTime = "";
                String endTime = "";
                while ((line = reader.readLine()) != null) {
                    if (isSubtitle(line)) {
                        subtitle += extractSubtitleFromLine(line.trim());
                    }

                    if (isTime(line)) {
                        String time = extractTimeFromLine(line.trim());
                        if (startTime.isBlank()) {
                            startTime = time;
                        } else {
                            endTime = time;
                        }
                    }

                    /**
                     * populate fileContent once we have
                     *  1. Start Time
                     *  2. End Time
                     *  3. Current Subtitle
                     */
                    if (!subtitle.isBlank() && !startTime.isBlank() && !endTime.isBlank()) {
                        fileContent += lineCount++ + "\n";
                        fileContent += "00:" + startTime + "00" + " --> " + "00:" + endTime + "00" + "\n";
                        fileContent += subtitle + "\n\n";

                        startTime = "";
                        endTime = "";
                        subtitle = "";
                    }
                }
                read.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileContent;
    }

    private static boolean isTime(String line) {
        return line.contains("value=");
    }

    private static boolean isSubtitle(String line) {
        return line.contains("<span data-text=\"true\">");
    }

    private static String extractSubtitleFromLine(String line) {
        final int START_POS = 67,
                  END_POS = line.length() - 14;
        return line.substring(START_POS, END_POS);
    }

    private static String extractTimeFromLine(String line) {
        final int START_POS = 7,
                  END_POS = line.length() - 8;
        return line.substring(START_POS, END_POS).replace(".", ",");
    }

    public static void writeFile(String fileName, String fileContent) {
        try {
            File f = new File(fileName);
            if (!f.exists()) {
                f.createNewFile();
            }
            OutputStreamWriter write = new OutputStreamWriter(
                    new FileOutputStream(f), "UTF-8");
            BufferedWriter writer = new BufferedWriter(write);
            writer.write(fileContent);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}