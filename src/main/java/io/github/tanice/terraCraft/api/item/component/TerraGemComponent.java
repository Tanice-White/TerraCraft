package io.github.tanice.terraCraft.api.item.component;

public interface TerraGemComponent extends TerraBaseComponent {

    float getInlaySuccessChance();

    void setInlaySuccessChance(float chance);

    float getDismantleSuccessChance();

    void setDismantleSuccessChance(float chance);

    boolean isInlayFailLoss();

    void setInlayFailLoss(boolean loss);

    boolean isDismantleFailLoss();

    void setDismantleFailLoss(boolean loss);
}
