package io.github.tanice.terraCraft.api.entitys;

import io.github.tanice.terraCraft.api.items.TerraBaseItem;

import java.util.Optional;

public interface TerraBaseEntity {
    // 装备头部物品
    void equipItemHead(TerraBaseItem item);

    // 装备胸部物品
    void equipItemChest(TerraBaseItem item);

    // 装备腿部物品
    void equipItemLegs(TerraBaseItem item);

    // 装备脚部物品
    void equipItemFeet(TerraBaseItem item);

    // 装备主手物品
    void equipItemMainHand(TerraBaseItem item);

    // 装备副手物品
    void equipItemOffHand(TerraBaseItem item);

    // 获取头部物品
    Optional<TerraBaseItem> getItemHead();

    // 获取胸部物品
    Optional<TerraBaseItem> getItemChest();

    // 获取腿部物品
    Optional<TerraBaseItem> getItemLegs();

    // 获取脚部物品
    Optional<TerraBaseItem> getItemFeet();

    // 获取主手物品
    Optional<TerraBaseItem> getItemMainHand();

    // 获取副手物品
    Optional<TerraBaseItem> getItemOffhand();
}
