package bsbll.research;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.joining;

import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSortedMap;

import bsbll.Base;

final class AdvanceField {
    private final ImmutableSortedMap<Base, String> parts;
    
    private AdvanceField() {
        this.parts = ImmutableSortedMap.of();
    }
    
    public AdvanceField(Map<Base, String> parts) {
        this.parts = ImmutableSortedMap.<Base, String>orderedBy(Base.comparingOrigin())
                .putAll(parts)
                .build();
    }
    
    public boolean isEmpty() {
        return this.parts.isEmpty();
    }
    
    public ImmutableCollection<String> getParts() {
        return this.parts.values();
    }
    
    public boolean isError(Base from) {
        return countErrors(from) > 0;
    }
    
    public int countErrors(Base from) {
        requireNonNull(from);
        String s = this.parts.get(from);
        if (s == null) {
            return 0;
        }
        int e = 0;
        String marker = "(E";
        int index = s.indexOf(marker);
        while (index != -1) {
            ++e;
            index = s.indexOf("(E", index + "(E".length());
        }
        return e;
    }
    
    public int countAllErrors() {
        return this.parts.keySet().stream()
                .mapToInt(this::countErrors)
                .sum();
    }
 
    @Override
    public String toString() {
        return isEmpty()
                ? "[empty]"
                : getParts().stream().collect(joining(";"));
    }
    
    public static AdvanceField fromString(String s) {
        if (s.isEmpty()) {
            return new AdvanceField();
        }
        Map<Base, String> parts = new HashMap<>();
        for (String p : s.split(";")) {
            String tp = p.trim();
            if (tp.isEmpty()) {
                continue;
            }
            checkArgument(tp.length() >= 3, "Invalid advance field: %s", s);
            Base from = Base.fromChar(tp.charAt(0));
            parts.put(from, p);
        }
        return new AdvanceField(parts);
    }
    
}
