package aedbia.showKey.configs;

import aedbia.showKey.ShowKey;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.electronwill.nightconfig.toml.TomlFormat;
import com.electronwill.nightconfig.toml.TomlParser;
import com.electronwill.nightconfig.toml.TomlWriter;
import net.minecraft.network.chat.Component;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@SuppressWarnings({"unused", "FieldCanBeLocal", "FieldMayBeFinal", "ResultOfMethodCallIgnored", "UnusedReturnValue"})
public class KeyConfig {
    private static List<KeyConfig> keyConfigs = new ArrayList<>();
    private final Path path;
    private final String fileName;
    private final String fileFullName;
    private CommentedConfig config;
    private List<Value<?>> values = new ArrayList<>();

    public KeyConfig(Path path, String fileName) {
        this.path = path;
        this.fileName = fileName;
        config = TomlFormat.newConfig();
        fileFullName = String.format(Locale.ROOT, "%s.toml", removeChar(Component.translatable(fileName).getString()));
        keyConfigs.add(this);
    }

    private static String removeChar(String fileName) {
        String b = fileName;
        String a = "/:\"|<>*?\\\\";
        for (char c : a.toCharArray()) {
            b = b.replace(Character.toString(c), "");
        }
        return b;
    }

    public static void loadAll() {
        keyConfigs.forEach(KeyConfig::load);
    }

    public static Value<?> getValue(String fileName, String path) {
        return getValue(null, fileName, path);
    }

    public static Value<?> getValue(Path local, String fileName, String path) {
        List<KeyConfig> k = keyConfigs.stream().filter(a -> Objects.equals(a.fileName, fileName) && (local == null || a.path == local)).toList();
        if (k.isEmpty()) {
            return null;
        }
        KeyConfig keyConfig = k.get(0);
        return keyConfig.getValue(path);
    }

    public void build() {
        if (!path.resolve(fileFullName).toFile().exists()) {
            try {
                path.resolve(fileFullName).toFile().createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (path.resolve(fileFullName).toFile().exists()) {
            load();
            write();
        }
    }

    private void write() {
        TomlWriter writer = new TomlWriter();
        writer.write(config, path.resolve(fileFullName), WritingMode.REPLACE);
    }

    private void load() {
        if (!path.toFile().exists()) {
            boolean a = path.toFile().mkdirs();
            ShowKey.LOGGER.debug("create configMkd" + a);
        }
        if (!path.resolve(fileFullName).toFile().exists()) {
            try {
                boolean a = path.resolve(fileFullName).toFile().createNewFile();
                ShowKey.LOGGER.debug("create config" + a);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        CommentedConfig config1;
        try (FileReader fileReader = new FileReader(String.valueOf(path.resolve(fileFullName)))) {
            TomlParser tomlParser = new TomlParser();
            config1 = tomlParser.parse(fileReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (config1 != null) {
            config1.checked();
            for (Value<?> value : values) {
                if (config1.contains(value.path)) {
                    if (config1.get(value.path).getClass() != value.defaultValue.getClass()) {
                        config1.set(value.path, value.defaultValue);
                    }
                    if (config.contains(value.path)) {
                        config.set(value.path, config1.get(value.path));
                    } else {
                        config.add(value.path, config1.get(value.path));
                    }
                } else {
                    config.add(value.path, value.defaultValue);

                }
                config.setComment(value.path, value.description);
            }
        }
    }

    public List<Value<?>> getValues() {
        return this.values;
    }

    public Value<?> getValue(String path) {
        List<Value<?>> values1 = values.stream().filter(a -> Objects.equals(a.path, path)).toList();
        if (values1.isEmpty()) {
            return null;
        }
        return values1.get(0);
    }

    public <T> Value<T> Add(String path, T defaultValue, String description) {
        return new Value<>(path, defaultValue, description);
    }

    public class Value<T> {
        final String path;
        private final T defaultValue;
        private final String description;

        Value(String path, T defaultValue, String description) {
            this.path = path;
            this.defaultValue = defaultValue;
            this.description = description;
            values.add(this);
        }

        public T get() {
            return config.get(this.path);
        }
    }
}
