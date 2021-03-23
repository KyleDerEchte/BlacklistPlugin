package de.kyleonaut.blacklist.player;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
@RequiredArgsConstructor
public final class BlacklistedPlayer {
    private final UUID uniqueId;
    private final String name;
    private final String reason;
    private final long id;
}
