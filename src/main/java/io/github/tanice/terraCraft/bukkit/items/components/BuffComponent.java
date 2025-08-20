package io.github.tanice.terraCraft.bukkit.items.components;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBTList;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.ComponentState;
import io.github.tanice.terraCraft.api.items.components.TerraBuffComponent;
import io.github.tanice.terraCraft.api.items.components.AbstractItemComponent;
import io.github.tanice.terraCraft.bukkit.utils.StringUtil;
import io.github.tanice.terraCraft.bukkit.utils.versions.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.utils.versions.ServerVersion;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class BuffComponent extends AbstractItemComponent implements TerraBuffComponent {
    @Nullable
    private List<String> hold;
    @Nullable
    private List<String> attackSelf;
    @Nullable
    private List<String> attack;
    @Nullable
    private List<String> defenseSelf;
    @Nullable
    private List<String> defense;

    public BuffComponent(@Nullable List<String> hold, @Nullable List<String> attackSelf, @Nullable List<String> attack, @Nullable List<String> defenseSelf, @Nullable List<String> defense, boolean updatable) {
        super(updatable);
        this.hold = hold;
        this.attackSelf = attackSelf;
        this.attack = attack;
        this.defenseSelf = defenseSelf;
        this.defense = defense;
    }

    public BuffComponent(@Nullable List<String> hold, @Nullable List<String> attackSelf, @Nullable List<String> attack, @Nullable List<String> defenseSelf, @Nullable List<String> defense, ComponentState state) {
        super(state);
        this.hold = hold;
        this.attackSelf = attackSelf;
        this.attack = attack;
        this.defenseSelf = defenseSelf;
        this.defense = defense;
    }

    public BuffComponent(ConfigurationSection cfg) {
        super(cfg.getBoolean("updatable", true));
        this.hold = StringUtil.splitByComma(cfg.getString("hold"));
        this.attackSelf = StringUtil.splitByComma(cfg.getString("attack_self"));
        this.attack = StringUtil.splitByComma(cfg.getString("attack"));
        this.defenseSelf = StringUtil.splitByComma(cfg.getString("defense_self"));
        this.defense = StringUtil.splitByComma(cfg.getString("defense"));
    }

    @Nullable
    public static BuffComponent from(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            return NBT.getComponents(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(COMPONENT_KEY + "." + MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".buffs");
                if (data == null) return null;
                return formNBT(data);
            });
        } else {
            return NBT.get(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(TAG_KEY + "." + TERRA_COMPONENT_KEY + ".buffs");
                if (data == null) return null;
                return formNBT(data);
            });
        }
    }

    @Override
    public void doApply(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt -> {
                ReadWriteNBT data = nbt.resolveOrCreateCompound(COMPONENT_KEY + "." + MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + "." + "buffs");
                addToCompound(data);
            });
        } else {
            NBT.modify(item, nbt -> {
                ReadWriteNBT data = nbt.resolveOrCreateCompound(TAG_KEY + "." + TERRA_COMPONENT_KEY + ".buffs");
                addToCompound(data);
            });
        }
    }

    @Override
    public void callEvent() {

    }

    @Override
    public void updateLore() {

    }

    public static void clear(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.resolveOrCreateCompound(COMPONENT_KEY + "." + MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY).removeKey("buffs");
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
                nbt.resolveOrCreateCompound(TAG_KEY + "." + TERRA_COMPONENT_KEY).removeKey("buffs");
            });
        }
    }

    public static void remove(TerraBaseItem item) {
        clear(item);
    }

    @Override
    public List<String> getHold() {
        return this.hold == null ? List.of() : this.hold;
    }

    @Override
    public void setHold(@Nullable List<String> buffs) {
        this.hold = buffs;
    }

    @Override
    public List<String> getAttackSelf() {
        return this.attackSelf == null ? List.of() : this.attackSelf;
    }

    @Override
    public void setAttackSelf(@Nullable List<String> buffs) {
        this.attackSelf = buffs;
    }

    @Override
    public List<String> getAttack() {
        return this.attack == null ? List.of() : this.attack;
    }

    @Override
    public void setAttack(@Nullable List<String> buffs) {
        this.attack = buffs;
    }

    @Override
    public List<String> getDefenseSelf() {
        return this.defenseSelf == null ? List.of() : this.defenseSelf;
    }

    @Override
    public void setDefenseSelf(@Nullable List<String> buffs) {
        this.defenseSelf = buffs;
    }

    @Override
    public List<String> getDefense() {
        return this.defense == null ? List.of() : this.defense;
    }

    @Override
    public void setDefense(@Nullable List<String> buffs) {
        this.defense = buffs;
    }

    @Override
    public int hashCode() {
        return Objects.hash(hold, attackSelf, attack, defenseSelf, defense);
    }

    private void addToCompound(ReadWriteNBT component) {
        ReadWriteNBTList<String> list;
        if (hold != null && !hold.isEmpty()) {
            list = component.getStringList("hold");
            list.clear();
            list.addAll(hold);
        }
        if (attackSelf != null && !attackSelf.isEmpty()) {
            list = component.getStringList("attack_self");
            list.clear();
            list.addAll(attackSelf);
        }
        if (attack != null && !attack.isEmpty()) {
            list = component.getStringList("attack");
            list.clear();
            list.addAll(attack);
        }
        if (defenseSelf != null && !defenseSelf.isEmpty()) {
            list = component.getStringList("defense_self");
            list.clear();
            list.addAll(defenseSelf);
        }
        if (defense != null && !defense.isEmpty()) {
            list = component.getStringList("defense");
            list.clear();
            list.addAll(defense);
        }
        component.setByte("state", state.toNbtByte());
    }

    private static BuffComponent formNBT(ReadableNBT nbt) {
        return new BuffComponent(
                nbt.getStringList("hold").toListCopy(),
                nbt.getStringList("attack_self").toListCopy(),
                nbt.getStringList("attack").toListCopy(),
                nbt.getStringList("defense_self").toListCopy(),
                nbt.getStringList("defense").toListCopy(),
                new ComponentState(nbt.getByte("state"))
        );
    }
}
