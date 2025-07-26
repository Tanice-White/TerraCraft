package io.github.tanice.terraCraft.api.buffs;

public interface TerraRunnableBuff extends TerraBaseBuff {

    /**
     * 获取buff对应的js文件名
     * @return js文件名
     */
    String getFileName();
}
