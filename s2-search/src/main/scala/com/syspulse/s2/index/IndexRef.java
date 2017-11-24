package com.syspulse.s2.index;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class IndexRef implements Serializable, Comparable<IndexRef> {
    private static final long serialVersionUID = 2000L;

    public Object id; // primary key to MetaObject
    public int sortedAttrOffset; // attribute offset
    public ArrayList<Object> attrs = new ArrayList<Object>();   // values, sorted by offset

    public IndexRef(Object id, int sortedAttrOffset, List<Object> attrs ) {
        this.id = id;
        this.sortedAttrOffset = sortedAttrOffset;
        this.attrs = new ArrayList<Object>(attrs);
    }

    @Override
    public String toString() {
        return String.format("%s,%d,%s",id,sortedAttrOffset,attrs);
    }

    protected int compareObjects(Object v1,Object v2) {
        if(v1 instanceof Comparable) {
            return ((Comparable) v1).compareTo(v2);
        }
        throw new RuntimeException("not comparable");
    }

    @Override
    public int compareTo(IndexRef o) {
        int cmp = -1;

        Object v1 = attrs.get(sortedAttrOffset);
        Object v2 = o.attrs.get(sortedAttrOffset);

        try {
            //System.err.format(">>> cmp: '%s'::'%s => %d'\n",attrs.get(sortedAttrOffset),o.attrs.get(sortedAttrOffset),cmp);
            cmp = v1==v2 ? 0 :
                  v1==null ? 1:
                  v2==null ? -1:
                  compareObjects(v1,v2);

        } catch (Exception e)  {
            throw new RuntimeException(
                    String.format("unsupported type: offset=%d: '%s': %s: %s",
                            sortedAttrOffset,
                            v1,
                            null==v1 ? "null" : v1.getClass()),e);
        }

        return cmp!=0 ? cmp : id.toString().compareTo(o.id.toString());
    }
}
