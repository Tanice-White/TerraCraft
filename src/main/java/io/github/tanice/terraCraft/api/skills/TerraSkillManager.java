package io.github.tanice.terraCraft.api.skills;

import io.github.tanice.terraCraft.core.skills.SkillRowData;
import org.bukkit.entity.Player;

public interface TerraSkillManager {

    /**
     * 释放技能(通过监听玩家事件)
     */
    void castSkill(Player player, Trigger trigger);

    /**
     * 手动设置技能冷却
     * @param nextAvailableTime 毫秒
     */
    void setSkillCooldown(Player player, SkillRowData skill, long nextAvailableTime);

    /**
     * 检查技能是否就绪 (只读)
     */
    boolean isSkillCooldownReady(Player player, String skillName, long currentTime);

    /**
     * 获取技能剩余冷却时间 (只读)
     */
    long getSkillRemainingCooldown(Player player, String skillName, long currentTime);

    /**
     * 提交玩家技能更新
     */
    void updatePlayerSkills(Player player);


    enum Trigger {
        CROUCH_DOWN,
        CROUCH_UP,

        LEFT_CLICK,
        RIGHT_CLICK,

        CROUCH_LEFT,
        CROUCH_RIGHT,

        JUMP_LEFT,
        JUMP_RIGHT,

        SHOOT
    }
}
