export const terra_name = "格挡";
export const enable = true;
export const condition = "defender"
export const priority= 0;
export const section = "after_damage";

const Float = Java.type('java.lang.Float');
export function run(protocol) {
    const attacker = protocol.getAttacker();
    const defender = protocol.getDefender();
    if (defender.isBlocking() && isAttackFromFront(attacker, defender)) {
        protocol.setHit(false);
        defender.sendMessage("§a格挡成功!");
        defender.playSound(defender, "minecraft:item.shield.block", Float.valueOf("0.8"), Float.valueOf("1.2"));
        return false; // 后续不再计算
    }
    return true;
}

function isAttackFromFront(attacker, defender) {
    if (attacker == null) return false;

    // 获取位置信息
    const dLoc = defender.getLocation();
    const aLoc = attacker.getLocation();

    // 计算攻击方相对于防御方的方向向量（只考虑水平面上的方向）
    const dirX = aLoc.getX() - dLoc.getX();
    const dirZ = aLoc.getZ() - dLoc.getZ();

    // 计算向量长度（距离），避免除以零
    const distance = Math.sqrt(dirX * dirX + dirZ * dirZ);
    if (distance < 0.001) return false; // 几乎重叠时不判定

    // 标准化方向向量
    const normX = dirX / distance;
    const normZ = dirZ / distance;

    // 获取防御方的朝向角度（yaw），转换为方向向量
    const yaw = defender.getYaw() * Math.PI / 180; // 转为弧度
    const forwardX = -Math.sin(yaw); // Minecraft中yaw的正弦对应X方向
    const forwardZ = Math.cos(yaw);  // Minecraft中yaw的余弦对应Z方向

    // 计算两个方向向量的点积
    // 点积 = |a||b|cosθ，由于都是单位向量，点积直接等于cosθ
    const dotProduct = normX * forwardX + normZ * forwardZ;

    // 计算角度（θ）的余弦值，90度的余弦值是0，45度是√2/2≈0.7071
    // 当cosθ ≥ 0时，角度≤90度；当cosθ ≥ √2/2时，角度≤45度
    // 我们需要的是攻击方在防御方正面90度范围内（左右各45度）
    return dotProduct >= 0;
}