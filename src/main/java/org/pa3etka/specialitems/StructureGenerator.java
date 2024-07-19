package org.pa3etka.specialitems;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;


public class StructureGenerator extends SpecialItem {
    // Метод для генерации случайной локации в радиусе 100 блоков от текущей локации
public static Location generateRandomLocation(Location location) {
    int radius = 100;
    int x = location.getBlockX() + (int) (Math.random() * radius * 2 - radius);
    int z = location.getBlockZ() + (int) (Math.random() * radius * 2 - radius);
    World world = location.getWorld();

    int highestY = world.getHighestBlockYAt(x, z);

    return new Location(world, x, highestY, z);
}

// Метод для генерации сундука и записи координат в файл yml
public static void generateChest(Player player) {
    Location location = generateRandomLocation(player.getLocation());

    // Создать сундук на самом верхнем блоке
    location.setY(location.getWorld().getHighestBlockYAt(location) + 1);
    Block chestBlock = location.getBlock();
    chestBlock.setType(Material.CHEST);

    // Блокировка сундука для взаимодействия (сделать настройками конфига)
    Chest chest = (Chest) chestBlock.getState();
    chest.setMetadata("locked", new FixedMetadataValue(plugin, true));


    System.out.println("Сундук создан: " + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ());

//    // Планирование задачи по снятию блокировки через 30 секунд
//    Bukkit.getScheduler().runTaskLater(plugin, () -> {
//        if (chest.hasMetadata("locked")) {
//            chest.removeMetadata("locked", plugin);
//            System.out.println("Блокировка сундука снята");
//        }
//    }, 60000L);


    // Запись координат в yml
    //FileConfiguration config = getConfig();
    //List<String> chestLocations = config.getStringList("chestLocations");
    //chestLocations.add(location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ());
    //config.set("chestLocations", chestLocations);
    //saveConfig();

}


// Метод для генерации куба размером 3x3x3 из указанных блоков в точке, куда смотрит игрок
public static void generateCube(Player player, Material blockType) {
     // Блок, на который смотрит игрок в радиусе 5 блоков
    Block targetBlock = player.getTargetBlock(null, 5);

    if (targetBlock == null) {
        player.sendMessage(ChatColor.RED + "Вы не смотрите на блок. Не удается определить точку создания куба.");
        return;
    }
     // Получаем локацию блока с добавлением смещения для центрирования
    Location playerLocation = targetBlock.getLocation().add(0.5, 0.5, 0.5);
    World world = player.getWorld();

    // Начальные координаты для куба (центр куба)
    int startX = playerLocation.getBlockX() - 1;
    int startY = playerLocation.getBlockY();
    int startZ = playerLocation.getBlockZ() - 1;

    // Создаем куб размером 3x3x3 из указанных блоков
    for (int x = startX; x < startX + 3; x++) {
        for (int y = startY; y < startY + 3; y++) {
            for (int z = startZ; z < startZ + 3; z++) {
                world.getBlockAt(x, y, z).setType(blockType);
            }
        }
    }
}





}
