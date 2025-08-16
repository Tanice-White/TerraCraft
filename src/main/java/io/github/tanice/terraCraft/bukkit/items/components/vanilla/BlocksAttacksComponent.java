package io.github.tanice.terraCraft.bukkit.items.components.vanilla;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBTCompoundList;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.vanilla.TerraBlocksAttacksComponent;
import io.github.tanice.terraCraft.bukkit.utils.nbtapi.NBTSound;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.utils.namespace.TerraNamespaceKey;
import org.bukkit.configuration.ConfigurationSection;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class BlocksAttacksComponent implements TerraBlocksAttacksComponent {
    @Nullable
    private final Float blockDelaySeconds;
    @Nullable
    private final NBTSound blockSound;
    @Nullable
    private final TerraNamespaceKey canPass;
    private final List<DamageReduction> damageReductions;
    @Nullable
    private final Float disableCooldownScale;
    @Nullable
    private final NBTSound disabledSound;
    private final float base;
    private final float factor;
    private final float threshold;

    /**
     * 允许自定义伤害标签
     * @param blockDelaySeconds （值≥0，默认为0）成功阻挡攻击前需要按住右键的秒数
     * @param blockSound 成功阻挡攻击时播放的声音
     * @param canPass 能够穿过的伤害标签
     * @param disableCooldownScale 被可停用阻挡的攻击击中时，物品冷却时长的乘数
     * @param disabledSound 此物品被攻击禁用时播放的声音
     * 物品耐久最终损耗floor(threshold, base + factor * 所受攻击伤害)
     * @param base 固定阻挡的伤害
     * @param factor 应被阻挡的伤害比例
     * @param threshold （值≥0）攻击对此物品造成的最低耐久度损耗
     */
    public BlocksAttacksComponent(@Nullable Float blockDelaySeconds, @Nullable NBTSound blockSound, @Nullable TerraNamespaceKey canPass, @Nullable Float disableCooldownScale, @Nullable NBTSound disabledSound, float base, float factor, float threshold) {
        this.blockDelaySeconds = blockDelaySeconds;
        this.blockSound = blockSound;
        this.canPass = canPass;
        this.damageReductions = new ArrayList<>();
        this.disableCooldownScale = disableCooldownScale;
        this.disabledSound = disabledSound;
        this.base = base;
        this.factor = factor;
        this.threshold = threshold;
    }

    public BlocksAttacksComponent(ConfigurationSection cfg) {
        this(
                cfg.isSet("delay_seconds") ? (float) cfg.getDouble("delay_seconds") : null,
                NBTSound.form(cfg.getConfigurationSection("block_sound")),
                TerraNamespaceKey.from(cfg.getString("pass")),
                cfg.isSet("cooldown_scale") ? (float) cfg.getDouble("cooldown_scale") : null,
                NBTSound.form(cfg.getConfigurationSection("disabled_sound")),
                (float) cfg.getDouble("base"),
                (float) cfg.getDouble("factor"),
                (float) cfg.getDouble("threshold")
        );
    }

    @Override
    public void apply(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                ReadWriteNBT component = nbt.getOrCreateCompound(COMPONENT_KEY).getOrCreateCompound(MINECRAFT_PREFIX + "blocks_attacks");
                if (blockDelaySeconds != null) component.setFloat("block_delay_seconds", blockDelaySeconds);
                ReadWriteNBT bsCompound;
                if (blockSound != null) {
                    bsCompound = component.getOrCreateCompound("block_sound");
                    bsCompound.setFloat("range", blockSound.getRange());
                    bsCompound.setString("sound_id", blockSound.getId());
                }
                if (canPass != null) component.setString("bypass_by", "#" + canPass.get());
                if (!damageReductions.isEmpty()) {
                    ReadWriteNBTCompoundList drCompoundList = component.getCompoundList("damage_reductions");
                    for (DamageReduction dr : damageReductions) {
                        bsCompound = drCompoundList.addCompound();
                        bsCompound.setFloat("base", dr.base);
                        bsCompound.setFloat("factor", dr.factor);
                        if (dr.horizontalBlockingAngle != null) bsCompound.setFloat("horizontal_blocking_angle", dr.horizontalBlockingAngle);

                        if (dr.types != null) {
                            for (TerraNamespaceKey tag : dr.types) bsCompound.getStringList("type").add("#" + tag.get());
                        }
                    }
                }

                if (disableCooldownScale != null) component.setFloat("disable_cooldown_scale", disableCooldownScale);

                if (disabledSound != null) {
                    bsCompound = component.getOrCreateCompound("disabled_sound");
                    bsCompound.setFloat("range", disabledSound.getRange());
                    bsCompound.setString("sound_id", disabledSound.getId());
                }

                bsCompound = component.getOrCreateCompound("item_damage");
                bsCompound.setFloat("base", base);
                bsCompound.setFloat("factor", factor);
                bsCompound.setFloat("threshold", threshold);
            });
        } else TerraCraftLogger.warning("Blocks attacks component is only supported in Minecraft 1.21.5 or newer versions");
    }

    public static void clear(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.getOrCreateCompound(COMPONENT_KEY).removeKey(MINECRAFT_PREFIX + "blocks_attacks");
            });
        }
    }

    public static void remove(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.getOrCreateCompound(COMPONENT_KEY).removeKey(MINECRAFT_PREFIX + "blocks_attacks");
                nbt.getOrCreateCompound(COMPONENT_KEY).getOrCreateCompound("!" + MINECRAFT_PREFIX + "blocks_attacks");
            });
        }
    }

    public void addDamageReduction(float base, float factor, @Nullable Float horizontalBlockingAngle, @Nullable TerraNamespaceKey[] types) {
        damageReductions.add(new DamageReduction(base, factor, horizontalBlockingAngle, types));
    }

    /**
     * @param base                    固定阻挡的伤害
     * @param factor                  应被阻挡的伤害比例
     * @param horizontalBlockingAngle （值>0，角度制，默认为90）在水平方向上，以当前玩家视角的水平分量向量为基准，如果受伤害方向与基准方向夹角小于此角度则伤害可被阻挡，否则不能阻挡。任何无来源伤害均被视为需要180度才能阻挡。
     * @param types                   可阻挡的伤害类型列表
     */
    private record DamageReduction(float base, float factor, @Nullable Float horizontalBlockingAngle, @Nullable TerraNamespaceKey[] types) {

    }
}
