package cn.afeibaili.whitelist.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * # 列表网络器
 *
 * @author AfeiBaili
 * @version 2026/9/5 20:20
 */

public class ListNetwork {
    public static final String WHITELIST_URI = "https://whitelist.afeibaili.cn/whitelist/list.txt";

    public static String getWhitelist() throws IOException {
        URL url = new URL(WHITELIST_URI);
        URLConnection urlConnection = url.openConnection();
        urlConnection.setConnectTimeout(1000 * 5);
        urlConnection.setRequestProperty("User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        return readText(urlConnection.getInputStream());
    }

    public static InputStream getInputStream(URL url) throws IOException {
        URLConnection connection = url.openConnection();
        connection.setConnectTimeout(1000 * 5);
        connection.setRequestProperty("User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        return connection.getInputStream();
    }

    public static String readText(InputStream stream) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(stream);
        char[] buffer = new char[1024];
        int len;
        StringBuilder output = new StringBuilder();
        while ((len = inputStreamReader.read(buffer)) != -1) {
            output.append(buffer, 0, len);
        }
        return output.toString();
    }

    public static List<String> readLines(InputStream stream) throws IOException {
        List<String> list = new ArrayList<>();
        new BufferedReader(new InputStreamReader(stream)).lines().forEach(list::add);
        return list;
    }
}
