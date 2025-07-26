package io.github.tanice.terraCraft.bukkit.utils.pdc.types;

import io.github.tanice.terraCraft.bukkit.utils.annotation.NonnullByDefault;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;

import java.util.StringJoiner;
import java.util.regex.Pattern;

@NonnullByDefault
public class StringArrayDataType implements PersistentDataType<String, String[]> {
    private static final String DELIMITER = "\001"; // 使用不可见字符作为分隔符
    private static final Pattern DELIMITER_PATTERN = Pattern.compile(DELIMITER);

    @Override
    public Class<String> getPrimitiveType() {
        return String.class;
    }

    @Override
    public Class<String[]> getComplexType() {
        return String[].class;
    }

    @Override
    public String toPrimitive(String[] complex, PersistentDataAdapterContext context) {
        StringJoiner joiner = new StringJoiner(DELIMITER);
        for (String element : complex) {
            if (element != null) {
                String escaped = element.replace(DELIMITER, DELIMITER + DELIMITER);
                joiner.add(escaped);
            }
        }
        return joiner.toString();
    }

    @Override
    public String[] fromPrimitive(String primitive, PersistentDataAdapterContext context) {
        if (primitive.isEmpty()) {
            return new String[0];
        }

        return DELIMITER_PATTERN.splitAsStream(primitive)
                .map(s -> s.replace(DELIMITER + DELIMITER, DELIMITER))
                .toArray(String[]::new);
    }
}
