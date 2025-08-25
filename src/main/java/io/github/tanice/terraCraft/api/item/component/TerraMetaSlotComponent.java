package io.github.tanice.terraCraft.api.item.component;

import io.github.tanice.terraCraft.core.util.slot.TerraEquipmentSlot;

public interface TerraMetaSlotComponent extends TerraBaseComponent {
    /**
     * 判断拥有本组件的物品的Meta在目标槽位是否生效
     * @param conditionSlot 目标槽位
     * @return meta是否有效
     */
    boolean activeUnder(TerraEquipmentSlot conditionSlot);

    TerraEquipmentSlot getSlot();

    void setSlot(TerraEquipmentSlot slot);
}
