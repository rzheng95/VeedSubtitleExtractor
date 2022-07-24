package com.rzheng;

import java.io.*;
import java.nio.charset.Charset;

public class Main2 {
    public static void main(String[] args) throws IOException {
        String chinese = "\u4E0A\u6D77";
        boolean append = true;
        writeUtf8ToFile(new File("chinese.txt"), append, chinese);

        System.out.println(readText(new File("test.txt")));
    }

    private static void writeUtf8ToFile(File file, boolean append, String data)
            throws IOException {
        boolean skipBOM = append && file.isFile() && (file.length() > 0);
        Closer res = new Closer();
        try {
            OutputStream out = res.using(new FileOutputStream(file, append));
            Writer writer = res.using(new OutputStreamWriter(out, Charset
                    .forName("UTF-8")));
            if (!skipBOM) {
                writer.write('\uFEFF');
            }
            writer.write(data);
        } finally {
            res.close();
        }
    }

    private static final Charset[] UTF_ENCODINGS = { Charset.forName("UTF-8"),
            Charset.forName("UTF-16LE"), Charset.forName("UTF-16BE") };

    private static Charset getEncoding(InputStream in) throws IOException {
        charsetLoop: for (Charset encodings : UTF_ENCODINGS) {
            byte[] bom = "\uFEFF".getBytes(encodings);
            in.mark(bom.length);
            for (byte b : bom) {
                if ((0xFF & b) != in.read()) {
                    in.reset();
                    continue charsetLoop;
                }
            }
            return encodings;
        }
        return Charset.defaultCharset();
    }

    private static String readText(File file) throws IOException {
        Closer res = new Closer();
        try {
            InputStream in = res.using(new FileInputStream(file));
            InputStream bin = res.using(new BufferedInputStream(in));
            Reader reader = res.using(new InputStreamReader(bin, getEncoding(bin)));
            StringBuilder out = new StringBuilder();
            for (int ch = reader.read(); ch != -1; ch = reader.read())
                out.append((char) ch);
            return out.toString();
        } finally {
            res.close();
        }
    }
}
