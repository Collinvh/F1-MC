package collinvht.projectr.commands.util;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

public class MutedOBJ {


    @Getter @Setter
    private ArrayList<String> words = new ArrayList<>();

    @Getter @Setter
    private ArrayList<UUID> players = new ArrayList<>();

    @Getter @Setter
    private boolean chatMuted;

    @Getter
    private final UUID uuid;


    public MutedOBJ(UUID uuid) {
        this.uuid = uuid;
    }

    public void addWord(String word) {
        words.add(word);
    }

    public void addPlayer(UUID player) {
        players.add(player);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }
}
