package io.github.tanice.terraCraft.api.item.component;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

public interface TerraGemHolderComponent extends TerraBaseComponent {
    /**
     * 由于可更新性质，实际上宝石的数量可能比limit多(多余的不会生效)
     * @return 所镶嵌的宝石数量
     */
    int getGemNums();

    List<ItemStack> getGems();

    void setGems(@Nullable List<ItemStack> gems);

    int getLimit();

    void setLimit(int limit);
}
