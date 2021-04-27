package de.kyleonaut.blacklist.player.repository;

import de.kyleonaut.blacklist.player.models.BlacklistedPlayer;

import java.util.Collection;
import java.util.List;

public interface BlacklistRepository {
    List<BlacklistedPlayer> findPlayers();
    void savePlayers(Collection<BlacklistedPlayer> players);
}
