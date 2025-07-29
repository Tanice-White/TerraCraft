package io.github.tanice.terraCraft.api.players;

import java.util.Map;
import java.util.UUID;

public interface TerraPlayerData {

    void apply();

    void merge(TerraPlayerData playerData);

    UUID getId();

    double getHealth();

    double getMaxHealth();

    double getMana();

    void setMana(double mana);

    double getMaxMana();

    double getManaRecoverySpeed();

    Map<String, Integer> getAte();

    TerraPlayerData clone();
}
