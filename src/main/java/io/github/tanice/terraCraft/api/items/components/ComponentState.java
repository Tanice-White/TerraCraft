package io.github.tanice.terraCraft.api.items.components;

import javax.annotation.Nullable;

public class ComponentState {
    @Nullable
    protected Boolean original; /* 是否为初始值始自带 */
    @Nullable
    protected Boolean modified; /* 是否被更改 */
    @Nullable
    protected Boolean updatable; /* 是否允许更新 */

    public ComponentState(@Nullable Byte state) {
        if (state == null) return;
        this.original = (state & 0b001) != 0;
        this.modified = (state & 0b010) != 0;
        this.updatable = (state & 0b100) != 0;
    }

    public ComponentState(@Nullable Boolean original, @Nullable Boolean modified, @Nullable Boolean updatable) {
        this.original = original;
        this.modified = modified;
        this.updatable = updatable;
    }

    public boolean getOriginal() {
        return original != null ? original : false;
    }

    public boolean getModified() {
        return modified != null ? modified : false;
    }

    public boolean getUpdatable() {
        return updatable != null ? updatable : true;
    }

    public void setOriginal(boolean original) {
        this.original = original;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public void setUpdatable(boolean updatable) {
        this.updatable = updatable;
    }

    public Byte toNbtByte() {
        byte state = 0;
        if (getOriginal()) state |= 0b001;
        if (getModified()) state |= 0b010;
        if (getUpdatable()) state |= 0b100;
        return state;
    }
}
