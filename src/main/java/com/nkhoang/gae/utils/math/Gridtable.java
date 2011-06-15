package com.nkhoang.gae.utils.math;

import java.util.*;

public class Gridtable
{
    private HashMap primaryTable = new HashMap();
    protected Comparator primaryKeyComparator;
    protected Comparator secondaryKeyComparator;

    /**
        Squirrel away a default Comparator object for sorting the primary keys.
    */
    public void setPrimaryKeyComparator (Comparator c)
    {
        primaryKeyComparator = c;
    }

    /**
        Squirrel away a default Comparator object for sorting the secondary keys.
    */
    public void setSecondaryKeyComparator (Comparator c)
    {
        secondaryKeyComparator = c;
    }

    /**
        Store the specified element indexed by <key1, key2>.
    */
    public void put (Object key1, Object key2, Object element)
    {
        HashMap secondaryTable = (HashMap)primaryTable.get(key1);
        if (secondaryTable == null) {
            secondaryTable = new HashMap();
            primaryTable.put(key1, secondaryTable);
        }
        secondaryTable.put(key2, element);
    }


    /**
        Return the element indexed by <key1, key2>.
    */
    public Object get (Object key1, Object key2)
    {
        HashMap secondaryTable = (HashMap)primaryTable.get(key1);
        if (secondaryTable == null) {
            return null;
        }
        return secondaryTable.get(key2);
    }

    /**
        Remove a key from the first dimension of the grid.
        Index of <key1, ?>

        @param key1 The key to remove
        @return The removed section of the grid
    */
    public Object remove (Object key1)
    {
        return primaryTable.remove(key1);
    }

    /**
        Remove a key from the specified cell of the grid.
        Index of <key1, key2>

        @param key1 The first dimension of the grid
        @param key2 The second dimension of the grid
        @return The removed cell of the grid
    */
    public Object remove (Object key1, Object key2)
    {
        HashMap secondaryTable = (HashMap)primaryTable.get(key1);
        if (secondaryTable == null) {
            return null;
        }
        else {
            return secondaryTable.remove(key2);
        }
    }

    /**
        Clear all elements from this table.  Analogous to
        HashMap.clear().
    */
    public void clear ()
    {
        primaryTable.clear();
    }

    /** Returns the primary keys. */
    public Iterator primaryKeys ()
    {
        return primaryTable.keySet().iterator();
    }

    /** Returns the primary keys, in sorted order. */
    public Iterator sortedPrimaryKeys ()
    {
        return sortedPrimaryKeys(primaryKeyComparator);
    }

    /** Returns the primary keys, in sorted order. */
    public Iterator sortedPrimaryKeys (Comparator c)
    {
        return sortedPrimaryKeysList(c).iterator();
    }

    public List sortedPrimaryKeysList ()
    {
        return sortedPrimaryKeysList(primaryKeyComparator);
    }

    public List sortedPrimaryKeysList (Comparator c)
    {
        Object[] keys = primaryTable.keySet().toArray();
        if (c != null) {
            Arrays.sort(keys, c);
        }
        return Arrays.asList(keys);
    }

    /**
        Looks at the given list of keys to find one that matches the
        specified key, using the given Comparator. Returns
        null if there are no matches. If the Comparator is null,
        uses the "equals" method.
    */
    public static Object lookupKey (List keys, Object key, Comparator comparator)
    {
        for (int i=0, s=keys.size(); i<s; i++) {
            Object keyToCheck = keys.get(i);
            boolean equalKeys = (comparator == null) ?
                key.equals(keyToCheck) :
                (comparator.compare(key, keyToCheck) == 0);

            if (equalKeys) {
                return keyToCheck;
            }
        }
        return null;
    }

    /**
        Returns a vector containing the secondary keys, given a
        primary key.
    */
    public Iterator secondaryKeys (Object key1)
    {
        HashMap secondaryTable = (HashMap)primaryTable.get(key1);
        if (secondaryTable == null) {
            return Collections.EMPTY_SET.iterator();
        }
        return secondaryTable.keySet().iterator();
    }

    /**
        Returns an array containing the secondary keys, given a
        primary key.
    */
    public Iterator sortedSecondaryKeys (Object key1)
    {
        return sortedSecondaryKeys(key1, secondaryKeyComparator);
    }

    /**
        Returns an array containing the secondary keys, given a
        primary key.
    */
    public Iterator sortedSecondaryKeys (Object key1, Comparator c)
    {
        HashMap secondaryTable = (HashMap)primaryTable.get(key1);
        if (secondaryTable == null) {
            return Collections.EMPTY_SET.iterator();
        }
        Object[] keys = secondaryTable.keySet().toArray();
        if (c != null) {
            Arrays.sort(keys, c);
        }
        return Arrays.asList(keys).iterator();
    }

    /**
        Returns a new Gridtable, with the primary and secondary keys swapped.
    */
    public Gridtable pivot ()
    {
        Gridtable table = new Gridtable();
        table.setPrimaryKeyComparator(this.secondaryKeyComparator);
        table.setSecondaryKeyComparator(this.primaryKeyComparator);
        Iterator i = this.primaryKeys();
        while (i.hasNext()) {
            Object key1 = i.next();
            Iterator ii = this.secondaryKeys(key1);
            while (ii.hasNext()) {
                Object key2 = ii.next();
                Object element = this.get(key1, key2);
                table.put(key2, key1, element);
            }
        }
        return table;
    }
}
