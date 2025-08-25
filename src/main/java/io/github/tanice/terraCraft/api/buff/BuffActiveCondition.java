package io.github.tanice.terraCraft.api.buff;

public enum BuffActiveCondition {
    /* buff持有者作为攻击方激活 = ATTACKER_PHYSICAL + ATTACKER_SKILL */
    ATTACKER {
        @Override
        public boolean activeUnder(BuffActiveCondition condition) {
            return condition == BuffActiveCondition.ALL || condition == BuffActiveCondition.ATTACKER ||
                    condition == BuffActiveCondition.ATTACKER_PHYSICAL ||
                    condition == BuffActiveCondition.ATTACKER_SKILL;
        }
    },
    /* 非技能类攻击放激活 */
    ATTACKER_PHYSICAL,
    /* 技能释放方激活 */
    ATTACKER_SKILL,
    /* buff持有者作为防御方激活 */
    DEFENDER {
        @Override
        public boolean activeUnder(BuffActiveCondition condition) {
            return condition == BuffActiveCondition.ALL || condition == BuffActiveCondition.DEFENDER ||
                    condition == BuffActiveCondition.DEFENDER_PHYSICAL || condition == BuffActiveCondition.DEFENDER_SKILL;
        }
    },
    /* 非技能类被攻击方激活 */
    DEFENDER_PHYSICAL,
    /* 被技能攻击时激活 */
    DEFENDER_SKILL,
    /* 均能激活 */
    ALL;

    BuffActiveCondition() {

    }

    public boolean activeUnder(BuffActiveCondition condition) {
        return condition == this || condition == BuffActiveCondition.ALL;
    }
}
