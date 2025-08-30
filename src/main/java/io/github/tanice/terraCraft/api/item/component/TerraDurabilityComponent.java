package io.github.tanice.terraCraft.api.item.component;

public interface TerraDurabilityComponent extends TerraBaseComponent {

    int getDamage();

    void setDamage(int damage);

    int getMaxDamage();

    void setMaxDamage(int maxDamage);

    boolean isBreakLoss();

    void setBreakLoss(boolean breakLoss);

    String getDamageExpr();

    void setDamageExpr(String damageExpr);

    /**
     * 获取本次使用应该减少的耐久
     * @param damage 造成/承受的伤害 工具+弓+弩的使用默认为0
     * @return 需要减少的耐久值
     */
    int getDamageForUse(double damage);

    boolean broken();
}
