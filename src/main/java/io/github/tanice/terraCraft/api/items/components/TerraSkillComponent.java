package io.github.tanice.terraCraft.api.items.components;

import javax.annotation.Nullable;
import java.util.List;

public interface TerraSkillComponent extends TerraBaseComponent {

    @Nullable List<String> getSkills();

    void setSkills(@Nullable List<String> skills);
}
