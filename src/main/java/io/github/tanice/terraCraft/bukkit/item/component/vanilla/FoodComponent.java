package io.github.tanice.terraCraft.bukkit.item.component.vanilla;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.github.tanice.terraCraft.api.item.component.vanilla.TerraFoodComponent;
import io.github.tanice.terraCraft.bukkit.util.version.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.util.version.ServerVersion;
import io.github.tanice.terraCraft.core.util.logger.TerraCraftLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.Objects;

public class FoodComponent implements TerraFoodComponent {
    @Nullable
    private final Boolean canAlwaysEat;
    private final int nutrition;
    private final float saturation;

    public FoodComponent(@Nullable Boolean canAlwaysEat, int nutrition, float saturation) {
        this.canAlwaysEat = canAlwaysEat;
        this.nutrition = nutrition;
        this.saturation = saturation;
    }

    public FoodComponent(ConfigurationSection cfg) {
        this(
                cfg.isSet("always_eat") ? cfg.getBoolean("always_eat") : null,
                cfg.getInt("nutrition"),
                cfg.getInt("saturation")
        );
    }

    @Override
    public void cover(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt ->{
                ReadWriteNBT component = nbt.getOrCreateCompound(MINECRAFT_PREFIX + "food");
                if (canAlwaysEat != null) component.setBoolean("can_always_eat", canAlwaysEat);
                component.setInteger("nutrition", nutrition);
                component.setFloat("saturation", saturation);
            });
        } else TerraCraftLogger.warning("Food component is only supported in Minecraft 1.20.5 or newer versions");
    }

    @Override
    public String getComponentName() {
        return "food";
    }

    public static void clear(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt ->{
                nbt.removeKey(MINECRAFT_PREFIX + "food");
            });
        }
    }

    public static void remove(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt ->{
                nbt.removeKey(MINECRAFT_PREFIX + "food");
                nbt.getOrCreateCompound("!" + MINECRAFT_PREFIX + "food");
            });
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(canAlwaysEat, nutrition, saturation);
    }
}
