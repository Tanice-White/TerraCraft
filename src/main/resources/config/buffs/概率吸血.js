export const name = "测试吸血";
export const enable = true;
export const role_condition = "attacker"  // 和你将buff放在hold_buff attack_buff defence_buff 息息相关，共同作用
export const priority= 1;  // 必写
export const calculate_type = "after_damage"; // 必写
export const duration = 118;

function run(damageAttr) {
    var attacker = damageAttr.getAttacker();
    var damage = damageAttr.getDamage();
    var x = damage * 0.4;
    attacker.setHealth(Math.min(attacker.getMaxHealth(), attacker.getHealth() + x))
    attacker.sendMessage("吸血: " + x);
    return true;
}