package de.kyleonaut.blacklist.player.service;

import de.kyleonaut.blacklist.player.models.BlacklistedPlayer;
import de.kyleonaut.blacklist.player.repository.BlacklistRepository;
import de.kyleonaut.blacklist.player.repository.BlacklistRepositoryImpl;
import lombok.Getter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class BlacklistPlayerService {
    private final BlacklistRepository repository;
    @Getter
    private List<BlacklistedPlayer> players;

    public BlacklistPlayerService() {
        this.repository = new BlacklistRepositoryImpl();
        this.players = this.repository.findPlayers();
    }

    /**
     * banPlayer() returns true if the player was banned successfully
     */
    public Boolean banPlayer(BlacklistedPlayer player) {
        if (players.contains(player)) {
            return false;
        }
        this.players.add(player);
        this.repository.savePlayers(players);
        return true;
    }

    public Optional<BlacklistedPlayer> findPlayer(UUID uniqueId) {
        return this.players.stream().filter(player -> player.getUniqueId().equals(uniqueId)).findFirst();
    }

    /**
     * removePlayer() returns true if the player was removed succesfully
     */
    public Boolean removePlayer(String playerName) {
        for (BlacklistedPlayer player : this.players) {
            if (player.getName().equals(playerName)) {
                this.players.remove(player);
                repository.savePlayers(this.players);
                return true;
            }
        }
        return false;
    }

    /**
     * isBanned() returns true if the player is banned
     */
    public Boolean isBanned(UUID uniqueId) {
        this.players = repository.findPlayers();
        for (BlacklistedPlayer player : this.players) {
            if (player.getUniqueId().toString().equals(uniqueId.toString())) {
                return true;
            }
        }
        return false;
    }
}
