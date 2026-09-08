package cn.afeibaili.whitelist.local;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * # 本地数据
 *
 * @author AfeiBaili
 * @version 2026/9/5 21:37
 */

public class LocalDataRepository {
    public static final String WHITELIST_PATH = System.getProperty("user.dir") + "/whitelist";
    public static final String DATA_DIR_PATH = WHITELIST_PATH + "/data";
    public static final String LIST_FILE_PATH = WHITELIST_PATH + "/list.txt";
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    static {
        new File(DATA_DIR_PATH).mkdirs();
    }

    public static void saveWhitelist(String whitelist) {
        File file = securityFile(LIST_FILE_PATH);
        try (FileWriter fw = new FileWriter(file)) {
            fw.write(whitelist);
            fw.flush();
        } catch (IOException ignored) {
        }
    }

    public static void saveData(PlayerData playerData) {
        File file = new File(DATA_DIR_PATH, String.valueOf(playerData.mcId));
        try (FileWriter fileWriter = new FileWriter(file, false)) {
            fileWriter.write(GSON.toJson(playerData));
            fileWriter.flush();
        } catch (IOException ignored) {
        }
    }

    public static PlayerData getData(String uuid) {
        File file = new File(DATA_DIR_PATH, uuid);
        if (!file.exists()) return null;
        String text = readText(file);
        try {
            return GSON.fromJson(text, PlayerData.class);
        } catch (JsonSyntaxException e) {
            return null;
        }
    }

    public static void deleteDataList(){
        File file = securityFile(DATA_DIR_PATH);
        File[] files = file.listFiles();
        if (files == null) return;
        for (File f : files) {
            f.delete();
        }
    }

    public static String getLocalWhitelist() {
        File file = securityFile(LIST_FILE_PATH);
        if (!file.exists()) {
            try (FileWriter fileWriter = new FileWriter(file, false);) {
                fileWriter.write("");
            } catch (IOException ignored) {
                return "";
            }
        }
        return readText(file);
    }

    public static File securityFile(String path) {
        File file = new File(path);
        if (!file.exists()) file.getParentFile().mkdirs();
        return file;
    }

    public static String readText(File file) {
        try (FileReader fileReader = new FileReader(file)) {
            int len;
            char[] buf = new char[1024];
            StringBuilder sb = new StringBuilder();
            while ((len = fileReader.read(buf)) != -1) {
                sb.append(buf, 0, len);
            }
            return sb.toString();
        } catch (IOException e) {
            return "";
        }
    }
}
