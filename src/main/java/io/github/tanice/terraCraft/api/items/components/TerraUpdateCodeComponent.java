package io.github.tanice.terraCraft.api.items.components;

import io.github.tanice.terraCraft.api.items.TerraBaseComponent;

public interface TerraUpdateCodeComponent extends TerraBaseComponent {

    String getCode();

    void setCode(String code);
}
