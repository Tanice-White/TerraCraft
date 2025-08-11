package io.github.tanice.terraCraft.bukkit.items.components;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBTCompoundList;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.TerraBlocksAttacks;
import io.github.tanice.terraCraft.bukkit.utils.adapter.BukkitDamageTags;
import io.github.tanice.terraCraft.bukkit.utils.adapter.BukkitSound;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;

import java.util.ArrayList;
import java.util.List;

public class BlocksAttacks implements TerraBlocksAttacks {

    private final float blockDelaySeconds;

    private final BukkitSound blockSound;

    private final BukkitDamageTags canPass;
    private final List<DamageReduction> damageReductions;
    private final float disableCooldownScale;

    private final BukkitSound disabledSound;

    private final float base;
    private final float factor;
    private final float threshold;

    public BlocksAttacks(float blockDelaySeconds, BukkitSound blockSound, BukkitDamageTags canPass, float disableCooldownScale, BukkitSound disabledSound, float base, float factor, float threshold) {
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

    @Override
    public void apply(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                ReadWriteNBT component = nbt.getOrCreateCompound(COMPONENT_KEY).getOrCreateCompound(MINECRAFT_PREFIX + "blocks_attacks");
                component.setFloat("block_delay_seconds", blockDelaySeconds);

                ReadWriteNBT bsCompound = component.getOrCreateCompound("block_sound");
                bsCompound.setFloat("range", blockSound.getRange());
                bsCompound.setString("sound_id", blockSound.getId());

                component.setString("bypass_by", canPass.getValue());

                ReadWriteNBTCompoundList drCompoundList = component.getCompoundList("damage_reductions");
                for (DamageReduction dr : damageReductions) {
                    bsCompound = drCompoundList.addCompound();
                    bsCompound.setFloat("base", dr.base);
                    bsCompound.setFloat("factor", dr.factor);
                    bsCompound.setFloat("horizontal_blocking_angle", dr.horizontalBlockingAngle);

                    if (dr.types != null) {
                        for (BukkitDamageTags tag : dr.types) bsCompound.getStringList("type").add(tag.getValue());
                    }
                }

                component.setFloat("disable_cooldown_scale", disableCooldownScale);

                bsCompound = component.getOrCreateCompound("disabled_sound");
                bsCompound.setFloat("range", disabledSound.getRange());
                bsCompound.setString("sound_id", disabledSound.getId());

                bsCompound = component.getOrCreateCompound("item_damage");
                bsCompound.setFloat("base", base);
                bsCompound.setFloat("factor", factor);
                bsCompound.setFloat("threshold", threshold);
            });
        } else TerraCraftLogger.error("Blocks attacks component is only supported in Minecraft 1.21.5 or newer versions");
    }

    @Override
    public void addDamageReduction(float base, float factor, float horizontalBlockingAngle, BukkitDamageTags... types) {
        damageReductions.add(new DamageReduction(base, factor, horizontalBlockingAngle, types));
    }


    private record DamageReduction(float base, float factor, float horizontalBlockingAngle, BukkitDamageTags... types) {

    }
}
