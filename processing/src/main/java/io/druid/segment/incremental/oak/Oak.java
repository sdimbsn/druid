//package io.druid.segment.incremental.oak;
//
//
//import java.nio.ByteBuffer;
//import java.util.*;
//import java.util.concurrent.ConcurrentNavigableMap;
//import java.util.function.BiFunction;
//import java.util.function.Function;
//
///**
// * Created by dbasin on 6/11/17.
// */
//public class Oak<K> implements ConcurrentNavigableMap<K , ByteBuffer> {
//
//    public Function<K, ByteBuffer> getSerializer() {
//        return serializer;
//    }
//
//    public Function<ByteBuffer, K> getDeserializer() {
//        return deserializer;
//    }
//
//    public Comparator<ByteBuffer> getComparator() {
//        return comparator;
//    }
//
//    Function<K, ByteBuffer> serializer;
//    Function<ByteBuffer, K> deserializer;
//    Comparator<ByteBuffer> comparator;
//
//    public Oak(Function<K, ByteBuffer> serializer, Function<ByteBuffer, K> deserializer, Comparator<ByteBuffer> comparator) {
//        this.serializer = serializer;
//        this.deserializer = deserializer;
//        this.comparator = comparator;
//    }
//
//    /***
//     * The function executes atomically provided as input updateFunction.
//     *  @param key              The key to be updated
//     *
//     * @param updateFunction  The update function accepts reference to internal ByteBuffer and can update it in place
//     *                       and then return as a result of function call.
//     *                        If the key is not present in OakMap, updateFunction is expected to create
//     *                       a new on-heap ByteBuffer and return it as a result of function call. In this case, OakMap
//     *                        will create appropriate off-heap buffer internally and copy there the result.
//     *
//     *
//     */
//    @Override
//    public ByteBuffer compute(K key,
//                              BiFunction<? super K,? super ByteBuffer,? extends ByteBuffer> updateFunction) {
//        throw new UnsupportedOperationException();
//    }
//
//
//    @Override
//    public int size() {
//        return 0;
//    }
//
//    @Override
//    public boolean isEmpty() {
//        return false;
//    }
//
//    @Override
//    public boolean containsKey(Object key) {
//        return false;
//    }
//
//    @Override
//    public boolean containsValue(Object value) {
//        return false;
//    }
//
//    @Override
//    public ByteBuffer get(Object key) {
//        return null;
//    }
//
//    @Override
//    public ByteBuffer put(K key, ByteBuffer value) {
//        return null;
//    }
//
//    @Override
//    public ByteBuffer remove(Object key) {
//        return null;
//    }
//
//    @Override
//    public void putAll(Map<? extends K, ? extends ByteBuffer> m) {
//
//    }
//
//    @Override
//    public void clear() {
//
//    }
//
//    @Override
//    public ConcurrentNavigableMap<K, ByteBuffer> subMap(K fromKey, boolean fromInclusive, K toKey, boolean toInclusive) {
//        return null;
//    }
//
//    @Override
//    public ConcurrentNavigableMap<K, ByteBuffer> headMap(K toKey, boolean inclusive) {
//        return null;
//    }
//
//    @Override
//    public ConcurrentNavigableMap<K, ByteBuffer> tailMap(K fromKey, boolean inclusive) {
//        return null;
//    }
//
//    @Override
//    public Comparator<? super K> comparator() {
//        return null;
//    }
//
//    @Override
//    public ConcurrentNavigableMap<K, ByteBuffer> subMap(K fromKey, K toKey) {
//        return null;
//    }
//
//    @Override
//    public ConcurrentNavigableMap<K, ByteBuffer> headMap(K toKey) {
//        return null;
//    }
//
//    @Override
//    public ConcurrentNavigableMap<K, ByteBuffer> tailMap(K fromKey) {
//        return null;
//    }
//
//    @Override
//    public K firstKey() {
//        return null;
//    }
//
//    @Override
//    public K lastKey() {
//        return null;
//    }
//
//    @Override
//    public Entry<K, ByteBuffer> lowerEntry(K key) {
//        return null;
//    }
//
//    @Override
//    public K lowerKey(K key) {
//        return null;
//    }
//
//    @Override
//    public Entry<K, ByteBuffer> floorEntry(K key) {
//        return null;
//    }
//
//    @Override
//    public K floorKey(K key) {
//        return null;
//    }
//
//    @Override
//    public Entry<K, ByteBuffer> ceilingEntry(K key) {
//        return null;
//    }
//
//    @Override
//    public K ceilingKey(K key) {
//        return null;
//    }
//
//    @Override
//    public Entry<K, ByteBuffer> higherEntry(K key) {
//        return null;
//    }
//
//    @Override
//    public K higherKey(K key) {
//        return null;
//    }
//
//    @Override
//    public Entry<K, ByteBuffer> firstEntry() {
//        return null;
//    }
//
//    @Override
//    public Entry<K, ByteBuffer> lastEntry() {
//        return null;
//    }
//
//    @Override
//    public Entry<K, ByteBuffer> pollFirstEntry() {
//        return null;
//    }
//
//    @Override
//    public Entry<K, ByteBuffer> pollLastEntry() {
//        return null;
//    }
//
//    @Override
//    public ConcurrentNavigableMap<K, ByteBuffer> descendingMap() {
//        return null;
//    }
//
//    @Override
//    public NavigableSet<K> navigableKeySet() {
//        return null;
//    }
//
//    @Override
//    public NavigableSet<K> keySet() {
//        return null;
//    }
//
//
//    @Override
//    public NavigableSet<K> descendingKeySet() {
//        return null;
//    }
//
//    @Override
//    public Collection<ByteBuffer> values() {
//        return null;
//    }
//
//    @Override
//    public Set<Entry<K, ByteBuffer>> entrySet() {
//        return null;
//    }
//
//    @Override
//    public ByteBuffer putIfAbsent(K key, ByteBuffer value) {
//        return null;
//    }
//
//    @Override
//    public boolean remove(Object key, Object value) {
//        return false;
//    }
//
//    @Override
//    public boolean replace(K key, ByteBuffer oldValue, ByteBuffer newValue) {
//        return false;
//    }
//
//    @Override
//    public ByteBuffer replace(K key, ByteBuffer value) {
//        return null;
//    }
//
//
//}
