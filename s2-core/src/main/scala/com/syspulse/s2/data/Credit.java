package com.syspulse.s2.data;

import java.io.Serializable;

public class Credit  implements Serializable {
    protected String nameLast;
    protected String nameFirst;
    protected String nameExtended;

    public String getNameLast() {
        return nameLast;
    }
    public void setNameLast(String value) {
        this.nameLast = value;
    }

    public String getNameFirst() {
        return nameFirst;
    }
    public void setNameFirst(String value) {
        this.nameFirst = value;
    }

    public String getNameExtended() {
        return nameExtended;
    }
    public void setNameExtended(String value) {
        this.nameExtended = value;
    }
}
