package io.github.tanice.terraCraft.core.buffs;

import io.github.tanice.terraCraft.api.buffs.TerraBaseBuff;
import io.github.tanice.terraCraft.api.buffs.TerraBuffRecord;
import io.github.tanice.terraCraft.api.buffs.TerraRunnableBuff;
import io.github.tanice.terraCraft.api.buffs.TerraTimerBuff;
import io.github.tanice.terraCraft.bukkit.utils.TerraWeakReference;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import java.util.UUID;

import static io.github.tanice.terraCraft.api.commands.TerraCommand.*;

public class BuffRecord implements TerraBuffRecord {
    private final TerraWeakReference entityReference;
    private TerraBaseBuff buff;
    
    private boolean permanent;
    private final boolean timer;
    private final boolean runnable;

    private int cooldownCounter;
    private int durationCounter;

    public BuffRecord(LivingEntity entity, TerraBaseBuff buff) {
        this(entity, buff, false);
    }

    public BuffRecord(LivingEntity entity, TerraBaseBuff buff, boolean isPermanent) {
        this.entityReference = new TerraWeakReference(entity);
        this.buff = buff;

        this.permanent = isPermanent;
        this.timer = buff instanceof TerraTimerBuff;
        this.runnable = buff instanceof TerraRunnableBuff;

        this.cooldownCounter = 0;
        this.durationCounter = buff.getDuration();
    }

    public BuffRecord(String uuid, TerraBaseBuff buff, int cooldownCounter, int durationCounter) {
        this.entityReference = new TerraWeakReference(Bukkit.getPlayer(UUID.fromString(uuid)));
        this.buff = buff;

        this.permanent = false;
        this.timer = buff instanceof TerraTimerBuff;
        this.runnable = buff instanceof TerraRunnableBuff;

        this.cooldownCounter = cooldownCounter;
        this.durationCounter = durationCounter;
    }

    /**
     * 自更新 (持续时间 取长合并, cd 更新)
     * 认为传入的 other 是新的
     */
    @Override
    public void merge(TerraBaseBuff other, boolean isPermanent) {
        this.permanent |= isPermanent;
        if (!this.permanent) {
            int p = this.buff.getDuration();
            int n = other.getDuration();
            other.setDuration(Math.max(p, n));
            int d = p - this.durationCounter;
            this.durationCounter = other.getDuration() - d;
        }
        this.buff = other;
    }

    @Override
    public TerraWeakReference getEntityReference() {
        return this.entityReference;
    }

    @Override
    public TerraBaseBuff getBuff() {
        return this.buff;
    }

    @Override
    public int getCooldownCounter() {
        return this.cooldownCounter;
    }

    @Override
    public void cooldown(int delta) {
        this.cooldownCounter -= delta;
        if (!this.permanent) this.durationCounter -= delta;
    }

    @Override
    public void reloadCooldown() {
        this.cooldownCounter = ((TerraTimerBuff) this.buff).getCd();
    }

    @Override
    public int getDurationCounter() {
        return this.durationCounter;
    }

    @Override
    public boolean isPermanent() {
        return this.permanent;
    }

    @Override
    public boolean isTimer() {
        return this.timer;
    }

    @Override
    public boolean isRunnable() {
        return this.runnable;
    }

    @Override
    public String toString() {
        return BOLD + YELLOW + "Buff Details in " + WHITE + (entityReference != null ? entityReference.get() : "None") + " :" + RESET + "\n" +
                AQUA + "Base Buff:" + WHITE + buff + "\n" +
                AQUA + "Permanent:" + WHITE + permanent + "\n" +
                AQUA + "Timer:" + WHITE + timer + "\n" +
                AQUA + "Runnable:" + WHITE + runnable + "\n" +
                AQUA + "Cooldown Counter:" + WHITE + cooldownCounter + "\n" +
                AQUA + "Duration Counter:" + WHITE + durationCounter + RESET;
    }
}
