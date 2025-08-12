package io.github.tanice.terraCraft.bukkit.items.components;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.TerraFoodComponent;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;

public class FoodComponent implements TerraFoodComponent {
    private final Boolean canAlwaysEat;
    private final int nutrition;
    private final float saturation;

    // 构造方法，初始化食物属性
    public FoodComponent(Boolean canAlwaysEat, int nutrition, float saturation) {
        this.canAlwaysEat = canAlwaysEat;
        this.nutrition = nutrition;
        this.saturation = saturation;
    }
    @Override
    public void apply(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt ->{
                ReadWriteNBT component = nbt.getOrCreateCompound(COMPONENT_KEY).getOrCreateCompound(MINECRAFT_PREFIX + "food");
                if (canAlwaysEat != null) component.setBoolean("can_always_eat", canAlwaysEat);
                component.setInteger("nutrition", nutrition);
                component.setFloat("saturation", saturation);
            });

        } else TerraCraftLogger.warning("Food component is only supported in Minecraft 1.20.5 or newer versions");
    }
}
