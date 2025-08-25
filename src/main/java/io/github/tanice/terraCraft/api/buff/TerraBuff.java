package io.github.tanice.terraCraft.api.buff;

import io.github.tanice.terraCraft.api.attribute.TerraCalculableMeta;

public interface TerraBuff extends TerraBaseBuff {

    /**
     * 获取buff的计算属性
     * @return 计算属性类
     */
    TerraCalculableMeta getMeta();
}
