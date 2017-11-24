package com.syspulse.s2.index;

import java.io.Serializable;

public class IndexKey implements Serializable{
    private static final long serialVersionUID = 1002L;
    private String sortedAttrName;         // encoding of the sorted attributes
    private String[] filterAttrNames;       // filters list
    private Object[] filterAttrValues;       // filters list
    private int pageIndex;

    public IndexKey(int pageIndex, String sortedAttrName, String[] filterAttrNames,Object[] filterAttrValues) {
        this.pageIndex = pageIndex;
        this.sortedAttrName = sortedAttrName;
        this.filterAttrNames = filterAttrNames;
        this.filterAttrValues = filterAttrValues;
    }

    public IndexKey(int pageIndex,String sortedAttrName) {
        this(pageIndex,sortedAttrName,new String[]{},new Object[]{});
    }

    public String getKey() {
        StringBuilder sb = new StringBuilder();

        sb.append(pageIndex);
        sb.append("-");

        sb.append(sortedAttrName);

        if(filterAttrNames.length>0) {
            sb.append(':');
        }
        for(int i=0; i<filterAttrNames.length; i++) {
            sb.append(filterAttrNames[i]);
            sb.append('=');
            sb.append(filterAttrValues[i]);
            if(i!=filterAttrNames.length-1)
                sb.append(",");
        }


        return sb.toString();
    }

    @Override
    public String toString() {
        return getKey();
    }
}
