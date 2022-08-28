package me.psk1103.enderdragontamer;

import net.minecraft.world.entity.boss.enderdragon.EntityEnderCrystal;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.dimension.end.EnderDragonBattle;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.boss.DragonBattle;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R1.boss.CraftDragonBattle;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftEntity;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.lang.reflect.Field;
import java.util.List;

public class BlockPlaceListener implements Listener {

    public EnderDragonTamer plugin;

    public BlockPlaceListener(EnderDragonTamer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEndCrystalPlace(PlayerInteractEvent e) {
        if (e.getMaterial() == Material.END_CRYSTAL && e.getClickedBlock() != null && e.getClickedBlock().getType() == Material.BEDROCK) {
            Block placedOn = e.getClickedBlock();
            plugin.getLogger().info("End crystal placed at " + e.getClickedBlock().getLocation());
            if(placedOn.getWorld().getEnvironment().equals(World.Environment.THE_END) &&placedOn.getBlockData().getMaterial().equals(Material.BEDROCK)) {
                plugin.getLogger().info("End crystal placed");
                World world = placedOn.getWorld();
                Bukkit.getScheduler().runTaskLater(plugin, () -> cancelEndPillarRespawn(world), 1);
            }
        }
    }

    private void cancelEndPillarRespawn(World world) {
        DragonBattle battle = world.getEnderDragonBattle();
        if(battle == null || battle.getEnderDragon() != null || battle.getRespawnPhase() == DragonBattle.RespawnPhase.NONE)
            return;
        plugin.getLogger().info("Cancelling end pillar respawn");
        DragonBattle.RespawnPhase phase = battle.getRespawnPhase();
        if(phase != DragonBattle.RespawnPhase.END)
            battle.setRespawnPhase(DragonBattle.RespawnPhase.END);
        try {
            CraftDragonBattle craftBattle = (CraftDragonBattle) battle;
            Field field = CraftDragonBattle.class.getDeclaredField("handle");
            field.setAccessible(true);
            EnderDragonBattle enderDragonBattle = (EnderDragonBattle) field.get(craftBattle);
            Field _z = EnderDragonBattle.class.getDeclaredField("z");
            _z.setAccessible(true);
            List<EntityEnderCrystal> crystals = (List<EntityEnderCrystal>) _z.get(enderDragonBattle);
            CraftWorld craftWorld = (CraftWorld) world;

            for (EntityEnderCrystal crystal : crystals) {
                Location loc = crystal.getBukkitEntity().getLocation();
                craftWorld.getHandle().a(crystal, loc.getX(), loc.getY(), loc.getZ(), 6.0F, Explosion.Effect.a);
            }
        }
        catch (NoSuchFieldException | IllegalAccessException | ClassCastException e) {
            e.printStackTrace();
        }
    }

    @EventHandler
    public void onEnderDragonBlockDestroy(EntityExplodeEvent event) {
        if (event.getEntityType() == EntityType.ENDER_DRAGON)
            event.setCancelled(true);
        if (event.getEntity() instanceof org.bukkit.entity.LargeFireball && (
                (Projectile)event.getEntity()).getShooter() instanceof org.bukkit.entity.EnderDragon)
            event.setCancelled(true);
    }

}
