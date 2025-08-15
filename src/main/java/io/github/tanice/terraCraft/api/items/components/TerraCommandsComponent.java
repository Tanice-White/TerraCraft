package io.github.tanice.terraCraft.api.items.components;

import javax.annotation.Nullable;
import java.util.List;

public interface TerraCommandsComponent extends TerraBaseComponent {

    @Nullable List<String> getCommands();

    void setCommands(@Nullable List<String> commands);
}
