package de.kyleonaut.blacklist.player.repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import de.kyleonaut.blacklist.BlacklistPlugin;
import de.kyleonaut.blacklist.player.models.BlacklistedPlayer;
import lombok.SneakyThrows;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Spliterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class BlacklistRepositoryImpl implements BlacklistRepository {
    private final Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
    private final Path path = this.getPath();

    @SneakyThrows
    private Path getPath() {
        final Path path = Paths.get("plugins", "BlacklistPlugin", "blacklist.json");
        if (Files.notExists(path)) {
            final InputStream in = BlacklistPlugin.class.getClassLoader().getResourceAsStream("blacklist.json");
            if (in == null) {
                throw new NullPointerException("Resource not found!");
            }
            Files.copy(in, path);
        }
        return path;
    }

    @SneakyThrows
    @Override
    public List<BlacklistedPlayer> findPlayers() {
        final FileReader fileReader = new FileReader(this.path.toFile());
        final JsonArray array = gson.fromJson(fileReader, JsonArray.class);
        final Spliterator<JsonElement> spliterator = array.spliterator();
        final List<BlacklistedPlayer> players = StreamSupport.stream(spliterator, false)
                .map(element -> gson.fromJson(element, BlacklistedPlayer.class))
                .collect(Collectors.toList());
        fileReader.close();
        return players;
    }

    @SneakyThrows
    public void savePlayers(Collection<BlacklistedPlayer> blacklistedPlayers) {
        final FileWriter fileWriter = new FileWriter(this.path.toFile());
        fileWriter.write(gson.toJson(blacklistedPlayers));
        fileWriter.close();
    }
}
