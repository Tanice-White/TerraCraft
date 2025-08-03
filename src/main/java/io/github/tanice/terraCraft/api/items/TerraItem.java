package io.github.tanice.terraCraft.api.items;

import io.github.tanice.terraCraft.api.attribute.DamageFromType;
import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import io.github.tanice.terraCraft.api.buffs.TerraBaseBuff;

import java.util.List;

public interface TerraItem extends TerraBaseItem {

    boolean isCancelDamage();

    boolean isLoseWhenBreak();

    String getSetName();

    DamageFromType getDamageType();

    String getSlotAsString();

    TerraCalculableMeta copyMeta();

    List<TerraBaseBuff> getHoldBuffs();

    List<TerraBaseBuff> getAttackBuffs();

    List<TerraBaseBuff> getDefenseBuffs();

    List<TerraBaseBuff> getAttackBuffsForSelf();

    List<TerraBaseBuff> getAttackBuffsForOther();

    List<TerraBaseBuff> getDefenseBuffsForSelf();

    List<TerraBaseBuff> getDefenseBuffsForOther();
}
