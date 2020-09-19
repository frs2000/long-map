package de.comparus.opensource.longmap;

import org.junit.Test;

import java.util.Objects;

import static org.junit.Assert.*;

public class LongMapImplTest {

    @Test
    public void put_returns_value() {
        LongMapImpl<String> map = new LongMapImpl<>();
        assertEquals("first", map.put(1, "first"));
    }

    @Test
    public void put_if_map_was_empty() {
        LongMapImpl<String> map = new LongMapImpl<>();
        map.put(1, "first");
        String[] expected = {"first"};
        assertArrayEquals(expected, map.values());
    }

    @Test
    public void put_if_was_collision_and_key_matched_rewrite_value() {
        LongMapImpl<String> map = new LongMapImpl<>();
        map.put(1, "first");
        map.put(1, "new_first");
        String[] expected = {"new_first"};
        assertArrayEquals(expected, map.values());
    }

    @Test
    public void put_unique_pairs_with_collision() {
        LongMapImpl<String> map = new LongMapImpl<>();
        map.put(1, "first");
        map.put(16, "collision_key");
        String[] expected = {"first", "collision_key"};
        assertArrayEquals(expected, map.values());
    }

    @Test
    public void put_collision_for_pair_with_negative_key() {
        LongMapImpl<String> map = new LongMapImpl<>();
        map.put(1, "first");
        map.put(-16, "negative_collision_key");
        String[] expected = {"first", "negative_collision_key"};
        assertArrayEquals(expected, map.values());
    }

    @Test
    public void put_with_map_rebuilding() {
        int rebuildCount = 13;
        LongMapImpl<Integer> rebuiltMap = createMap(rebuildCount);
        rebuiltMap.remove(rebuildCount - 1);

        LongMapImpl<Integer> map = createMap(rebuildCount - 1);
        assertArrayEquals(map.values(), rebuiltMap.values());
    }

    private LongMapImpl<Integer> createMap(int elCount) {
        LongMapImpl<Integer> map = new LongMapImpl<>();
        for (int i = 0; i < elCount; i++) map.put(i, i);
        return map;
    }

    @Test
    public void get_when_key_exists() {
        LongMapImpl<String> map = new LongMapImpl<>();
        map.put(1, "first");
        assertEquals("first", map.get(1));
    }

    @Test
    public void get_when_was_collision_and_key_exists() {
        LongMapImpl<String> map = new LongMapImpl<>();
        map.put(1, "first");
        map.put(16, "collision_key");
        assertEquals("collision_key", map.get(16));
    }

    @Test
    public void get_from_empty_map() {
        LongMapImpl<String> map = new LongMapImpl<>();
        assertNull(map.get(1));
    }

    @Test
    public void get_when_node_with_key_not_exists() {
        LongMapImpl<String> map = new LongMapImpl<>();
        map.put(1, "first");
        assertNull(map.get(2));
    }

    @Test
    public void get_if_exists_collision_and_node_with_key_not_exists_in_chain() {
        LongMapImpl<String> map = new LongMapImpl<>();
        map.put(1, "first");
        map.put(16, "collision_key");
        assertNull(map.get(31));
    }

    @Test
    public void remove_return_value_if_key_exists() {
        LongMapImpl<String> map = new LongMapImpl<>();
        map.put(1, "first");
        assertEquals("first", map.remove(1));
    }

    @Test
    public void remove_return_null_if_key_not_exists() {
        LongMapImpl<String> map = new LongMapImpl<>();
        map.put(1, "first");
        assertNull(map.remove(2));
    }

    @Test
    public void remove_return_null_if_map_is_empty() {
        LongMapImpl<String> map = new LongMapImpl<>();
        assertNull(map.remove(1));
    }

    @Test
    public void remove_return_value_if_was_collision_key_exists_and_node_first_in_chain() {
        LongMapImpl<String> map = new LongMapImpl<>();
        map.put(1, "first");
        map.put(16, "collision_key");
        assertEquals("first", map.remove(1));
    }

    @Test
    public void remove_if_was_collision_key_exists_and_node_is_last_in_chain() {
        LongMapImpl<String> map = new LongMapImpl<>();
        map.put(1, "first");
        map.put(16, "collision_key");
        map.put(31, "31_collision_key");
        assertEquals("31_collision_key", map.remove(31));
    }

    @Test
    public void remove_if_was_collision_and_node_inside_chain() {
        LongMapImpl<String> map = new LongMapImpl<>();
        map.put(1, "first");
        map.put(16, "16_collision_key");
        map.put(31, "collision_key");
        assertEquals("16_collision_key", map.remove(16));
    }

    @Test
    public void remove_return_null_if_was_collision_and_key_not_exists_in_chain() {
        LongMapImpl<String> map = new LongMapImpl<>();
        map.put(1, "first");
        map.put(16, "collision_key");
        assertNull(map.remove(31));
    }

    @Test
    public void isEmpty_true() {
        LongMapImpl<Integer> map = new LongMapImpl<>();
        assertTrue(map.isEmpty());
    }

    @Test
    public void isEmpty_false() {
        LongMapImpl<Integer> map = new LongMapImpl<>();
        map.put(1, 1);
        assertFalse(map.isEmpty());
    }

    @Test
    public void containsKey_true_if_node_with_key_exists() {
        LongMapImpl<Integer> map = new LongMapImpl<>();
        map.put(1, 1);
        assertTrue(map.containsKey(1));
    }

    @Test
    public void containsKey_true_if_was_collision_and_node_with_key_exists() {
        LongMapImpl<String> map = new LongMapImpl<>();
        map.put(1, "first");
        map.put(16, "collision_key");
        assertTrue(map.containsKey(16));
    }

    @Test
    public void containsKey_false_if_node_with_key_not_exists() {
        LongMapImpl<Integer> map = new LongMapImpl<>();
        map.put(1, 1);
        assertFalse(map.containsKey(2));
    }

    @Test
    public void containsKey_false_for_empty_map() {
        LongMapImpl<Integer> map = new LongMapImpl<>();
        assertFalse(map.containsKey(1));
    }

    @Test
    public void containsValue() {
        LongMapImpl<Integer> map = new LongMapImpl<>();
        map.put(1, 1);
        assertTrue(map.containsValue(1));
    }

    @Test
    public void containsValue_with_custom_object() {
        LongMapImpl<Custom> map = new LongMapImpl<>();
        map.put(1, new Custom(10, "str"));
        assertTrue(map.containsValue(new Custom(10, "str")));
    }

    @Test
    public void containsValue_false_for_empty_map() {
        LongMapImpl<String> map = new LongMapImpl<>();
        assertFalse(map.containsValue("not_exists_val"));
    }

    @Test
    public void containsValue_if_was_collision_and_value_exists() {
        LongMapImpl<String> map = new LongMapImpl<>();
        map.put(1, "first");
        map.put(16, "collision_key");
        assertTrue(map.containsValue("collision_key"));
    }

    @Test
    public void containsValue_if_was_collision_and_value_not_exists() {
        LongMapImpl<String> map = new LongMapImpl<>();
        map.put(1, "first");
        map.put(16, "collision_key");
        assertFalse(map.containsValue("not_exists_val"));
    }

    @Test
    public void keys_without_collision() {
        LongMapImpl<Integer> map = new LongMapImpl<>();
        map.put(1L, 1);
        map.put(2147483648L, 2);
        long[] expected = {1L, 2147483648L};
        assertArrayEquals(expected, map.keys());
    }

    @Test
    public void keys_with_collision_and_negative_key() {
        LongMapImpl<Integer> map = new LongMapImpl<>();
        map.put(1L, 1);
        map.put(-16L, 1);
        long[] expected = {1L, -16L};
        assertArrayEquals(expected, map.keys());
    }

    @Test
    public void keys_for_empty_map() {
        LongMapImpl<Integer> map = new LongMapImpl<>();
        long[] expected = {};
        assertArrayEquals(expected, map.keys());
    }

    @Test
    public void values_without_collision() {
        LongMapImpl<String> map = new LongMapImpl<>();
        map.put(1, "first");
        map.put(2, "second");
        String[] expected = {"first", "second"};
        assertArrayEquals(expected, map.values());
    }

    @Test
    public void values_with_collision() {
        LongMapImpl<String> map = new LongMapImpl<>();
        map.put(1, "first");
        map.put(16, "collision_key");
        String[] expected = {"first", "collision_key"};
        assertArrayEquals(expected, map.values());
    }

    @Test
    public void values_for_empty_map() {
        LongMapImpl<String> map = new LongMapImpl<>();
        String[] expected = {};
        assertArrayEquals(expected, map.values());
    }

    @Test
    public void size_when_pairs_has_unique_key() {
        LongMapImpl<Integer> map = new LongMapImpl<>();
        map.put(1, 1);
        map.put(2, 2);
        assertEquals(2, map.size());
    }

    @Test
    public void size_when_pairs_has_duplicate_key() {
        LongMapImpl<String> map = new LongMapImpl<>();
        map.put(1, "1");
        map.put(2, "2");
        map.put(2, "2_duplicate_key");
        assertEquals(2, map.size());
    }

    @Test
    public void size_for_empty_map() {
        LongMapImpl<Integer> map = new LongMapImpl<>();
        assertEquals(0, map.size());
    }

    @Test
    public void clear_not_empty_map() {
        LongMapImpl<Integer> map = new LongMapImpl<>();
        map.put(1, 1);
        map.clear();
        assertEquals(0, map.size());
    }

    @Test
    public void clear_if_map_is_empty() {
        LongMapImpl<Integer> map = new LongMapImpl<>();
        map.clear();
        assertEquals(0, map.size());
    }
}

class Custom {
    private final int digit;
    private final String str;

    public Custom(int digit, String str) {
        this.digit = digit;
        this.str = str;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Custom custom = (Custom) o;
        return digit == custom.digit &&
                Objects.equals(str, custom.str);
    }
}