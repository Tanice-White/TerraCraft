package io.github.tanice.terraCraft.bukkit.listeners;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.particle.type.ParticleTypes;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerEntityAnimation;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerParticle;
import io.github.tanice.terraCraft.bukkit.TerraCraftBukkit;

public class GenericParticleListener implements PacketListener {

    private boolean cancelGenericParticles;

    public GenericParticleListener() {
        reload();
    }

    public void reload() {
        cancelGenericParticles = TerraCraftBukkit.inst().getConfigManager().shouldCancelGenericParticles();
    }

    public void unload() {
    }

    public void onPacketSend(PacketSendEvent event){
        if(!cancelGenericParticles) return;
        if(event.getPacketType()== PacketType.Play.Server.PARTICLE){
            WrapperPlayServerParticle packet = new WrapperPlayServerParticle(event);
            if (packet.getParticle().getType() == ParticleTypes.DAMAGE_INDICATOR) event.setCancelled(true);
        }else if (event.getPacketType() == PacketType.Play.Server.ENTITY_ANIMATION) {
            WrapperPlayServerEntityAnimation packet = new WrapperPlayServerEntityAnimation(event);
            if (packet.getType() == WrapperPlayServerEntityAnimation.EntityAnimationType.CRITICAL_HIT) event.setCancelled(true);
        }
    }
}
