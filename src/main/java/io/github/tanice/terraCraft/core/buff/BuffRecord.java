package io.github.tanice.terraCraft.core.buff;

import io.github.tanice.terraCraft.api.buff.TerraBaseBuff;
import io.github.tanice.terraCraft.api.buff.TerraBuffRecord;
import io.github.tanice.terraCraft.api.buff.TerraRunnableBuff;
import io.github.tanice.terraCraft.api.buff.TerraTimerBuff;
import io.github.tanice.terraCraft.bukkit.util.TerraWeakReference;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;

import java.util.UUID;

import static io.github.tanice.terraCraft.api.command.TerraCommand.*;

public class BuffRecord implements TerraBuffRecord {
    private final TerraWeakReference entityReference;
    private TerraBaseBuff buff;

    private final boolean timer;
    private final boolean runnable;

    private boolean toRemove;

    private int cooldownCounter;
    private int durationCounter;

    public BuffRecord(LivingEntity entity, TerraBaseBuff buff) {
        this.entityReference = new TerraWeakReference(entity);
        this.buff = buff;
        this.timer = buff instanceof TerraTimerBuff;
        this.runnable = buff instanceof TerraRunnableBuff;
        this.toRemove = false;
        this.cooldownCounter = 0;
        this.durationCounter = buff.getDuration();
    }

    public BuffRecord(String uuid, TerraBaseBuff buff, int cooldownCounter, int durationCounter) {
        this.entityReference = new TerraWeakReference(Bukkit.getPlayer(UUID.fromString(uuid)));
        this.buff = buff;
        this.timer = buff instanceof TerraTimerBuff;
        this.runnable = buff instanceof TerraRunnableBuff;
        this.toRemove = false;
        this.cooldownCounter = cooldownCounter;
        this.durationCounter = durationCounter;
    }

    /**
     * 自更新 (持续时间 取长合并, cd 更新)
     * 认为传入的 other 是新的
     */
    @Override
    public void merge(TerraBaseBuff other) {
        int p = this.buff.getDuration();
        int n = other.getDuration();
        other.setDuration(Math.max(p, n));
        int d = p - this.durationCounter;
        this.durationCounter = other.getDuration() - d;
        this.buff = other;
        this.toRemove = false;
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
        cooldownCounter -= delta;
        durationCounter -= delta;
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
    public boolean isTimer() {
        return this.timer;
    }

    @Override
    public boolean isRunnable() {
        return this.runnable;
    }

    @Override
    public boolean isToRemove() {
        return this.toRemove;
    }

    @Override
    public void setToRemove(boolean remove) {
        this.toRemove = remove;
    }

    @Override
    public String toString() {
        return BOLD + YELLOW + "buff in " + WHITE + (entityReference != null ? entityReference.get() : "null") + " :" + RESET + "\n" +
                AQUA + "buff:" + WHITE + buff + "\n" +
                AQUA + "timer:" + WHITE + timer + "\n" +
                AQUA + "runnable:" + WHITE + runnable + "\n" +
                AQUA + "cooldown:" + WHITE + cooldownCounter + "\n" +
                AQUA + "duration:" + WHITE + durationCounter + "\n" +
                AQUA + "to remove" + WHITE + toRemove + RESET;
    }
}
