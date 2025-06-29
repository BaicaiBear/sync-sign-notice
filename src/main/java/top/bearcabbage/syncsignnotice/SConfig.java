package top.bearcabbage.syncsignnotice;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * 配置文件管理类
 */
public class SConfig {
    private final Path filePath;
    private JsonObject jsonObject;
    private final Gson gson;

    public SConfig(Path filePath) {
        this.filePath = filePath;
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        try {
            if (Files.notExists(filePath.getParent())) {
                Files.createDirectories(filePath.getParent());
            }
            if (Files.notExists(filePath)) {
                Files.createFile(filePath);
                try (FileWriter writer = new FileWriter(filePath.toFile())) {
                    writer.write("{}");
                }
            }

        } catch (IOException e) {
            SyncSignNotice.LOGGER.error(e.toString());
        }
        loadConfig();
    }

    private void loadConfig() {
        try (FileReader reader = new FileReader(filePath.toFile())) {
            this.jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            this.jsonObject = new JsonObject();
        }
    }

    public void save() {
        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            gson.toJson(jsonObject, writer);
        } catch (IOException e) {
            SyncSignNotice.LOGGER.error(e.toString());
        }
    }

    public void set(String key, Object value) {
        jsonObject.add(key, gson.toJsonTree(value));
    }

    public <T> T get(String key, Class<T> clazz) {
        return gson.fromJson(jsonObject.get(key), clazz);
    }

    public <T> T getOrDefault(String key, T defaultValue) {
        if (jsonObject.has(key)) {
            @SuppressWarnings("unchecked")
            Class<T> clazz = (Class<T>) defaultValue.getClass();
            return gson.fromJson(jsonObject.get(key), clazz);
        }
        else {
            set(key, defaultValue);
            save();
            return defaultValue;
        }
    }
}
