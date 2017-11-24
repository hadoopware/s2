package com.syspulse.s2.index;

import com.googlecode.javaewah.EWAHCompressedBitmap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class IndexFilterBitmap<T> implements Serializable {
    private static final long serialVersionUID = 3002L;

    public int attrOffset;

    // filter value
    public T value;

    // bitmap size
    public EWAHCompressedBitmap filterBitmap; //= new EWAHCompressedBitmap(IndexPageBitmatrix.PAGE_SIZE);

    public IndexFilterBitmap(int attrOffset,T value, EWAHCompressedBitmap filterBitmap ) {
        this.attrOffset = attrOffset;
        this.value = value;
        this.filterBitmap = filterBitmap;
    }

    @Override
    public String toString() {
        return String.format("%d:%s->[%s]",attrOffset,value,filterBitmap);
    }
}
