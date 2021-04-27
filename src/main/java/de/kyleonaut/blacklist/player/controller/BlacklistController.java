package de.kyleonaut.blacklist.player.controller;

import com.google.common.base.Joiner;
import de.kyleonaut.blacklist.BlacklistPlugin;
import de.kyleonaut.blacklist.player.models.BlacklistedPlayer;
import de.kyleonaut.blacklist.player.service.BlacklistPlayerService;
import lombok.SneakyThrows;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.md_5.bungee.event.EventHandler;

import java.util.Arrays;
import java.util.List;

public class BlacklistController extends Command implements Listener {
    private final BlacklistPlugin plugin;
    private final BlacklistPlayerService service;

    public BlacklistController(BlacklistPlugin plugin) {
        super("blacklist", "blacklist");
        super.setPermissionMessage(plugin.getMessage("NoPermissions"));
        this.plugin = plugin;
        this.service = new BlacklistPlayerService();
    }

    @SneakyThrows
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length >= 1 && args[0].equalsIgnoreCase("list")) {
            if (!sender.hasPermission("blacklist.list")) {
                sender.sendMessage(new TextComponent(plugin.getMessage("NoPermissions")));
                return;
            }
            final List<BlacklistedPlayer> players = this.service.getPlayers();
            if (players.isEmpty()) {
                sender.sendMessage(new TextComponent(plugin.getMessage("BlacklistNoData")));
                return;
            }
            sender.sendMessage(new TextComponent(plugin.getMessage("Blacklist-Title")));
            players.forEach(player -> {
                final int position = players.indexOf(player) + 1;
                sender.sendMessage(new TextComponent(plugin.getMessage("Blacklist-Format")
                        .replace("%pos%", "" + position)
                        .replace("%player%", player.getName())));
                sender.sendMessage(new TextComponent(plugin.getMessage("BlacklistReason")
                        .replace("%reason%", player.getReason())));
            });
        } else if (args.length >= 2) {
            if (args[0].equalsIgnoreCase("remove")) {
                final String playerName = args[1];
                if (!sender.hasPermission("blacklist.remove")) {
                    sender.sendMessage(new TextComponent(plugin.getMessage("NoPermissions")));
                    return;
                }
                if (service.removePlayer(playerName)) {
                    sender.sendMessage(new TextComponent(
                            plugin.getMessage("UnBlacklisted")
                                    .replace("%player%", playerName)));
                    return;
                }
                sender.sendMessage(new TextComponent(
                        plugin.getMessage("NotBlacklisted").replace("%player%", playerName)));
            } else if (sender.hasPermission("blacklist")) {
                final String playerName = args[0];
                final ProxiedPlayer target = ProxyServer.getInstance().getPlayer(playerName);
                if (target == null) {
                    sender.sendMessage(new TextComponent(plugin.getMessage("PlayerNotFound")));
                    return;
                }
                final String reason = Joiner.on(" ").join(Arrays.copyOfRange(args, 1, args.length));
                if (!target.hasPermission("blacklist.exempt")) {
                    if (service.findPlayer(target.getUniqueId()).isPresent()) {
                        sender.sendMessage(new TextComponent(
                                plugin.getMessage("IsBlacklisted").replace("%player%", target.getName())));
                        return;
                    }
                    if (service.banPlayer(new BlacklistedPlayer(target.getUniqueId(), playerName, reason, 0))) {
                        sender.sendMessage(new TextComponent(
                                plugin.getMessage("Blacklisted").replace("%player%", playerName)));
                        target.disconnect(plugin.getKickMessage(reason));
                    } else {
                        sender.sendMessage(new TextComponent(
                                plugin.getMessage("IsBlacklisted").replace("%player%", playerName)));
                    }
                    return;
                }
                sender.sendMessage(new TextComponent(plugin.getMessage("Exempted")));
            } else {
                sender.sendMessage(new TextComponent(plugin.getMessage("NoPermissions")));
            }
        } else if (sender.hasPermission("blacklist.admin") && args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            plugin.config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(plugin.getConfigPath().toFile());
            sender.sendMessage(new TextComponent(plugin.getMessage("Reload")));
        } else if (sender.hasPermission("blacklist")) {
            sender.sendMessage(new TextComponent(plugin.getMessage("IncorrectUsage").replace(
                    "%usage%",
                    "\n" +
                            "/blacklist <player> <reason> \n" +
                            "/blacklist <remove> <player> \n" +
                            "/blacklist list"
            )));
        } else {
            sender.sendMessage(new TextComponent(plugin.getMessage("NoPermissions")));
        }
    }

    @EventHandler
    public void onJoin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();
        if (service.isBanned(player.getUniqueId())) {
            if (service.findPlayer(player.getUniqueId()).isPresent()) {
                final String reason = service.findPlayer(player.getUniqueId()).get().getReason();
                player.disconnect(plugin.getKickMessage(reason));
            }
        }
    }
}
