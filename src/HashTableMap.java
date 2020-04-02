

// Here is Part II of 62:206 final (2020)  30%
// you need to write out the method findEntry(K key).

/** A hash table with linear probing and the MAD hash function */
//import java.util.Iterator;
import java.util.ArrayList;
import java.util.Random;

public class HashTableMap<K,V> {

    protected static class HashEntry<K,V> implements Entry<K,V> {
        protected K key;
        protected V value;
        public HashEntry(K k, V v) { key = k; value = v; }
        public V getValue() { return value; }
        public K getKey() { return key; }
        public V setValue(V val) {
            V oldValue = value;
            value = val;
            return oldValue;
        }
        public boolean equals(Entry<K,V> o) {
            HashEntry<K,V> ent;
            try { ent = (HashEntry<K,V>) o; }
            catch (ClassCastException ex) { return false; }
            return (ent.getKey().equals(key)) && (ent.getValue().equals(value));
        }

        public String toString() { return "[" + key + "," + value + "]"; }
    }
    protected Entry<K,V> AVAILABLE = new HashEntry<K,V>(null, null); // marker
    protected int n = 0; 		// number of entries in the table
    protected int capacity;	// capacity of the bucket array
    protected Entry<K,V>[] bucket;// bucket array
    protected int scale, shift;   // the shift and scaling factors
    /** Creates a hash table with initial capacity 1023. */
    public HashTableMap() { this(1023); }
    /** Creates a hash table with the given capacity. */
    public HashTableMap(int cap) {
        capacity = cap;
        bucket = (Entry<K,V>[]) new Entry[capacity]; // safe cast
        Random rand = new Random();
        scale = rand.nextInt(capacity-1) + 1;
        shift = rand.nextInt(capacity - 1) + 1;

    }
    /** Determines whether a key is valid. */
    protected void checkKey(K k) {
        if (k == null) throw new InvalidKeyException("Invalid key: null.");
    }
    /** Hash function applying MAD method to default hash code. */
    public int hashValue(K key) {
        return Math.abs(key.hashCode()*scale + shift) % capacity;
    }
    /** Returns the number of entries in the hash table. */
    public int size() { return n; }
    /** Returns whether or not the table is empty. */
    public boolean isEmpty() { return (n == 0); }
    /** Returns an iterable object containing all of the keys. */
    public Iterable<K> keys() {
        ArrayList<K> keys = new ArrayList<K>();
        for (int i=0; i<capacity; i++)
            if ((bucket[i] != null) && (bucket[i] != AVAILABLE))
                keys.add(bucket[i].getKey());
        return keys;
    }

    public Iterable<Entry<K,V>> entries() {
        ArrayList<Entry<K,V>> ent = new ArrayList<Entry<K,V>>();
        for (int i=0; i<capacity; i++)
            if ((bucket[i] != null) && (bucket[i] != AVAILABLE))
                ent.add(bucket[i]);
        return ent;
    }
    /** Helper search method - returns index of found key or -(a + 1),
     * where a is the index of the first empty or available slot found. */
    protected int findEntry(K key) throws InvalidKeyException {
//        Your work is here.                                              //<---------------------------
        checkKey( key);

        DefaultComparator<K> comp = new DefaultComparator<K>();

        int index = hashValue(key);
        int startingIndex = index;
        int firstAvailable = -1;
        Entry<K,V> currentEntry;
        do {

            currentEntry = bucket[index];
            if (currentEntry == null)  //if position was empty and never used
            {
                firstAvailable = index; //index will be converted to negative in last line
                startingIndex = index;  //this line is to end loop
            }

            else {
                if (currentEntry == AVAILABLE) //if was previously used but became available
                {
                    firstAvailable = index; //index will be converted to negative in last line
                    startingIndex = index;  //this line is to end loop
                }

                   //using the compare method from Entry<K,V> because it compares both the Key and the Value
                   //and at this point I am only searching for the key, Also, if the same key and different
                   //value resulted in Not Found, the table would end up with multiple versions of the key
                   //that's why I used Default Comparator and compared the Keys only

                if (comp.compare(key, currentEntry.getKey()) == 0)//if position is currently used by the same key
                    return index;   //return index AS POSITIVE value

                else  //if position is currently used by a different key, calculate index again
                    index = (index + 1) % capacity; //look at the next position (after checking the last position, returns to first position
            }

        }while( index != startingIndex );

        return (-(firstAvailable+1));


    }
    /** Returns the value associated with a key. */
    public V get (K key) throws InvalidKeyException {
        int i = findEntry(key);  // helper method for finding a key
        if (i < 0) return null;  // there is no value for this key
        return bucket[i].getValue();     // return the found value in this case
    }
    /** Put a key-value pair in the map, replacing previous one if it exists. */
    public V put (K key, V value) throws InvalidKeyException {
        int i = findEntry(key); //find the appropriate spot for this entry
        if (i >= 0)	//  this key has a previous value
            return ((HashEntry<K,V>) bucket[i]).setValue(value); // set new value
        if (n >= capacity/2) {
            rehash(); // rehash to keep the load factor <= 0.5
            i = findEntry(key); //find again the appropriate spot for this entry
        }
        bucket[-i-1] = new HashEntry<K,V>(key, value); // convert to proper index
        n++;
        return null; 	// there was no previous value
    }
    /** Doubles the size of the hash table and rehashes all the entries. */
    protected void rehash() {
        capacity = 2*capacity;
        Entry<K,V>[] old = bucket;
        bucket = (Entry<K,V>[]) new Entry[capacity]; // new bucket is twice as big
        Random rand = new Random();
        scale = rand.nextInt(capacity-1) + 1;    	// new hash scaling factor
        shift = rand.nextInt(capacity); 		// new hash shifting factor
        for (int i=0; i<old.length; i++) {
            Entry<K,V> e = old[i];
            if ((e != null) && (e != AVAILABLE)) { // a valid entry
                int j = - 1 - findEntry(e.getKey());
                bucket[j] = e;
            }
        }
    }
    /** Removes the key-value pair with a specified key. */
    public V remove (K key) throws InvalidKeyException {
        int i = findEntry(key);  	// find this key first
        if (i < 0) return null;  	// nothing to remove
        V toReturn = bucket[i].getValue();
        bucket[i] = AVAILABLE; 		// mark this slot as deactivated
        n--;
        return toReturn;
    }

    public static void main(String[] args) {

        HashTableMap<Integer, Integer> M = new HashTableMap(7);
        M.put(5,6);
        M.put(4, 7);

        System.out.print ("The keys are "  + M.keys());
        System.out.print("\n");

        System.out.print ("The values are "  + M.entries());
        System.out.print("\n");

        System.out.print ("The only value for 5 is "  + M.get(5));
        System.out.print("\n");
        for (int i = 7; i <= 100; ++i)
            M.put(i, i);
        M.remove (7);
        System.out.print ("The size of M is "  + M.size());
        System.out.print("\n");

        System.out.print ("The values are "  + M.entries());
        System.out.print("\n");

        HashTableMap<Integer, String> M1 = new HashTableMap<Integer, String>(7);
        String s1 = "Hello";
        String s2 = new String (s1);
        M1.put (32, s1);
        M1.put (31, s2);

        System.out.print ("The values are "  + M1.entries());
        System.out.print("\n");

    }
}
