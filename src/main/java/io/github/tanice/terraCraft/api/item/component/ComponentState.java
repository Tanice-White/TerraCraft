package io.github.tanice.terraCraft.api.item.component;

import javax.annotation.Nullable;

public class ComponentState {
    @Nullable
    protected Boolean original; /* 是否为初始值始自带 */
    @Nullable
    protected Boolean modified; /* 是否被更改 -- 暂时不影响更新 */
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

    public boolean isOriginal() {
        return original != null ? original : false;
    }

    public boolean isModified() {
        return modified != null ? modified : false;
    }

    public boolean isUpdatable() {
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
        if (isOriginal()) state |= 0b001;
        if (isModified()) state |= 0b010;
        if (isUpdatable()) state |= 0b100;
        return state;
    }
}
