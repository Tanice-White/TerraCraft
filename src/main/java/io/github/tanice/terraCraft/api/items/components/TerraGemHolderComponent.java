package io.github.tanice.terraCraft.api.items.components;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

public interface TerraGemHolderComponent extends TerraBaseComponent {

    @Nullable List<ItemStack> getGems();

    void setGems(@Nullable List<ItemStack> gems);

    int getLimit();

    void setLimit(int limit);
}
