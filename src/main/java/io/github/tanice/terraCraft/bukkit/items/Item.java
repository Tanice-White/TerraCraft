package io.github.tanice.terraCraft.bukkit.items;

import io.github.tanice.terraCraft.api.attribute.AttributeActiveSection;
import io.github.tanice.terraCraft.api.attribute.DamageFromType;
import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.api.buffs.TerraBaseBuff;
import io.github.tanice.terraCraft.api.items.TerraItem;
import io.github.tanice.terraCraft.api.items.gems.TerraGemHolder;
import io.github.tanice.terraCraft.api.items.levels.TerraLeveled;
import io.github.tanice.terraCraft.api.items.qualities.TerraQualitative;
import io.github.tanice.terraCraft.api.skills.TerraSkillHolder;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.bukkit.utils.annotation.NonnullByDefault;
import io.github.tanice.terraCraft.bukkit.utils.pdc.PDCAPI;
import io.github.tanice.terraCraft.core.attribute.CalculableMeta;
import io.github.tanice.terraCraft.core.items.AbstractItem;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;
import io.github.tanice.terraCraft.core.utils.EnumUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static io.github.tanice.terraCraft.core.constants.ConfigKeys.*;

/**
 * 插件中的武器、护甲
 */
@NonnullByDefault
public class Item extends AbstractItem implements TerraItem, TerraQualitative, TerraLeveled, TerraSkillHolder, TerraGemHolder {
    /** 技能组名 */
    private final List<String> skills;
    /** 可选品质组组名 */
    private final String qualityGroupName;
    /** 等级模板名 */
    private final String levelTemplateName;
    /** 是否取消伤害 */
    private final boolean cancelDamage;
    /** 耐久消失是否销毁物品 */
    private final boolean loseWhenBreak;
    /** 宝石槽数量 */
    private final Integer gemStackNumber;

    /** 所属套装 */
    private final String setName;
    /** 物品伤害类型 */
    private final DamageFromType damageType;
    /** 生效槽位 */
    private final String slot;
    /** 具体信息 */
    private final TerraCalculableMeta meta;

    /** 持有自带buff */
    private final List<TerraBaseBuff> holdBuffs;
    /** 攻击生效buff 0-给自己  1-给对方 */
    private final List<List<TerraBaseBuff>> attackBuffs;
    /** 受击生效buff 0-给自己  1-给对方 */
    private final List<List<TerraBaseBuff>> defenseBuffs;

    /**
     * 依据内部名称和对应的config文件创建mc基础物品
     */
    public Item(String innerName, ConfigurationSection cfg, AttributeActiveSection aas) {
        super(innerName, cfg);
        this.skills = cfg.getStringList(SKILLS);
        this.qualityGroupName = cfg.getString(QUALITY_GROUPS, "");
        this.cancelDamage = cfg.getBoolean(CANCEL_DAMAGE, false);
        this.loseWhenBreak = cfg.getBoolean(LOSE_WHEN_BREAK, false);
        this.levelTemplateName = cfg.getString(LEVEL_TEMPLATE_NAME, "");
        this.gemStackNumber = cfg.getInt(GEM_STACK_NUMBER, 0);
        this.setName = cfg.getString(EQUIPMENT_SET, "");
        this.damageType = EnumUtil.safeValueOf(DamageFromType.class, cfg.getString(DAMAGE_TYPE), DamageFromType.OTHER);
        this.slot = cfg.getString(SLOT, "any");
        this.meta = new CalculableMeta(cfg.getConfigurationSection(ATTRIBUTE_SECTION), aas);
        this.holdBuffs = initBuffList(HOLD_BUFF, cfg).get(1);
        this.attackBuffs = initBuffList(ATTACK_BUFF, cfg);
        this.defenseBuffs = initBuffList(DEFENCE_BUFF, cfg);
        // TODO 绑定原版属性
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
