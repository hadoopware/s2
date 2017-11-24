package com.syspulse.s2.index;

import java.util.ArrayList;
import java.util.Collections;
import java.util.SortedSet;

public class SortedIndexedSet<E extends Comparable<E>> extends ArrayList<E> {
    @Override
    public boolean add(E e) {
        if(0==indexOf(e)) {
            // duplicate
            //throw new IllegalAccessException("element already exists: "+indexOf(e));
            return false;
        }
        super.add(e);
        Collections.sort(this);
        return true;
    }

}
