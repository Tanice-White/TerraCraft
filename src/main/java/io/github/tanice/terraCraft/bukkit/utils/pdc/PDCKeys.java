package io.github.tanice.terraCraft.bukkit.utils.pdc;

import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import org.bukkit.NamespacedKey;

public final class PDCKeys {
    /** 所有插件物品必须有的 */
    public static final NamespacedKey TERRA_NAME = new NamespacedKey(TerraCraftBukkit.inst(), "terra_name");
    public static final NamespacedKey CODE = new NamespacedKey(TerraCraftBukkit.inst(), "code");

    public static final NamespacedKey LEVEL = new NamespacedKey(TerraCraftBukkit.inst(), "level");
    public static final NamespacedKey QUALITY = new NamespacedKey(TerraCraftBukkit.inst(), "quality");
    public static final NamespacedKey GEMS = new NamespacedKey(TerraCraftBukkit.inst(), "gems");
    public static final NamespacedKey DAMAGE = new NamespacedKey(TerraCraftBukkit.inst(), "damage");
    public static final NamespacedKey MAX_DAMAGE = new NamespacedKey(TerraCraftBukkit.inst(), "max_damage");
}
