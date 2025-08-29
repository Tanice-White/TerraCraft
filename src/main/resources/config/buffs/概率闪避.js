export const terra_name = "闪避";
export const enable = true;
export const condition = "defender"
export const priority= 0;  // 必写
export const section = "before_damage"; // 必写
export const chance = 0.75;

export function run(protocol) {
    // 可以自己在内部扩展生效条件
    var attacker = protocol.getAttacker();
    var defender = protocol.getDefender();
    if (Math.random() < chance) {
        attacker.sendMessage("对方闪避了");
        defender.sendMessage("~闪避~")
        // 可以播放个声音啥的
        // 双方互换位置
        var l = attacker.getLocation();
        attacker.teleport(defender.getLocation());
        defender.teleport(l);
        protocol.setHit(false);  // 认为本次攻击没有成功
        return false;  // 后续不执行
    }
    // 没有闪避则后续执行
    return true;
}