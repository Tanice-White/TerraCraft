package io.github.tanice.terraCraft.bukkit.items.components;

import io.github.tanice.terraCraft.api.items.TerraBaseItem;
import io.github.tanice.terraCraft.api.items.components.TerraSkillComponent;
import io.github.tanice.terraCraft.bukkit.items.AbstractItemComponent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;
import java.util.List;

public class SkillComponent extends AbstractItemComponent implements TerraSkillComponent {

    @Nullable
    private List<String> skills;

    public SkillComponent(@Nullable List<String> skills) {
        this.skills = skills;
    }

    public static SkillComponent from(ItemStack item) {

    }

    @Override
    public void apply(TerraBaseItem item) {

    }

    @Override
    public @Nullable List<String> getSkills() {
        return this.skills;
    }

    @Override
    public void setSkills(@Nullable List<String> skills) {
        this.skills = skills;
    }
}
