package io.github.tanice.terraCraft.bukkit.items.components;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.TerraEquippableComponent;
import io.github.tanice.terraCraft.bukkit.utils.nbtapi.NBTSound;
import io.github.tanice.terraCraft.bukkit.utils.slots.TerraEquipmentSlot;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.utils.namespace.TerraNamespaceKey;

import javax.annotation.Nonnull;
import java.util.List;

public class EquippableComponent implements TerraEquippableComponent {
    private final List<TerraNamespaceKey> allowedEntities;
    private final TerraNamespaceKey assetId;
    private final TerraNamespaceKey cameraOverlay;
    private final Boolean canBeSheared;
    private final Boolean damageOnHurt;
    private final Boolean equipOnInteract;
    private final NBTSound equipSound;
    private final Boolean dispensable;
    private final NBTSound shearingSound;
    @Nonnull
    private final TerraEquipmentSlot slot;
    private final Boolean swappable;

    public EquippableComponent(List<TerraNamespaceKey> allowedEntities, TerraNamespaceKey assetId, TerraNamespaceKey cameraOverlay, Boolean canBeSheared, Boolean damageOnHurt, Boolean equipOnInteract, NBTSound equipSound, Boolean dispensable, NBTSound shearingSound, @Nonnull TerraEquipmentSlot slot, Boolean swappable) {
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
                if (assetId != null) component.setString("asset_id", assetId.get());
                if (cameraOverlay != null) component.setString("camera_overlay", cameraOverlay.get());
                if (canBeSheared != null) component.setBoolean("can_be_sheared", canBeSheared);
                if (damageOnHurt != null) component.setBoolean("damage_on_hurt", damageOnHurt);
                if (equipOnInteract != null) component.setBoolean("equip_on_interact", equipOnInteract);
                ReadWriteNBT soundComponent;
                if (equipSound != null) {
                    soundComponent = component.getOrCreateCompound("equip_sound");
                    soundComponent.setFloat("range", equipSound.getRange());
                    soundComponent.setString("sound_id", equipSound.getId());
                }
                component.setBoolean("dispensable", dispensable);
                if (shearingSound != null) {
                    soundComponent = component.getOrCreateCompound("shearing_sound");
                    soundComponent.setFloat("range", shearingSound.getRange());
                    soundComponent.setString("sound_id", shearingSound.getId());
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
