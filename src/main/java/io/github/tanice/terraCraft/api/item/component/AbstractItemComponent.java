package io.github.tanice.terraCraft.api.item.component;

import org.bukkit.inventory.ItemStack;

import javax.annotation.Nullable;

public abstract class AbstractItemComponent implements TerraBaseComponent {

    protected ComponentState state;

    public AbstractItemComponent(boolean updatable) {
        state = new ComponentState(true, false, updatable);
    }

    public AbstractItemComponent(ComponentState state) {
        this.state = state;
    }

    public ComponentState getState() {
        return this.state;
    }

    public void setState(@Nullable ComponentState state) {
        if (state == null) this.state = new ComponentState(null, null, null);
        else this.state = state;
    }

    @Override
    public boolean canUpdate() {
        return state.isUpdatable() && state.isOriginal();
    }

    @Override
    public void cover(ItemStack item) {
        doCover(item);
        updateLore();
    }

    /**
     * 获取支持游戏内指令页面显示的数据展示字符串
     */
    public abstract String toString();

    protected abstract void doCover(ItemStack item);

    protected abstract void updateLore();
}
