package de.kyleonaut.blacklist;

import de.kyleonaut.blacklist.player.BlacklistController;
import lombok.Getter;
import lombok.SneakyThrows;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
public class BlacklistPlugin extends Plugin {
    public Configuration config;

    @SneakyThrows
    @Override
    public void onEnable() {
        File theDir = new File(ProxyServer.getInstance().getPluginsFolder().getPath()+"/BlacklistPlugin");
        if (!theDir.exists()){
            theDir.mkdirs();
        }
        this.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(this.getConfigPath().toFile());
        ProxyServer.getInstance().getPluginManager().registerListener(this, new BlacklistController(this));
        ProxyServer.getInstance().getPluginManager().registerCommand(this, new BlacklistController(this));
    }

    @SneakyThrows
    public Path getConfigPath() {
        final Path path = Paths.get("plugins", "BlacklistPlugin", "config.yml");
        if (Files.notExists(path)) {
            final InputStream in = BlacklistPlugin.class.getClassLoader().getResourceAsStream("config.yml");
            if (in == null) {
                throw new NullPointerException("Resource not found!");
            }
            Files.copy(in, path);
        }
        return path;
    }

    public String getMessage(String path) {
        return ChatColor.translateAlternateColorCodes(
                '&',
                this.config.getString("messages." + path, "§cNo Message with name" + path + " found")
        );
    }

    public BaseComponent getKickMessage(String reason) {
        String message = getConfig().getStringList("Blacklist").get(0) + "\n\n";
        message = message + getConfig().getStringList("Blacklist").get(2)
                .replace("%reason%", reason) + "\n";
        message = message + getConfig().getStringList("Blacklist").get(3) + "\n\n";
        message = message + getConfig().getStringList("Blacklist").get(5) + "\n";
        message = message + getConfig().getStringList("Blacklist").get(6);
        return new TextComponent(ChatColor.translateAlternateColorCodes('&', message));
    }
}
