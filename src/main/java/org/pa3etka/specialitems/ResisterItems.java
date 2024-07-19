package org.pa3etka.specialitems;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.pa3etka.specialitems.SpecialItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.pa3etka.specialitems.ConfigReader.readDamageBallFromConfig;

public class ResisterItems extends SpecialItem {

    public static Object getItemValue(String key, ConfigurationSection itemSection) {
    switch (key) {
        case "Resist":
            return itemSection.getStringList("Resist");
        case "CustomModelData":
            return itemSection.getInt("CustomModelData");
        case "ResisteDamagePercent":
            return itemSection.getDouble("ResisteDamagePercent");
        case "ItemData":
            return itemSection.getItemStack("ItemData");
        default:
            return null;
    }
}

public static List<String> getResistItems(String key, ConfigurationSection itemSection) {
    Object value = getItemValue(key, itemSection);
    if (value instanceof List) {
        return (List<String>) value;
    } else {
        return new ArrayList<>();
    }
}

public static int getCustomModelData(String key, ConfigurationSection itemSection) {
    Object value = getItemValue(key, itemSection);
    if (value instanceof Integer) {
        return (int) value;
    } else {
        return 0;
    }
}

public static double getResisteDamagePercent(String key, ConfigurationSection itemSection) {
    Object value = getItemValue(key, itemSection);
    if (value instanceof Double) {
        return (double) value;
    } else {
        return 0.0;
    }
}


public static ItemStack hasResisterItem(Player player, String projectileName, Map<String, Object> configData, String valueKey) {
    if (configData.containsKey("ResisterItems")) {
        ConfigurationSection resisterItemsSection = (ConfigurationSection) configData.get("ResisterItems");
        Map<String, Object> resisterItemsMap = resisterItemsSection.getValues(false);

        for (Map.Entry<String, Object> entry : resisterItemsMap.entrySet()) {
            ConfigurationSection itemSection = (ConfigurationSection) entry.getValue();
            List<String> resistItems = getResistItems("Resist", itemSection);
            int customModelData = getCustomModelData("CustomModelData", itemSection);
            double damagePrejectileResistePercent = getResisteDamagePercent("ResisteDamageProjectilePercent", itemSection);
            double damageAllResistePercent = getResisteDamagePercent("ResisteDamageAllPercent", itemSection);


            boolean itemFound = false; // Флаг для отслеживания найденного предмета

            for (ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.getItemMeta().hasCustomModelData() && item.getItemMeta().getCustomModelData() == customModelData) {
                    if (resistItems.contains(projectileName) && !itemFound) {
                        System.out.println("resister found: " + item.getItemMeta().getDisplayName() + " " + item.getItemMeta().getCustomModelData() + " " + damagePrejectileResistePercent);
                        applyDamagePlayer(player, damagePrejectileResistePercent, readDamageBallFromConfig(projectileName, configData));
                        itemFound = true; // Устанавливаем флаг, что предмет найден и обработан
                    }
                }
                if (itemFound) {
                    // Возвращаем найденный предмет
                    return item;

                }
            }
        }
    }
    return null;
}



    public static void decreaseDurability(Player player, ItemStack itemName, int damageitemamount) {
        ItemStack resisterItem = null; // Предмет с минимальной прочностью
        int minDurability = Integer.MAX_VALUE; // Минимальная прочность - максимальное значение в начале

        // Итерация по содержимому инвентаря игрока
        for (ItemStack item : player.getInventory().getContents()) {
        if (item != null && item.getItemMeta().hasCustomModelData() && item.getItemMeta().getCustomModelData() == itemName.getItemMeta().getCustomModelData()) {
            int durability = item.getType().getMaxDurability() - item.getDurability();
            if (durability < minDurability) {
                minDurability = durability;
                resisterItem = item;
            }
        }
    }

        // Если найден предмет с минимальной прочностью
        if (resisterItem != null) {
            ItemMeta resisterItemMeta = resisterItem.getItemMeta();
            if (resisterItemMeta instanceof Damageable) {
                Damageable damageable = (Damageable) resisterItemMeta;
                int newDurability = damageable.getDamage() + damageitemamount; // Увеличение поломки на damageitemamount
                damageable.setDamage(newDurability); // Установка урона предмету
                resisterItem.setItemMeta(resisterItemMeta); // Применение изменений к предмету

                // Проверка, если прочность превышает максимальную прочность для данного типа предмета
                if (newDurability >= resisterItem.getType().getMaxDurability()) {
                    player.getInventory().removeItem(resisterItem); // Удаление предмета из инвентаря игрока
                    System.out.println("Игрок имеет особой предмет с наименьшей прочностью. Предмет сломан.");
                }
            }
        }
    }
    }




