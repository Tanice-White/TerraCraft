export const terra_name = "测试闪避";
export const enable = true;
export const condition = "defender"
export const priority= 0;  // 必写
export const section = "before_damage"; // 必写
export const chance = 0.5;

export function run(TwDamageEvent) {
    // 可以自己在内部扩展生效条件
    var attacker = TwDamageEvent.getAttacker();
    var defender = TwDamageEvent.getDefender();
    var random = new Random();
    if (random.nextDouble() < chance) {
        attacker.sendMessage("对方闪避了");
        defender.sendMessage("~闪避~")
        // 可以播放个声音啥的
        // 双方互换位置
        var l = attacker.getLocation();
        attacker.teleport(defender.getLocation());
        defender.teleport(l);
        return false;  // 后续不执行
    }
    // 没有闪避则后续执行
    return true;
}