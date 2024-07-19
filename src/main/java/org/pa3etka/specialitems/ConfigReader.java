package org.pa3etka.specialitems;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ConfigReader extends SpecialItem{

public static ItemStack getItemFromConfig(String itemType, String itemName, FileConfiguration config) {
    if (itemType.equalsIgnoreCase("Ball")) {
        return getBallFromConfig(itemName, config);
    } else if (itemType.equalsIgnoreCase("Resister")) {
        return getResisterFromConfig(itemName, config);
    }
    return null; // Логика для других типов предметов
}

public static ItemStack getResisterFromConfig(String itemName, FileConfiguration config) {
    if (config.contains("ResisterItems." + itemName)) {
        ConfigurationSection itemSection = config.getConfigurationSection("ResisterItems." + itemName);

        Material material = Material.matchMaterial(itemSection.getString("Item"));
        int customModelData = itemSection.getInt("CustomModelData");

        ItemStack createdItemStack = new ItemStack(material);
        ItemMeta createdItemMeta = createdItemStack.getItemMeta();
        createdItemMeta.setDisplayName(itemSection.getString("Name"));
        createdItemMeta.setCustomModelData(customModelData);
        createdItemStack.setItemMeta(createdItemMeta);

        return createdItemStack;
    }
    return null;
}
// Насколько снаряд сломает предмет сопротивления снаряду
public static int readDamageItemResisterFromConfig(String projectileName, Map<String, Object> configData) {
    if (configData.containsKey("SpecialItems." + projectileName + ".ProjectileHit.DamageResisterItem")) {
        return (int) configData.get("SpecialItems." + projectileName + ".ProjectileHit.DamageResisterItem");
    }
    return 0; // Возвращение значения по умолчанию в случае отсутствия урона в конфигурации
}


    public static ItemStack getBallFromConfig(String itemName, FileConfiguration config) {
    if (config.contains("SpecialItems." + itemName)) {
        ConfigurationSection itemSection = config.getConfigurationSection("SpecialItems." + itemName);

        Material material = Material.matchMaterial(itemSection.getString("Item"));
        int customModelData = itemSection.getInt("CustomModelData");

        ItemStack createdItemStack = new ItemStack(material);
        ItemMeta createdItemMeta = createdItemStack.getItemMeta();
        createdItemMeta.setDisplayName(itemSection.getString("Name"));
        createdItemMeta.setCustomModelData(customModelData);
        createdItemStack.setItemMeta(createdItemMeta);

        return createdItemStack;
    }
    return null;
}

    public static List<PotionEffectType> readEffectOnHitFromConfig(String projectileName, Map<String, Object> configData) {
    List<PotionEffectType> effects = new ArrayList<>();
    System.out.println("Projectile потелел): " + projectileName);

    if (configData.containsKey("SpecialItems." + projectileName + ".ProjectileHit.PotionEffect")) {
        List<String> effectNames = (List<String>) configData.get("SpecialItems." + projectileName + ".ProjectileHit.PotionEffect");

    System.out.println("Projectile летит): " + projectileName);
        for (String name : effectNames) {
            PotionEffectType effectType = PotionEffectType.getByName(name);
            if (effectType != null) {
                effects.add(effectType);
            }
        }
    }

    return effects;
}

public static List<Particle> readParticleOnHitFromConfig(String projectileName, Map<String, Object> configData) {
    List<Particle> particleEffects = new ArrayList<>();

    // Проверяем наличие ключа в Map
    if (configData.containsKey("SpecialItems." + projectileName + ".ProjectileHit.ParticleEffect")) {
        Object particleEffectObj = configData.get("SpecialItems." + projectileName + ".ProjectileHit.ParticleEffect");

        if (particleEffectObj instanceof List<?> particleEffectNames) {

            for (Object obj : particleEffectNames) {
                if (obj instanceof String) {
                    String name = (String) obj;
                    particleEffects.add(Particle.valueOf(name));

                }
            }
        }
    }

    return particleEffects;
}

public static List<Sound> readSoundShooterFromConfig(String projectileName, Map<String, Object> configData) {
    List<Sound> soundShooter = new ArrayList<>();
    if (configData.containsKey("SpecialItems." + projectileName + ".ProjectileHit.Sounds.Shooter_sound")) {
        Object soundNameObj = configData.get("SpecialItems." + projectileName + ".ProjectileHit.Sounds.Shooter_sound");

        if (soundNameObj instanceof List<?> soundShooterNames) {

            for (Object obj : soundShooterNames) {
                if (obj instanceof String name) {
                    soundShooter.add(Sound.valueOf(name));
                }
            }
        } else if (soundNameObj instanceof String singleSoundName) {
            soundShooter.add(Sound.valueOf(singleSoundName));
        }

        return soundShooter;
    }

    return null;
}
public static double readDamageBallFromConfig(String projectileName, Map<String, Object> configData) {
    if (configData.containsKey("SpecialItems." + projectileName + ".ProjectileHit.Damage")) {
        double damageAmount = (double) configData.get("SpecialItems." + projectileName + ".ProjectileHit.Damage");
        return damageAmount;
    }
    return 0.0; // Возвращение значения по умолчанию в случае отсутствия урона в конфигурации
}

    private Sound readHitSoundFromConfig(String projectileName, FileConfiguration config) {
        if (config.contains("SpecialItems." + projectileName + ".ProjectileHit.Sounds.Hit_sound")) {
            String soundName = config.getString("SpecialItems." + projectileName + ".ProjectileHit.Sounds.Hit_sound");
            return Sound.valueOf(soundName);
        }
        return null;

    }

    private Sound readImpactSoundFromConfig(String projectileName, FileConfiguration config) {
        if (config.contains("SpecialItems." + projectileName + ".ProjectileHit.Sounds.Impact_sound")) {
            String soundName = config.getString("SpecialItems." + projectileName + ".ProjectileHit.Sounds.Impact_sound");
            return Sound.valueOf(soundName);
        }
        return null;
    }

}
