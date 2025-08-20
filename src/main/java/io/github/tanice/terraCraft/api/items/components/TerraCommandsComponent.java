package io.github.tanice.terraCraft.api.items.components;

import javax.annotation.Nullable;
import java.util.List;

public interface TerraCommandsComponent extends TerraBaseComponent {

    /**
     * 返回物品消耗后执行的指令列表的引用
     */
    List<String> getCommands();

    /**
     * 设置物品消耗后执行的指令列表
     */
    void setCommands(@Nullable List<String> commands);
}
