package io.github.tanice.terraCraft.api.item.component;

public interface TerraGemComponent extends TerraBaseComponent {

    float getInlaySuccessChance(int gemNum, int limit);

    void setInlaySuccessExpr(String inlaySuccessExpr);

    float getDismantleSuccessChance(int gemNum, int limit);

    void setDismantleSuccessExpr(String dismantleSuccessExpr);

    boolean isInlayFailLoss();

    void setInlayFailLoss(boolean loss);

    boolean isDismantleFailLoss();

    void setDismantleFailLoss(boolean loss);
}
