package com.syspulse.s2.index;

import java.io.Serializable;

public class IndexInfo implements Serializable{
    private static final long serialVersionUID = 1001L;
    public int pageSize = 0;
    public int numPages = 0;

    public final static String KEY = "INDEX-INFO";

    public IndexInfo(int pageSize,int numPages) {
        this.numPages = numPages;
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return String.format("%d,%d",pageSize,numPages);
    }

    public static String getKey(String suffix) {
        return KEY+":"+suffix;
    }
}
