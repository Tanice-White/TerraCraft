package io.github.tanice.terraCraft.bukkit.item.component.vanilla;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.github.tanice.terraCraft.api.item.component.vanilla.TerraEquippableComponent;
import io.github.tanice.terraCraft.bukkit.util.nbtapi.NBTSound;
import io.github.tanice.terraCraft.core.util.slot.TerraEquipmentSlot;
import io.github.tanice.terraCraft.bukkit.util.version.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.util.version.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.util.namespace.TerraNamespaceKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

import static io.github.tanice.terraCraft.core.util.EnumUtil.safeValueOf;

public class EquippableComponent implements TerraEquippableComponent {
    @Nullable
    private final List<TerraNamespaceKey> allowedEntities;
    @Nullable
    private final TerraNamespaceKey assetId;
    @Nullable
    private final TerraNamespaceKey cameraOverlay;
    @Nullable
    private final Boolean canBeSheared; /* 1.21.6 */
    @Nullable
    private final Boolean damageOnHurt;
    @Nullable
    private final Boolean equipOnInteract; /* 1.21.5 */
    @Nullable
    private final NBTSound equipSound;
    @Nullable
    private final Boolean dispensable;
    @Nullable
    private final NBTSound shearingSound; /* 1.21.6 */

    private final TerraEquipmentSlot slot;
    @Nullable
    private final Boolean swappable;

    public EquippableComponent(@Nullable List<TerraNamespaceKey> allowedEntities, @Nullable TerraNamespaceKey assetId, @Nullable TerraNamespaceKey cameraOverlay, @Nullable Boolean canBeSheared, @Nullable Boolean damageOnHurt, @Nullable Boolean equipOnInteract, @Nullable NBTSound equipSound, @Nullable Boolean dispensable, @Nullable NBTSound shearingSound, TerraEquipmentSlot slot, @Nullable Boolean swappable) {
        this.allowedEntities = allowedEntities;
        this.assetId = assetId;
        this.cameraOverlay = cameraOverlay;
        this.canBeSheared = canBeSheared;
        this.damageOnHurt = damageOnHurt;
        this.equipOnInteract = equipOnInteract;
        this.equipSound = equipSound;
        this.dispensable = dispensable;
        this.shearingSound = shearingSound;
        this.slot = slot;
        this.swappable = swappable;
    }

    public EquippableComponent(ConfigurationSection cfg) {
        this(
                cfg.isSet("allow") ? cfg.getStringList("allow").stream().map(TerraNamespaceKey::from).filter(Objects::nonNull).toList() : null,
                cfg.isSet("asset") ? TerraNamespaceKey.from(cfg.getString("asset")) : null,
                cfg.isSet("camera") ? TerraNamespaceKey.from(cfg.getString("camera")) : null,
                cfg.isSet("can_shear") ? cfg.getBoolean("can_shear") :null,
                cfg.isSet("damage_on_hurt") ? cfg.getBoolean("damage_on_hurt") :null,
                cfg.isSet("equip_on_interact") ? cfg.getBoolean("equip_on_interact") :null,
                NBTSound.form(cfg.getConfigurationSection("equip_sound")),
                cfg.isSet("dispensable") ? cfg.getBoolean("dispensable") :null,
                NBTSound.form(cfg.getConfigurationSection("shearing_sound")),
                safeValueOf(TerraEquipmentSlot.class, cfg.getString("slot"), TerraEquipmentSlot.SADDLE),
                cfg.isSet("swappable") ? cfg.getBoolean("swappable") :null
        );
    }

    @Override
    public void cover(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
            NBT.modifyComponents(item, nbt -> {
                ReadWriteNBT component = nbt.getOrCreateCompound(MINECRAFT_PREFIX + "equippable");

                if (allowedEntities != null && !allowedEntities.isEmpty()) {
                    component.getStringList("allowed_entities").clear();
                    component.getStringList("allowed_entities").addAll(allowedEntities.stream().map(TerraNamespaceKey::get).toList());
                }
                if (assetId != null) {
                    if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_4)) component.setString("asset_id", assetId.get());
                    else component.setString("model", assetId.get());
                }
                if (cameraOverlay != null) component.setString("camera_overlay", cameraOverlay.get());
                if (damageOnHurt != null) component.setBoolean("damage_on_hurt", damageOnHurt);
                if (equipOnInteract != null) {
                    if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_5)) component.setBoolean("equip_on_interact", equipOnInteract);
                    else TerraCraftLogger.warning("equip_on_interact in Equippable component is only supported in Minecraft 1.21.5 or newer versions");
                }
                ReadWriteNBT soundComponent;
                if (equipSound != null) {
                    soundComponent = component.getOrCreateCompound("equip_sound");
                    soundComponent.setFloat("range", equipSound.getRange());
                    soundComponent.setString("sound_id", equipSound.getId());
                }
                component.setBoolean("dispensable", dispensable);

                if (canBeSheared != null){
                    if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_6)) component.setBoolean("can_be_sheared", canBeSheared);
                    else TerraCraftLogger.warning("can_be_sheared in Equippable component is only supported in Minecraft 1.21.6 or newer versions");
                }
                if (shearingSound != null) {
                    if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_6)) {
                        soundComponent = component.getOrCreateCompound("shearing_sound");
                        soundComponent.setFloat("range", shearingSound.getRange());
                        soundComponent.setString("sound_id", shearingSound.getId());
                    } else TerraCraftLogger.warning("shearing_sound in Equippable component is only supported in Minecraft 1.21.6 or newer versions");
                }

                String[] slots = slot.getStandardEquippableName();
                if (slots.length > 1)
                    TerraCraftLogger.warning("Slot in EquippableComponent is only supported on a single slot(not group). Use the first slot name" + slots[0] + "as default");
                component.setString("slot", slots[0]);
                component.setBoolean("swappable", swappable);
            });
        } else TerraCraftLogger.warning("Equippable component is only supported in Minecraft 1.21.2 or newer versions");
    }

    @Override
    public String getComponentName() {
        return "equippable";
    }

    public static void clear(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
            NBT.modifyComponents(item, nbt -> {
                nbt.removeKey(MINECRAFT_PREFIX + "equippable");
            });
        }
    }

    public static void remove(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
            NBT.modifyComponents(item, nbt -> {
                nbt.removeKey(MINECRAFT_PREFIX + "equippable");
                nbt.getOrCreateCompound("!" + MINECRAFT_PREFIX + "equippable");
            });
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(allowedEntities, assetId, cameraOverlay, canBeSheared, damageOnHurt, equipOnInteract, equipSound, dispensable, shearingSound, slot, swappable);
    }
}
