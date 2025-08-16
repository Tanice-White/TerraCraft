package io.github.tanice.terraCraft.bukkit.items;

import io.github.tanice.terraCraft.api.attribute.AttributeActiveSection;
import io.github.tanice.terraCraft.api.attribute.DamageFromType;
import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.api.buffs.TerraBaseBuff;
import io.github.tanice.terraCraft.api.items.TerraItem;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.utils.annotation.NonnullByDefault;
import io.github.tanice.terraCraft.core.items.AbstractItem;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

@NonnullByDefault
public class Item extends AbstractItem implements TerraItem {

    /**
     * 依据内部名称和对应的config文件创建mc基础物品
     */
    public Item(String innerName, ConfigurationSection cfg, AttributeActiveSection aas) {
        super(innerName, cfg);
    }

    @Override
    public List<String> selfUpdate(ItemStack old) {
        ItemMeta oldMeta = old.getItemMeta();
        if (oldMeta == null) return List.of();

        /* 等级限制不更新 - 在计算和显示的时候只显示最高等级即可 */
        /* 宝石更新 */
        String[] gems = PDCAPI.getGems(oldMeta);
        if (gems != null) {
            int oldNum = gems.length;
            if (oldNum > gemStackNumber) {
                // 截取前gemStackNumber个元素写入
                String[] keptGems = new String[gemStackNumber];
                System.arraycopy(gems, 0, keptGems, 0, gemStackNumber);
                PDCAPI.setGems(oldMeta, keptGems);
                return new ArrayList<>(Arrays.asList(gems).subList(gemStackNumber, oldNum));
            }
        }
        /* TODO 原版属性更新 */

        return super.selfUpdate(old);
    }

    @Override
    public List<String> getSkillNames() {
        return new ArrayList<>(this.skills);
    }

    @Override
    public boolean isCancelDamage() {
        return this.cancelDamage;
    }

    @Override
    public boolean isLoseWhenBreak() {
        return this.loseWhenBreak;
    }

    @Override
    public int getGemStackNumber() {
        return this.gemStackNumber;
    }

    @Override
    public String getSetName() {
        return this.setName;
    }

    @Override
    public DamageFromType getDamageType() {
        return this.damageType;
    }

    @Override
    public String getSlotAsString() {
        return this.slot;
    }

    @Override
    public TerraCalculableMeta copyMeta() {
        return this.meta.clone();
    }

    @Override
    public List<TerraBaseBuff> getHoldBuffs() {
        return this.holdBuffs;
    }

    @Override
    public List<TerraBaseBuff> getAttackBuffs() {
        List<TerraBaseBuff> combined = new ArrayList<>();
        combined.addAll(this.attackBuffs.get(0));
        combined.addAll(this.attackBuffs.get(1));
        return combined;
    }

    @Override
    public List<TerraBaseBuff> getDefenseBuffs() {
        List<TerraBaseBuff> combined = new ArrayList<>();
        combined.addAll(this.defenseBuffs.get(0));
        combined.addAll(this.defenseBuffs.get(1));
        return combined;
    }

    @Override
    public List<TerraBaseBuff> getAttackBuffsForSelf() {
        return new ArrayList<>(this.attackBuffs.getFirst());
    }

    @Override
    public List<TerraBaseBuff> getAttackBuffsForOther() {
        return new ArrayList<>(this.attackBuffs.get(1));
    }

    @Override
    public List<TerraBaseBuff> getDefenseBuffsForSelf() {
        return new ArrayList<>(this.defenseBuffs.getFirst());
    }

    @Override
    public List<TerraBaseBuff> getDefenseBuffsForOther() {
        return new ArrayList<>(this.defenseBuffs.get(1));
    }

    @Override
    public String getQualityGroupName() {
        return this.qualityGroupName;
    }

    @Override
    public String getLevelTemplateName() {
        return this.levelTemplateName;
    }

    /**
     * 初始化 attack_buff 和 defence_buff 以及 hold_buff
     */
    private List<List<TerraBaseBuff>> initBuffList(String sectionKey, ConfigurationSection cfg) {
        List<List<TerraBaseBuff>> res = Arrays.asList(new ArrayList<>(), new ArrayList<>());
        List<String> configLines = cfg.getStringList(sectionKey);

        for (String line : configLines) {
            String[] tokens = line.trim().split("\\s+");

            if (tokens.length < 1 || tokens.length > 4) {
                TerraCraftLogger.error("Error in " + sectionKey + " description for consumable: " + name + ": Invalid format in line " + line);
                continue;
            }
            String buffName = tokens[0];
            Optional<TerraBaseBuff> buffOption = TerraCraftBukkit.inst().getBuffManager().getBuff(buffName);
            if (buffOption.isEmpty()) {
                TerraCraftLogger.error("Buff" + buffName + " not found in " + sectionKey + " for consumable: " + name);
                continue;
            }

            int duration;
            double chance;
            boolean isSelf = false;
            TerraBaseBuff buff = buffOption.get();
            for (int i = 1; i < tokens.length; i++) {
                String token = tokens[i].toLowerCase();

                if (token.startsWith("#")) {
                    duration = Integer.parseInt(token.substring(1));
                    if (duration > 0) buff.setDuration(duration);

                } else if (token.startsWith("%")) {
                    chance = Double.parseDouble(token.substring(1));
                    if (chance > 0 && chance <= 100) buff.setChance(chance);

                } else if (token.equals("@self") || token.equals("@other")) isSelf = token.equals("@self");
            }
            res.get(isSelf ? 0 : 1).add(buff);
        }
        return res;
    }
}
