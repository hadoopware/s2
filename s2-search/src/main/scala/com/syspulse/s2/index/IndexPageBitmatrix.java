package com.syspulse.s2.index;

import com.googlecode.javaewah.EWAHCompressedBitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

public class IndexPageBitmatrix extends IndexPage {
    private static final long serialVersionUID = 3001L;

    public final static int PAGE_SIZE = 100;
    public final static int PAGE_FILTERS = 100;


    // BitMap matrix for many filters
    public List<IndexFilterBitmap<?>> filterMatrix; //

    public IndexPageBitmatrix(int pageId,List<IndexFilterBitmap<?>> filterMatrix) {
        super(pageId);
        // initialize filterMatrix
        this.filterMatrix = filterMatrix;
    }

    public IndexPageBitmatrix(int pageId) {
        super(pageId);
        // initialize filterMatrix
        this.filterMatrix = new ArrayList<IndexFilterBitmap<?>>(PAGE_FILTERS);
    }

    public <T> IndexFilterBitmap<T> findBitmap(int attrOffset,T filterValue) {
        for(IndexFilterBitmap ifb : filterMatrix) {
            if(ifb.attrOffset == attrOffset && filterValue == ifb.value)
                return ifb;
        }
        return null;
    }

    @Override
    public String toString() {
        return String.format("%d:(%d):%s:%s",pageId,refs.size(),refs,filterMatrix);
    }
}
