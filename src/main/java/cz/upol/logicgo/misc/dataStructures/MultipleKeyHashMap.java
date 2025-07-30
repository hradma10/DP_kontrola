package cz.upol.logicgo.misc.dataStructures;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class MultipleKeyHashMap<T, U, V> {

    private final HashMap<T, HashMap<U, V>> map;

    public MultipleKeyHashMap() {
        map = new HashMap<>();
    }

    public void put(T key1, U key2, V val) {
        map.computeIfAbsent(key1, k -> new HashMap<>()).put(key2, val);
    }

    public V get(T key1, U key2) {
        HashMap<U, V> firstMap = map.get(key1);
        return (firstMap != null) ? firstMap.get(key2) : null;
    }

    public boolean containsKeys(T key1, U key2) {
        return map.containsKey(key1) && map.get(key1).containsKey(key2);
    }

    public void remove(T key1, U key2) {
        if (map.containsKey(key1)) {
            map.get(key1).remove(key2);
            if (map.get(key1).isEmpty()) {
                map.remove(key1);
            }
        }
    }

    public boolean containsKey1(T key1) {
        return map.containsKey(key1);
    }

    public boolean containsKey2(U key2) {
        return map.values().stream().anyMatch(innerMap -> innerMap.containsKey(key2));
    }

    public Set<T> keySet1() {
        return map.keySet();
    }

    public Set<U> keySet2(T key1) {
        return map.containsKey(key1) ? map.get(key1).keySet() : new HashSet<>();
    }

    public Collection<V> values() {
        Set<V> allValues = new HashSet<>();
        map.values().forEach(innerMap -> allValues.addAll(innerMap.values()));
        return allValues;
    }

    public int size() {
        return map.values().stream().mapToInt(HashMap::size).sum();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }

    public void clear() {
        map.clear();
    }

    public V replace(T key1, U key2, V newVal) {
        if (containsKeys(key1, key2)) {
            return map.get(key1).put(key2, newVal);
        }
        return null;
    }
}
