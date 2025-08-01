export const name = "测试持续掉血";
export const enable = true;
export const role_condition = "all"  // attacker defender all -> 决定 buff 持有者作为哪一方，以及是否是技能 能够生效 必写
export const priority= 0;  // 必写
export const active_section = "timer"; // 必写
export const chance = 0.75;
export const cd = 20;
// 执行周期是每2tick，所以持续时间必须是偶数
// 若需要执行t次，则duration可以设置为 cd * (t + 1) - 2
export const duration = 118;

// Timer类的参数只有目标实体
function run(target) {
    // livingEntity.setHealth(livingEntity.getHealth() - Math.min(livingEntity.getHealth(), 1.5));
    target.damage(1.5);
    target.setNoDamageTicks(0);
    return false;  // 返回否表示“禁止后续属性计算”  Timer类的返回值无效
}