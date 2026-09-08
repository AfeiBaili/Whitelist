package cn.afeibaili.whitelist;

import cn.afeibaili.whitelist.local.LocalDataRepository;
import cn.afeibaili.whitelist.local.PlayerData;
import cn.afeibaili.whitelist.network.ListNetwork;
import com.google.gson.Gson;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static cn.afeibaili.whitelist.Whitelist.LOG;

/**
 * # 校验器
 *
 * @author AfeiBaili
 * @version 2026/9/5 21:40
 */

public class Validator {
    public static final Gson GSON = new Gson();
    static long lastUpdateTime = 0;

    public static void updateList() {
        if (System.currentTimeMillis() - lastUpdateTime < 1000 * 60 * 5) return;
        try {
            String whitelist = ListNetwork.getWhitelist();
            String localWhitelist = LocalDataRepository.getLocalWhitelist();
            if (!whitelist.trim().equals(localWhitelist.trim())) {
                LOG.info("更新白名单列表中");
                LocalDataRepository.saveWhitelist(whitelist);
                updateLocalList(whitelist);
                lastUpdateTime = System.currentTimeMillis();
            }
        } catch (IOException ignored) {
            LOG.warn(ignored.getMessage());
        }
    }

    public static boolean inWhitelist(String uuid) {
        PlayerData data = LocalDataRepository.getData(uuid);
        return data != null;
    }

    public static void updateLocalList(String remoteWhitelist) {
        LocalDataRepository.deleteDataList();
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(remoteWhitelist.getBytes(StandardCharsets.UTF_8))));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                InputStream stream = ListNetwork.getInputStream(new URL("https://whitelist.afeibaili.cn/whitelist/data/" + line));
                String text = ListNetwork.readText(stream);
                LocalDataRepository.saveData(GSON.fromJson(text, PlayerData.class));
            }
        } catch (IOException ignored) {
        }
    }
}
