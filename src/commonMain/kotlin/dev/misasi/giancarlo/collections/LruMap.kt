package dev.misasi.giancarlo.collections

class LruMap<Key, Value>(private val maxSize: Int): MutableMap<Key, Value> {
    private val data = mutableMapOf<Key, Value>()
    private val leastRecentlyUsed = LinkedHashSet<Key>()

    // From Map
    override val size get() = data.size
    override fun isEmpty(): Boolean = data.isEmpty()
    override fun containsKey(key: Key): Boolean = data.containsKey(key)
    override fun containsValue(value: Value): Boolean = data.containsValue(value)
    override fun get(key: Key): Value? = data[key].apply {
        updateLeastRecentlyUsed(key)
    }

    // From MutableMap
    override val keys get() = data.keys
    override val values get() = data.values
    override val entries get() = data.entries
    override fun remove(key: Key): Value? = data.remove(key).apply { leastRecentlyUsed.remove(key) }
    override fun putAll(from: Map<out Key, Value>) = from.entries.forEach { put(it.key, it.value) }
    override fun clear() = data.clear().also { leastRecentlyUsed.clear() }
    override fun put(key: Key, value: Value): Value? = this.data.put(key, value).apply {
        updateLeastRecentlyUsed(key)
        evictLeastRecentlyUsedEntries()
    }

    private fun updateLeastRecentlyUsed(key: Key) {
        leastRecentlyUsed.remove(key)
        leastRecentlyUsed.add(key)
    }

    private fun evictLeastRecentlyUsedEntries() {
        while (data.size > maxSize) {
            leastRecentlyUsed.firstOrNull()?.let { remove(it) }
        }
    }
}