package collinvht.f1mc.module.racing.object.race;

import collinvht.f1mc.util.Utils;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.sidebar.component.SidebarComponent;

public class RaceBoard {
    private Sidebar sidebar;
    public RaceBoard(Race race) {
        sidebar = Utils.getScoreboardLibrary().createSidebar();
    }

    public void update() {
    }
}
