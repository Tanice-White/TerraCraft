package io.github.tanice.terraCraft.bukkit.item.component;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.nbtapi.iface.ReadWriteNBTList;
import de.tr7zw.nbtapi.iface.ReadableNBT;
import io.github.tanice.terraCraft.api.item.TerraBaseItem;
import io.github.tanice.terraCraft.api.item.component.ComponentState;
import io.github.tanice.terraCraft.api.item.component.TerraBuffComponent;
import io.github.tanice.terraCraft.api.item.component.AbstractItemComponent;
import io.github.tanice.terraCraft.bukkit.util.nbtapi.NBTBuff;
import io.github.tanice.terraCraft.bukkit.util.version.MinecraftVersions;
import io.github.tanice.terraCraft.bukkit.util.version.ServerVersion;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

import static io.github.tanice.terraCraft.api.command.TerraCommand.*;

public class BuffComponent extends AbstractItemComponent implements TerraBuffComponent {
    @Nullable
    private List<NBTBuff> hold;
    @Nullable
    private List<NBTBuff> attackSelf;
    @Nullable
    private List<NBTBuff> attack;
    @Nullable
    private List<NBTBuff> defenseSelf;
    @Nullable
    private List<NBTBuff> defense;

    public BuffComponent(@Nullable List<String> hold, @Nullable List<String> attackSelf, @Nullable List<String> attack, @Nullable List<String> defenseSelf, @Nullable List<String> defense, boolean updatable) {
        super(updatable);
        if (hold != null) this.hold = hold.stream().map(NBTBuff::new).toList();
        if (attackSelf != null) this.attackSelf = attackSelf.stream().map(NBTBuff::new).toList();
        if (attack != null) this.attack = attack.stream().map(NBTBuff::new).toList();
        if (defenseSelf != null) this.defenseSelf = defenseSelf.stream().map(NBTBuff::new).toList();
        if (defense != null) this.defense = defense.stream().map(NBTBuff::new).toList();
    }

    public BuffComponent(@Nullable List<String> hold, @Nullable List<String> attackSelf, @Nullable List<String> attack, @Nullable List<String> defenseSelf, @Nullable List<String> defense, ComponentState state) {
        super(state);
        if (hold != null) this.hold = hold.stream().map(NBTBuff::new).toList();
        if (attackSelf != null) this.attackSelf = attackSelf.stream().map(NBTBuff::new).toList();
        if (attack != null) this.attack = attack.stream().map(NBTBuff::new).toList();
        if (defenseSelf != null) this.defenseSelf = defenseSelf.stream().map(NBTBuff::new).toList();
        if (defense != null) this.defense = defense.stream().map(NBTBuff::new).toList();
    }

    public BuffComponent(ConfigurationSection cfg) {
        super(cfg.getBoolean("updatable", true));
        List<String> vs;
        vs = cfg.getStringList("hold");
        if (!vs.isEmpty()) this.hold = vs.stream().map(NBTBuff::new).toList();
        vs = cfg.getStringList("attack_self");
        if (!vs.isEmpty()) this.attackSelf = vs.stream().map(NBTBuff::new).toList();
        vs = cfg.getStringList("attack");
        if (!vs.isEmpty()) this.attack = vs.stream().map(NBTBuff::new).toList();
        vs = cfg.getStringList("defense_self");
        if (!vs.isEmpty()) this.defenseSelf = vs.stream().map(NBTBuff::new).toList();
        vs = cfg.getStringList("defense");
        if (!vs.isEmpty()) this.defense = vs.stream().map(NBTBuff::new).toList();
    }

    @Nullable
    public static BuffComponent from(ItemStack item) {
        if (item == null || item.isEmpty()) return null;
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            return NBT.getComponents(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + ".buff");
                if (data == null) return null;
                return formNBT(data);
            });
        } else {
            return NBT.get(item, nbt -> {
                ReadableNBT data = nbt.resolveCompound(TERRA_COMPONENT_KEY + ".buff");
                if (data == null) return null;
                return formNBT(data);
            });
        }
    }

    @Override
    public void doApply(ItemStack item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item, nbt -> {
                ReadWriteNBT data = nbt.resolveOrCreateCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY + "." + "buff");
                addToCompound(data);
            });
        } else {
            NBT.modify(item, nbt -> {
                ReadWriteNBT data = nbt.resolveOrCreateCompound(TERRA_COMPONENT_KEY + ".buff");
                addToCompound(data);
            });
        }
    }

    @Override
    public void updateLore() {

    }

    public static void clear(TerraBaseItem item) {
        if (ServerVersion.isAfterOrEq(MinecraftVersions.v1_20_5)) {
            NBT.modifyComponents(item.getBukkitItem(), nbt -> {
                nbt.resolveOrCreateCompound(MINECRAFT_PREFIX + "custom_data." + TERRA_COMPONENT_KEY).removeKey("buff");
            });
        } else {
            NBT.modify(item.getBukkitItem(), nbt -> {
                nbt.resolveOrCreateCompound(TERRA_COMPONENT_KEY).removeKey("buff");
            });
        }
    }

    public static void remove(TerraBaseItem item) {
        clear(item);
    }

    @Override
    public List<NBTBuff> getHold() {
        return this.hold == null ? List.of() : this.hold;
    }

    @Override
    public void setHold(@Nullable List<String> buffs) {
        if (buffs == null || buffs.isEmpty()) return;
        this.hold = buffs.stream().map(NBTBuff::new).toList();
    }

    @Override
    public List<NBTBuff> getAttackSelf() {
        return this.attackSelf == null ? List.of() : this.attackSelf;
    }

    @Override
    public void setAttackSelf(@Nullable List<String> buffs) {
        if (buffs == null || buffs.isEmpty()) return;
        this.attackSelf = buffs.stream().map(NBTBuff::new).toList();
    }

    @Override
    public List<NBTBuff> getAttack() {
        return this.attack == null ? List.of() : this.attack;
    }

    @Override
    public void setAttack(@Nullable List<String> buffs) {
        if (buffs == null || buffs.isEmpty()) return;
        this.attack = buffs.stream().map(NBTBuff::new).toList();
    }

    @Override
    public List<NBTBuff> getDefenseSelf() {
        return this.defenseSelf == null ? List.of() : this.defenseSelf;
    }

    @Override
    public void setDefenseSelf(@Nullable List<String> buffs) {
        if (buffs == null || buffs.isEmpty()) return;
        this.defenseSelf = buffs.stream().map(NBTBuff::new).toList();
    }

    @Override
    public List<NBTBuff> getDefense() {
        return this.defense == null ? List.of() : this.defense;
    }

    @Override
    public void setDefense(@Nullable List<String> buffs) {
        if (buffs == null || buffs.isEmpty()) return;
        this.defense = buffs.stream().map(NBTBuff::new).toList();
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
            list.addAll(hold.stream().map(NBTBuff::toString).toList());
        }
        if (attackSelf != null && !attackSelf.isEmpty()) {
            list = component.getStringList("attack_self");
            list.clear();
            list.addAll(attackSelf.stream().map(NBTBuff::toString).toList());
        }
        if (attack != null && !attack.isEmpty()) {
            list = component.getStringList("attack");
            list.clear();
            list.addAll(attack.stream().map(NBTBuff::toString).toList());
        }
        if (defenseSelf != null && !defenseSelf.isEmpty()) {
            list = component.getStringList("defense_self");
            list.clear();
            list.addAll(defenseSelf.stream().map(NBTBuff::toString).toList());
        }
        if (defense != null && !defense.isEmpty()) {
            list = component.getStringList("defense");
            list.clear();
            list.addAll(defense.stream().map(NBTBuff::toString).toList());
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

    @Override
    public String getComponentName() {
        return "buff";
    }

    @Override
    public String toString() {
        return BOLD + YELLOW + "buff:" + "\n" +
                AQUA + "    " + "hold:" +
                buffListToString(hold) + "\n" +
                AQUA + "    " + "attack_self:" +
                buffListToString(attackSelf) + "\n" +
                AQUA + "    " + "attack:" +
                buffListToString(attack) + "\n" +
                AQUA + "    " + "defense_self:" +
                buffListToString(defenseSelf) + "\n" +
                AQUA + "    " + "defense:" +
                buffListToString(defense) + "\n" +
                AQUA + "    " + "state:" + WHITE + state + RESET;
    }

    private String buffListToString(List<NBTBuff> buffs) {
        StringBuilder sb = new StringBuilder();
        sb.append(RESET);
        if (buffs != null && !buffs.isEmpty()) {
            sb.append(WHITE);
            for (int i = 0; i < buffs.size(); i++) {
                sb.append(buffs.get(i));
                if (i < buffs.size() - 1) sb.append(", ");
            }
        }
        return sb.toString();
    }
}
