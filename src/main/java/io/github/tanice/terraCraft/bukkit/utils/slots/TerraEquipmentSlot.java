package io.github.tanice.terraCraft.bukkit.utils.slots;

import java.util.Arrays;
import java.util.Objects;

public enum TerraEquipmentSlot {

    ANY("any", 98, 99, 100, 101, 102, 103, 105, 106),

    MAINHAND("mainhand", 98),
    OFFHAND("offhand", 99),
    HEAD("head", 103),
    CHEST("chest", 102),
    LEGS("legs", 101),
    FEET("feet", 100),

    HAND("hand", 98, 99),
    ARMOR("armor", 103, 102, 101, 100),

    BODY("body", 105),
    SADDLE("saddle", 106);

    // 槽位属性
    private final String standardName;
    private final int[] slotNumber;

    TerraEquipmentSlot(String standardName, int... slotNumber) {
        this.standardName = standardName;
        this.slotNumber = slotNumber;
    }

    /**
     * 根据槽位编号查找对应的枚举值
     * @param number 槽位编号
     * @return 对应的TerraEquipmentSlot，如果未找到则返回null
     */
    public static TerraEquipmentSlot of(int... number) {
        for (TerraEquipmentSlot slot : values()) {
            if (Arrays.equals(slot.slotNumber, number)) return slot;
        }
        return null;
    }

    /**
     * 根据槽位名称查找对应的枚举值
     * @param slotName 槽位名称
     * @return 对应的TerraEquipmentSlot，如果未找到则返回null
     */
    public static TerraEquipmentSlot of(String slotName) {
        for (TerraEquipmentSlot slot : values()) {
            if (slot.standardName.equalsIgnoreCase(slotName)) return slot;
        }
        return null;
    }

    public String[] getStandardEquippableName() {
        if (Objects.equals(this.standardName, HAND.getStandardName())) return new String[]{MAINHAND.standardName, OFFHAND.standardName};

        else if (Objects.equals(this.standardName, ARMOR.getStandardName())) return new String[] {
                HEAD.standardName, CHEST.standardName, LEGS.standardName, FEET.standardName
        };
        else if (Objects.equals(this.standardName, ANY.getStandardName())) return new String[] {
                MAINHAND.standardName, OFFHAND.standardName, HEAD.standardName, CHEST.standardName, LEGS.standardName, FEET.standardName, BODY.standardName, SADDLE.standardName
        };
        else return new String[]{standardName};
    }

    public String getStandardName() {
        return this.standardName;
    }

    public int[] getSlotNumber() {
        return this.slotNumber;
    }
}