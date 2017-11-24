package com.syspulse.s2.index;

import java.io.Serializable;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

public class IndexPage implements Serializable {
    private static final long serialVersionUID = 1003L;
    //public SortedSet<IndexRef> refs = new ConcurrentSkipListSet<IndexRef>();
    public SortedIndexedSet<IndexRef> refs = new SortedIndexedSet<IndexRef>();
    protected int pageId;      // page id (used for Partitioned cache as a key suffix)

    public IndexPage(int pageId) {
        this.pageId = pageId;
    }

    public int size() {
        return refs.size();
    }

    public int getPageId() {
        return pageId;
    }

    public IndexRef getRef(int offset) {
        return refs.get(offset);
    }

    @Override
    public String toString() {
        return String.format("%d:(%d):%s",pageId,refs.size(),refs);
    }
}
