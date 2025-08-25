package io.github.tanice.terraCraft.bukkit.util.annotation;

import javax.annotation.Nonnull;
import javax.annotation.meta.TypeQualifierDefault;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Nonnull
@Documented
@TypeQualifierDefault(value={ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(value= RetentionPolicy.RUNTIME)
public @interface NonnullByDefault {
}

