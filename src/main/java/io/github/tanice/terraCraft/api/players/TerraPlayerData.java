package io.github.tanice.terraCraft.api.players;

import java.util.Map;
import java.util.UUID;

public interface TerraPlayerData {

    void apply();

    UUID getId();

    double getHealth();

    double getMaxHealth();

    double getMana();

    double getMaxMana();

    double getManaRecoverySpeed();

    Map<String, Integer> getAte();

    TerraPlayerData clone();
}
