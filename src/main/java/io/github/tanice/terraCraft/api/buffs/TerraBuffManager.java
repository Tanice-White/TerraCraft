package io.github.tanice.terraCraft.api.buffs;

import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface TerraBuffManager {

    /**
     * 获取全局已注册的buff名称集合
     * @return 全局已注册的buff名称集合
     */
    Collection<String> getBuffNames();

    /**
     * 获取buff实例
     * @param name buff内部名
     * @return buff实例的可变副本
     */
    Optional<TerraBaseBuff> getBuff(String name);

    /**
     * buff名过滤器
     * @param buffNames 待过滤的buff名称集合
     * @param name 目标字符串
     * @return 以目标字符串开头的buff名称集合
     */
    Collection<String> filterBuffs(Collection<String> buffNames, String name);

    /**
     * 删除目标实体的所有buff
     *
     * @param entity 目标实体
     */
    void unregister(LivingEntity entity);

    /**
     * 从数据库加载玩家buff
     * @param player 玩家实体
     */
    void loadPlayerBuffs(Player player);

    /**
     * 缓存并清除所有目标玩家的buff
     * @param player 玩家实体
     */
    void SaveAndClearPlayerBuffs(Player player);

    /**
     * 缓存所有玩家buff
     */
    void saveAllPlayerRecords();

    void activateBuff(LivingEntity entity, String buffName);

    void activateBuff(LivingEntity entity, String buffName, boolean isPermanent);

    void activateBuff(LivingEntity entity, TerraBaseBuff buff);

    void activateBuff(LivingEntity entity, TerraBaseBuff buff, boolean isPermanent);

    void activateBuffs(LivingEntity entity, List<String> buffNames);

    void activateBuffs(LivingEntity entity, List<String> buffNames, boolean isPermanent);

    void activateBuffs(LivingEntity entity, Collection<TerraBaseBuff> buffs);

    void activateBuffs(LivingEntity entity, Collection<TerraBaseBuff> buffs, boolean isPermanent);

    /**
     * 让实体的buff集合失效
     * @param entity 目标实体
     * @param buffs 目标buff实体集合
     */
    void deactivateBuffs(LivingEntity entity, Collection<TerraBaseBuff> buffs);

    /**
     * 清空实体的所有buff
     * @param entity 目标实体
     */
    void deactivateEntityBuffs(LivingEntity entity);

    /**
     * 获取实体的有效buff列表
     * @param entity 目标实体
     * @return buff列表
     */
    List<TerraCalculableMeta> getEntityActiveBuffs(LivingEntity entity);
}
