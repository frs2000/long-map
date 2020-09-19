package de.comparus.opensource.longmap;

import java.util.ArrayList;
import java.util.List;

public class LongMapImpl<V> implements LongMap<V> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final float LOAD_FACTOR = 0.75f;
    private static final int EMPTY_MAP_SIZE = 0;
    private static final int MULTIPLIER = 2;
    private Long<V>[] arr;
    private int mapSize;

    private class Long<V> {
        final long key;
        V val;
        Long<V> nextNode;

        public Long(long key, V val) {
            this.key = key;
            this.val = val;
        }

        @Override
        public String toString() {
            return key + "=" + val;
        }
    }

    public LongMapImpl() {
        arr = createArr(DEFAULT_CAPACITY);
    }

    private Long<V>[] createArr(int capacity) {
        return new Long[capacity];
    }

    public V put(long key, V value) {
        if (mapSize >= arr.length * LOAD_FACTOR) rebuildArr();
        putToArr(key, value, arr);
        return value;
    }

    private void rebuildArr() {
        Long<V>[] newArr = createArr(arr.length * MULTIPLIER);
        for (int i = 0; i < arr.length; i++) {
            for (Long<V> node = arr[i]; node != null; node = node.nextNode) {
                putToArr(node.key, node.val, newArr);
            }
        }
        arr = newArr;
    }

    private void putToArr(long key, V value, Long<V>[] arr) {
        int index = getBucketIndex(key, arr);

        if (arr[index] == null) {
            arr[index] = new Long<>(key, value);
            mapSize++;
        } else {
            putInsideChain(key, value, arr[index]);
        }
    }

    private int getBucketIndex(long key, Long<V>[] arr) {
        int maxIndex = arr.length - 1;
        return Math.abs((int) (key % maxIndex));
    }

    private void putInsideChain(long key, V value, Long<V> node) {
        if (node.key == key) {
            node.val = value;
            return;
        }
        if (node.nextNode == null) {
            node.nextNode = new Long<>(key, value);
            mapSize++;
            return;
        }
        putInsideChain(key, value, node.nextNode);
    }

    public V get(long key) {
        if (isEmpty()) return null;
        int index = getBucketIndex(key, arr);
        if (arr[index] == null) return null;
        return findValueInChain(key, arr[index]);
    }

    private V findValueInChain(long key, Long<V> node) {
        if (node.key == key) return node.val;
        if (node.nextNode == null) return null;
        return findValueInChain(key, node.nextNode);
    }

    public V remove(long key) {
        if (isEmpty()) return null;
        int index = getBucketIndex(key, arr);
        if (arr[index] == null) return null;

        if (arr[index].key == key) {
            V val = arr[index].val;
            if (arr[index].nextNode == null) {
                arr[index] = null;
            } else {
                arr[index] = arr[index].nextNode;
            }
            mapSize--;
            return val;
        }
        return removeFromChain(key, arr[index], arr[index].nextNode);
    }

    private V removeFromChain(long key, Long<V> currentNode, Long<V> nextNode) {
        if (key == nextNode.key) {
            V val = nextNode.val;
            if (nextNode.nextNode == null) {
                currentNode.nextNode = null;
            } else {
                currentNode.nextNode = nextNode.nextNode;
            }
            mapSize--;
            return val;
        } else {
            if (nextNode.nextNode == null) return null;
        }
        return removeFromChain(key, nextNode, nextNode.nextNode);
    }

    public boolean isEmpty() {
        return mapSize == EMPTY_MAP_SIZE;
    }

    public boolean containsKey(long key) {
        if (isEmpty()) return false;
        int index = getBucketIndex(key, arr);
        if (arr[index] == null) return false;
        return isContainsKey(key, arr[index]);
    }

    private boolean isContainsKey(long key, Long<V> node) {
        if (node.key == key) return true;
        if (node.nextNode == null) return false;
        return isContainsKey(key, node.nextNode);
    }

    public boolean containsValue(V value) {
        if (isEmpty()) return false;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] != null && isNodeContainsValue(arr[i], value)) return true;
        }
        return false;
    }

    public boolean isNodeContainsValue(Long<V> node, V value) {
        if (node.val == value || node.val.equals(value)) return true;
        if (node.nextNode == null) return false;
        return isNodeContainsValue(node.nextNode, value);
    }

    public long[] keys() {
        long[] keys = new long[mapSize];
        if (mapSize > EMPTY_MAP_SIZE) {
            int freeIndex = 0;
            for (int i = 0; i < arr.length; i++) {
                for (Long<V> node = arr[i]; node != null; node = node.nextNode) {
                    keys[freeIndex] = node.key;
                    freeIndex++;
                }
            }
        }
        return keys;
    }

    public V[] values() {
        List<V> values = new ArrayList<>();
        if (mapSize > EMPTY_MAP_SIZE) {
            for (int i = 0; i < arr.length; i++) {
                for (Long<V> node = arr[i]; node != null; node = node.nextNode) {
                    values.add(node.val);
                }
            }
        }
        return (V[]) values.toArray();
    }

    public long size() {
        return mapSize;
    }

    public void clear() {
        if (mapSize > EMPTY_MAP_SIZE) {
            mapSize = EMPTY_MAP_SIZE;
            arr = createArr(DEFAULT_CAPACITY);
        }
    }
}