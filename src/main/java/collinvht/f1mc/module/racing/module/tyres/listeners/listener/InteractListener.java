package collinvht.f1mc.module.racing.module.tyres.listeners.listener;

import collinvht.f1mc.module.racing.module.team.manager.TeamManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class InteractListener implements Listener {
    private static int playerInteractEventCooldown;
    @EventHandler
    public static void playerInteractEvent(PlayerInteractEvent event) {
        if(event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if(event.getClickedBlock() != null) {
                if(event.getClickedBlock().getBlockData().getMaterial().equals(Material.CHAIN)) {
                    if(playerInteractEventCooldown == 0) {
                        TyreGUI.open(event.getPlayer(), TeamManager.getTeamForPlayer(event.getPlayer()));
                        event.setCancelled(true);
                        playerInteractEventCooldown = 1;
                    } else {
                        playerInteractEventCooldown -= 1;
                    }
                }
            }
        }
    }
}
