package org.pa3etka.specialitems;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.pa3etka.specialitems.ConfigReader.*;

public class SpecialBall extends SpecialItem {
//    private PotionEffectType effectOnHit;
//    private Particle particleOnHit;
//    private Sound soundShooter;
//    private Sound hitSound;
//    private Sound impactSound;
//
//    public SpecialBall(PotionEffectType effectOnHit, Particle particleOnHit, Sound soundShooter, Sound hitSound, Sound impactSound) {
//        this.effectOnHit = effectOnHit;
//        this.particleOnHit = particleOnHit;
//        this.soundShooter = soundShooter;
//        this.hitSound = hitSound;
//        this.impactSound = impactSound;
//    }



public static boolean isSpecialProjectile(Projectile projectile, Map<String, Object> configData) {
        if (configData.containsKey("SpecialItems")) {
            ConfigurationSection specialItemsSection = (ConfigurationSection) configData.get("SpecialItems");
            Map<String, Object> specialItemsMap = specialItemsSection.getValues(false);

            for (Object value : specialItemsMap.values()) {
                if (value instanceof ConfigurationSection itemSection) {
                    int customModelData = itemSection.getInt("CustomModelData");

                    if (projectile instanceof Snowball && hasMatchingCustomModelData(projectile, customModelData)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public static String applyEffects(Projectile projectile, Location hitLocation, Entity hitEntity, Map<String, Object> configData) {
    String projectileName = "Неизвестный снаряд";

    if (configData.containsKey("SpecialItems")) {
        ConfigurationSection specialItemsSection = (ConfigurationSection) configData.get("SpecialItems");
        Map<String, Object> specialItemsMap = specialItemsSection.getValues(false);
        //System.out.println("1: " + specialItemsMap);

        for (Map.Entry<String, Object> entry : specialItemsMap.entrySet()) {
            Object value = entry.getValue();
            //System.out.println("2: " + value);

           if (value instanceof ConfigurationSection itemSection) {
               int customModelData = itemSection.getInt("CustomModelData");
                //System.out.println("3: " + customModelData);

                if (projectile instanceof Snowball && hasMatchingCustomModelData(projectile, customModelData)) {
                    projectileName = entry.getKey();
                    //System.out.println("4: " + projectileName);
                    //Применение эффектов снаряда к цели - если попало в игрока

        // Попадание снаряда в игрока
//        if (hitEntity instanceof Player hitPlayer) {
//            hitPlayer.damage(5);
//
//            // Применение эффектов снаряда к игроку
//            if (!effectTypes.isEmpty()) {
//                for (PotionEffectType effectType : effectTypes) {
//                    hitPlayer.addPotionEffect(new PotionEffect(effectType, 20, 1));
//                }
//            }
//
//            // Применение частиц снаряда к игроку
//            if (!particleTypes.isEmpty()) {
//                for (Particle particleType : particleTypes) {
//                    displayParticleEffect(particleType, hitLocation);
//                }
//                launchProjectile(player, projectileName, hitLocation, hitEntity, configData);
//            }
        //}
                    break;
                }
            }
        }

    }

    return projectileName;
}






}
