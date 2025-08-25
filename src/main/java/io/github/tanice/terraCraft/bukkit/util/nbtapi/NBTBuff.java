package io.github.tanice.terraCraft.bukkit.util.nbtapi;

import io.github.tanice.terraCraft.api.buff.TerraBaseBuff;
import io.github.tanice.terraCraft.api.buff.TerraTimerBuff;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;
import io.github.tanice.terraCraft.core.logger.TerraCraftLogger;

import javax.annotation.Nullable;
import java.util.Optional;

public class NBTBuff {
    private final String name;
    @Nullable
    private Float chance;
    @Nullable
    private Integer duration;
    @Nullable
    private Integer cd;

    public NBTBuff(String name, @Nullable Float chance, @Nullable Integer duration, @Nullable Integer cd) {
        this.name = name;
        this.chance = chance;
        this.duration = duration;
        this.cd = cd;
    }

    /**
     * 从String抽象出buff配置
     * @param strCfg 配置字符串
     * buff名称 #持续时间 :触发冷却 %触发概率
     */
    public NBTBuff(String strCfg) {
        String[] v = strCfg.split(" ");
        if (v.length < 1 || v.length > 4) {
            TerraCraftLogger.error("Invalid NBTBuff config: " + strCfg);
            this.name = "error";
            return;
        }
        this.name = v[0];
        String part, value;
        for (int i = 1; i < v.length; i++) {
            part = v[i].trim();
            if (part.isEmpty()) continue;
            try {
                if (part.startsWith("#")) {
                    value = part.substring(1).trim();
                    this.duration = Integer.parseInt(value);
                } else if (part.startsWith(":")) {
                    value = part.substring(1).trim();
                    this.cd = Integer.parseInt(value);
                } else if (part.startsWith("%")) {
                    value = part.substring(1).trim();
                    this.chance = Float.parseFloat(value);
                } else TerraCraftLogger.warning("Unknown NBTBuff config tag '" + part + "' in: " + strCfg);
            } catch (NumberFormatException e) {
                TerraCraftLogger.error("Invalid number format in NBTBuff config " + part + " :" + e.getMessage());
            }
        }
    }

    public String getName() {
        return this.name;
    }

    @Nullable
    public Float getChance() {
        return this.chance;
    }

    @Nullable
    public Integer getDuration() {
        return this.duration;
    }

    @Nullable
    public Integer getCd() {
        return this.cd;
    }

    @Nullable
    public TerraBaseBuff getAsTerraBuff() {
        Optional<TerraBaseBuff> baseBuff = TerraCraftBukkit.inst().getBuffManager().getBuff(name);
        if (baseBuff.isPresent()) {
            TerraBaseBuff buff = baseBuff.get();
            if (chance != null) buff.setChance(chance);
            if (duration != null) buff.setDuration(duration);
            if (buff instanceof TerraTimerBuff timerBuff && cd != null) timerBuff.setCd(cd);
            return buff;
        }
        return null;
    }

    @Override
    public String toString() {
        String v = name;
        if (duration != null) v += " #" + duration;
        if (cd != null) v += " :" + cd;
        if (chance != null) v += " %" + chance;
        return v;
    }
}
