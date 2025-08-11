package io.github.tanice.terraCraft.bukkit.utils.adapter;

// TODO 支持more
public enum BukkitDamageTags {
    // 无视盔甲
    BYPASSES_ARMOR("bypasses_armor"),
    // 无视受击冷却
    BYPASSES_COOLDOWN("bypasses_cooldown"),
    // 无视抗性效果
    BYPASSES_EFFECTS("bypasses_effects"),
    // 无视附魔保护
    BYPASSES_ENCHANTMENTS("bypasses_enchantments"),
    // 无视伤害免疫
    BYPASSES_INVULNERABILITY("bypasses_invulnerability"),
    // 无视所有伤害减免
    BYPASSES_RESISTANCE("bypasses_resistance"),
    // 无视盾牌
    BYPASSES_SHIELD("bypasses_shield"),
    // 无视狼铠
    BYPASSES_WOLF_ARMOR("bypasses_wolf_armor"),
    // 破坏盔甲架
    CAN_BREAK_ARMOR_STAND("can_break_armor_stand"),
    // 头盔损伤
    DAMAGES_HELMET("damages_helmet"),
    // 爆炸伤害
    IS_EXPLOSION("is_explosion"),
    // 玩家攻击
    IS_PLAYER_ATTACK("is_player_attack"),
    // 投射物伤害
    IS_PROJECTILE("is_projectile"),
    // 不引发反击
    NO_ANGER("no_anger"),
    // 无击退
    NO_KNOCKBACK("no_knockback");

    private final String value;

    BukkitDamageTags(String v) {
        this.value = v;
    }

    public String getValue() {
        return this.value;
    }
}
