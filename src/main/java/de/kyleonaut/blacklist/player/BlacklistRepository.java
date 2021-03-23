package de.kyleonaut.blacklist.player;

import java.util.Collection;
import java.util.List;

public interface BlacklistRepository {
    List<BlacklistedPlayer> findPlayers();
    void savePlayers(Collection<BlacklistedPlayer> players);
}
