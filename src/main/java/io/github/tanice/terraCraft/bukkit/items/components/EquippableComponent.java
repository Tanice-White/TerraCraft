package io.github.tanice.terraCraft.bukkit.items.components;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.vanilla.TerraEquippableComponent;
import io.github.tanice.terraCraft.bukkit.utils.nbtapi.NBTSound;
import io.github.tanice.terraCraft.core.utils.slots.TerraEquipmentSlot;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.utils.namespace.TerraNamespaceKey;

import javax.annotation.Nullable;
import java.util.List;

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

    @Override
    public void apply(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_21_2)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                ReadWriteNBT component = nbt.getOrCreateCompound(COMPONENT_KEY).getOrCreateCompound(MINECRAFT_PREFIX + "equippable");

                if (allowedEntities != null && !allowedEntities.isEmpty())
                    component.getStringList("allowed_entities").addAll(allowedEntities.stream().map(TerraNamespaceKey::get).toList());
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
}
