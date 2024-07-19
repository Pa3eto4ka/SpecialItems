package org.pa3etka.specialitems;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.io.File;
import java.util.*;

import static org.pa3etka.specialitems.ResisterItems.hasResisterItem;
import static org.pa3etka.specialitems.SpecialBall.*;
import static org.pa3etka.specialitems.ConfigReader.*;

public class SpecialItem extends JavaPlugin implements Listener {

    public Map<String, Object> configData = new HashMap<>();
    public static Plugin plugin;

    @Override
    public void onEnable() {
        plugin = this;
        getServer().getPluginManager().registerEvents(this, this);
        loadSpecialItems();
    }

    public FileConfiguration loadConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            saveResource("config.yml", false);
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(configFile);

        addConfigEntries("", config.getValues(true));

//        for (Map.Entry<String, Object> entry : configData.entrySet()) {
//            System.out.println("Key: " + entry.getKey() + ", Value: " + entry.getValue());
//        }

        return config;
    }

    private void addConfigEntries(String prefix, Map<?, ?> configSection) {
        for (Map.Entry<?, ?> entry : configSection.entrySet()) {
            if (entry.getValue() instanceof Map) {
                addConfigEntries(prefix + entry.getKey() + ".", (Map<?, ?>) entry.getValue());
            } else {
                configData.put(prefix + entry.getKey().toString(), entry.getValue());
            }
        }
    }

    private void loadSpecialItems() {
        FileConfiguration config = loadConfig();
        ConfigurationSection itemsSection = config.getConfigurationSection("SpecialItems");

        if (itemsSection != null) {
            for (String itemName : itemsSection.getKeys(false)) {
                // Создание предмета из конфига
                getBallFromConfig(itemName, config);
            }
        }
    }



    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        FileConfiguration config = loadConfig();
        // Генерация сундука
        if (label.equalsIgnoreCase("specialitems") && args.length > 0 && args[0].equalsIgnoreCase("generate") && args.length > 1 && args[1].equalsIgnoreCase("chest")) {
            if (sender instanceof Player) {
                StructureGenerator.generateChest((Player) sender);
                sender.sendMessage("Сундук сгенерирован в случайном месте!");
            } else {
                sender.sendMessage("Команду можно использовать только от имени игрока.");
            }
            return true;
        }
        //Генерация куба
        if (label.equalsIgnoreCase("specialitems") && args.length > 0 && args[0].equalsIgnoreCase("generate") && args.length > 1 && args[1].equalsIgnoreCase("cube")) {
            if (sender instanceof Player) {
                if (args.length == 1) {
                    sender.sendMessage(ChatColor.RED + "Использование: /specialitems generate cube <тип_блока>");
                    return true;
                }

                Material blockType = Material.matchMaterial(args[2]);

                if (blockType == null) {
                    sender.sendMessage(ChatColor.RED + "Неверный тип блока.");
                    return true;
                }

                StructureGenerator.generateCube((Player) sender, blockType);
                sender.sendMessage(ChatColor.GREEN + "Куб успешно создан.");
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "Команду могут использовать только игроки.");
                return true;
            }
        }

        if (cmd.getName().equalsIgnoreCase("specialitems")) {


            if (args[0].equalsIgnoreCase("list")) {
                sender.sendMessage(ChatColor.GREEN + "Список всех особых предметов:");
                config = loadConfig();
                if (config.contains("SpecialItems")) {
                    ConfigurationSection itemsSection = config.getConfigurationSection("SpecialItems");

                    for (String itemName : itemsSection.getKeys(false)) {
                        sender.sendMessage(ChatColor.YELLOW + "- " + itemName);
                    }
                }
                if (config.contains("ResiterItems")) {
                    ConfigurationSection itemsSection = config.getConfigurationSection("ResiterItems");

                    for (String itemName : itemsSection.getKeys(false)) {
                        sender.sendMessage(ChatColor.YELLOW + "+ " + itemName);
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Нет доступных особых предметов.");
                }
                return true;

            }

            if (args.length < 4 || !args[0].equalsIgnoreCase("give") && !args[0].equalsIgnoreCase("list")) {
                sender.sendMessage(ChatColor.RED + "Использование: /specialitems give <игрок> <тип_предмета> <название_предмета> <количество>");
                return true;
            }


            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "Эту команду может использовать только игрок.");
                return true;
            }
            Player targetPlayer = Bukkit.getPlayer(args[1]);
            if (targetPlayer == null) {
                sender.sendMessage(ChatColor.RED + "Игрок не найден.");
                return true;
            }

            String itemType = args[2];
            String itemName = args[3];
            ItemStack itemStack = ConfigReader.getItemFromConfig(itemType, itemName, config);
            if (itemStack == null) {
                sender.sendMessage(ChatColor.RED + "Предмет с названием '" + itemName + "' не найден в конфигурации.");
                return true;
            }

            int amount;
            try {
                amount = Integer.parseInt(args[4]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.RED + "Количество должно быть числом.");
                return true;
            }

            itemStack.setAmount(amount);
            targetPlayer.getInventory().addItem(itemStack);
            sender.sendMessage(ChatColor.GREEN + "Вы выдали игроку " + targetPlayer.getName() + " " + amount + " предметов '" + itemName + "'.");
            return true;

        }
        return false;
    }


    public static boolean hasMatchingCustomModelData(Projectile projectile, int customModelData) {
        if (projectile instanceof Snowball) {
            Snowball snowball = (Snowball) projectile;
            ItemMeta itemMeta = snowball.getItem().getItemMeta();
            if (itemMeta != null && itemMeta.hasCustomModelData() && itemMeta.getCustomModelData() == customModelData) {
                return true;
            }
        }
        return false;
    }




    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        Location hitLocation = event.getHitBlock() != null ? event.getHitBlock().getLocation() : event.getHitEntity().getLocation();
        Entity hitEntity = event.getHitEntity();
        System.out.println("Projectile hit at " + hitLocation.getX() + ", " + hitLocation.getY() + ", " + hitLocation.getZ());

        if (projectile.getShooter() instanceof Player player) {

            // Проверяем, является ли снаряд специальным
            if (projectile instanceof Snowball && SpecialBall.isSpecialProjectile(projectile, configData)) {
                String projectileName = SpecialBall.applyEffects(projectile, hitLocation, hitEntity, configData);

                launchProjectile(player, projectileName, hitLocation, hitEntity, configData);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Block clickedBlock = event.getClickedBlock();
            if (clickedBlock.getState() instanceof Chest) {
                Chest chest = (Chest) clickedBlock.getState();
                if (chest.hasMetadata("locked") && chest.getMetadata("locked").get(0).asBoolean()) {
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("Этот сундук заблокирован и не может быть открыт.");

                }


            }
        }
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        List<Block> affectedBlocks = event.blockList();
        for (Block block : affectedBlocks) {
            if (block.getState() instanceof Chest) {
                if ((block.getState()).hasMetadata("locked") && block.getState().getMetadata("locked").get(0).asBoolean()) {
                    event.setCancelled(true); // Отменить взрыв возле сундука
                }
            }
        }
    }

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        if (projectile.getShooter() instanceof Player) {
            Player player = (Player) projectile.getShooter();

            // Проверяем, что снаряд запущенный игроком
            // Добавьте здесь вашу логику для обработки запуска снаряда
            //частички за снарядом
            // Запуск снаряда
            //Projectile projectile = player.launchProjectile(Snowball.class);
            //projectile.setCustomName(projectileName);

            // Получение вектора направления движения снаряда
            Vector direction = player.getLocation().getDirection();

            new BukkitRunnable() {
                int count = 0;

                @Override
                public void run() {
                    Particle.DustTransition dustTransition = new Particle.DustTransition(Color.LIME, Color.GREEN, 1);
                    Location particleLocation = projectile.getLocation().add(direction);
                    particleLocation.getWorld().spawnParticle(Particle.DUST_COLOR_TRANSITION, particleLocation.getX(), particleLocation.getY(), particleLocation.getZ(), 1, 0, 0, 0, 0, dustTransition);

                    if (projectile.isDead() || count >= 150) {
                        cancel();
                    }
                }
            }.runTaskTimer(plugin, 0L, 1L);
        }
    }

    public void launchProjectile(Player player, String projectileName, Location hitLocation, Entity hitEntity, Map<String, Object> configData) {
        // Код для запуска снаряда


        // Получение параметров конфигурации снаряда
        List<PotionEffectType> effectTypes = readEffectOnHitFromConfig(projectileName, configData);
        List<Particle> particleTypes = readParticleOnHitFromConfig(projectileName, configData);
        List<Sound> soundShooter = readSoundShooterFromConfig(projectileName, configData);
        System.out.println("Projectile " + projectileName + " launched from player " + player.getName() + " at " + hitLocation.getX() + ", " + hitLocation.getY() + ", " + hitLocation.getZ());
        //Приминение эффектов на попадание

        // Вывод параметров в консоль

        if (hitEntity instanceof Player hitPlayer) {
            if (effectTypes != null && !effectTypes.isEmpty()) {


                if (hasResisterItem(hitPlayer, projectileName, configData, "CustomModelData")!=null) {
                    // Если игрок имеет особый предмет на сопротивление, то применяем его
                    ResisterItems.decreaseDurability(player, hasResisterItem(hitPlayer, projectileName, configData, "ItemData"), readDamageItemResisterFromConfig(projectileName, configData));



                    //hitPlayer.getInventory().removeItem(selectedSpecialItem);
//                selectedSpecialItem.setItemMeta(selectedSpecialItem);
//                hitPlayer.getInventory().addItem(selectedSpecialItem);
                } else {
                    // Применяем эффекты от снаряда
                    hitPlayer.damage(readDamageBallFromConfig(projectileName, configData));
                    System.out.println("Damage " + readDamageBallFromConfig(projectileName, configData));
                    System.out.print("Снаряд '" + projectileName + "' запущен. Параметры эффекта на попадание: ");
                    for (PotionEffectType effectType : effectTypes) {
                        System.out.print(effectType.getName() + " ");
                        hitPlayer.addPotionEffect(new PotionEffect(effectType, 20, 1));
                    }
                    System.out.println();
                }

            }
        } else {
            System.out.println("Снаряд '" + projectileName + "' запущен. Не удалось получить параметры эффекта на попадание.");
        }


        // Вывод типов частиц в консоль
        if (particleTypes != null && !particleTypes.isEmpty()) {
            System.out.print("Снаряд '" + projectileName + "' запущен. Типы частиц при попадании: ");
            for (Particle particleType : particleTypes) {
                System.out.print(particleType.toString() + " ");
                displayParticleEffect(particleType, hitLocation);


            }
            System.out.println();

        } else {
            System.out.println("Снаряд '" + projectileName + "' запущен. Не удалось получить параметры частиц при попадании.");
        }
        if (soundShooter != null && !soundShooter.isEmpty()) {
            System.out.print("Снаряд '" + projectileName + "' запущен. Звуки при стрельбе: ");
            for (Sound sound : soundShooter) {
                System.out.print(sound.toString() + " ");
            }
            System.out.println();
        } else {
            System.out.println("Снаряд '" + projectileName + "' запущен. Не удалось получить параметры звуков при стрельбе.");
        }

    }
    public static void applyDamagePlayer(Player player, double damageResisterPercent, double damageItem) {
        System.out.println("Нанесён урон " + damageItem);
        System.out.println("Урон сопротивления " + damageResisterPercent);
        System.out.println("Итоговый урон " + (damageItem*(1-damageResisterPercent)));
        player.damage(damageItem*(1-damageResisterPercent));
    }

    public void displayParticleEffect(Particle particleType, Location location) {
        new BukkitRunnable() {
            int count = 0;

            @Override
            public void run() {
                location.getWorld().spawnParticle(particleType, location.getX(), location.getY(), location.getZ(), 10, 0.5, 0.5, 0.5, 0.1);
                if (++count >= 5) {
                    cancel();
                }
            }
        }.runTaskTimer(this, 0L, 10L);
    }
/*
@EventHandler
public void onPlayerTakeDamage(EntityDamageEvent event) {
    if (event.getEntity() instanceof Player) {
        Player player = (Player) event.getEntity();

        // Проверяем наличие предмета на сопротивление урона у игрока
        if (hasResisterItem(player)) {
            double damageReductionPercent = getDamageReductionPercent(player); // Получаем процент снижения урона из конфига

            // Уменьшаем входящий урон на указанный процент
            double damage = event.getDamage();
            double reducedDamage = damage - (damage * damageReductionPercent / 100);

            // Устанавливаем уменьшенный урон
            event.setDamage(reducedDamage);
        }
    }
}

 */

    // Обработка события спавна мобов
    @EventHandler
    public void onMobSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();
// Появление мобов в броне и с мечем
        if (entity instanceof Zombie || entity instanceof Skeleton) {
            // Генерируем случайный вид брони
            ItemStack helmet = generateRandomArmorPiece(ArmorType.HELMET);
            ItemStack chestplate = generateRandomArmorPiece(ArmorType.CHESTPLATE);
            ItemStack leggings = generateRandomArmorPiece(ArmorType.LEGGINGS);
            ItemStack boots = generateRandomArmorPiece(ArmorType.BOOTS);

            // Устанавливаем броню на моба
            LivingEntity livingEntity = (LivingEntity) entity;
            livingEntity.getEquipment().setHelmet(helmet);
            livingEntity.getEquipment().setChestplate(chestplate);
            livingEntity.getEquipment().setLeggings(leggings);
            livingEntity.getEquipment().setBoots(boots);

            // Генерируем случайный вид меча
            ItemStack sword = generateRandomSword();

            // Устанавливаем меч на моба
            livingEntity.getEquipment().setItemInMainHand(sword);
        }
    }

    public enum ArmorType {
        HELMET,
        CHESTPLATE,
        LEGGINGS,
        BOOTS
    }

    private ItemStack generateRandomArmorPiece(ArmorType armorType) {
        List<ItemStack> armorPieces = new ArrayList<>();

        // Добавляем соответствующие виды брони в список в зависимости от типа брони
        switch (armorType) {
            case HELMET:
                // Добавляем виды брони с их вероятностями
                addArmorWithProbability(armorPieces, new ItemStack(Material.DIAMOND_HELMET), 80); // Вероятность 80%
                addArmorWithProbability(armorPieces, new ItemStack(Material.IRON_HELMET), 20);
                // Добавьте другие виды шлемов с их вероятностями
                break;
            case CHESTPLATE:
                // Добавляем виды брони с их вероятностями
                addArmorWithProbability(armorPieces, new ItemStack(Material.DIAMOND_CHESTPLATE), 70);
                addArmorWithProbability(armorPieces, new ItemStack(Material.IRON_CHESTPLATE), 30);
                // Добавьте другие виды нагрудников с их вероятностями
                break;
            case LEGGINGS:
                // Добавляем виды брони с их вероятностями
                addArmorWithProbability(armorPieces, new ItemStack(Material.DIAMOND_LEGGINGS), 60);
                addArmorWithProbability(armorPieces, new ItemStack(Material.IRON_LEGGINGS), 40);
                // Добавьте другие виды поножей с их вероятностями
                break;
            case BOOTS:
                // Добавляем виды брони с их вероятностями
                addArmorWithProbability(armorPieces, new ItemStack(Material.DIAMOND_BOOTS), 50);
                addArmorWithProbability(armorPieces, new ItemStack(Material.IRON_BOOTS), 50);
                // Добавьте другие виды ботинок с их вероятностями
                break;
        }

        // Выбираем случайный комплект брони из списка с учетом вероятностей
        ItemStack randomArmorPiece = getWeightedRandomItem(armorPieces);

        return randomArmorPiece;
    }

    private void addArmorWithProbability(List<ItemStack> armorPieces, ItemStack armorPiece, int probability) {
        // Добавляем предмет в список заданное количество раз в зависимости от вероятности
        for (int i = 0; i < probability; i++) {
            armorPieces.add(armorPiece);
        }
    }

    private ItemStack getWeightedRandomItem(List<ItemStack> items) {
        // Выбор случайного предмета с учетом его веса (вероятности)
        int totalWeight = items.stream().mapToInt(item -> getItemWeight(item)).sum();
        int randomWeight = (int) (Math.random() * totalWeight);

        for (ItemStack item : items) {
            randomWeight -= getItemWeight(item);
            if (randomWeight <= 0) {
                return item;
            }
        }

        // Если не удалось выбрать предмет, вернуть null
        return null;
    }

    private int getItemWeight(ItemStack item) {
        // Возвращаем вес (вероятность) предмета
        return 1; // Вес предмета по вероятности
    }

    private ItemStack generateRandomSword() {
        // Список доступных видов мечей
        List<ItemStack> swords = new ArrayList<>();

        // Добавляем различные виды мечей в список
        swords.add(new ItemStack(Material.DIAMOND_SWORD));
        swords.add(new ItemStack(Material.IRON_SWORD));
        swords.add(new ItemStack(Material.GOLDEN_SWORD));
        swords.add(new ItemStack(Material.STONE_SWORD));
        swords.add(new ItemStack(Material.WOODEN_SWORD));

        // Случайный меч из списка
        ItemStack randomSword = swords.get(new Random().nextInt(swords.size()));

        return randomSword;
    }


}