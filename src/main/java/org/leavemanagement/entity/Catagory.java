package org.leavemanagement.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;


@Entity
public abstract class Catagory {

    private int catagoryid;
    private String catagoryname;
    private String abbriviation;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getCatagoryid() {
        return catagoryid;
    }

    public void setCatagoryid(int catagoryid) {
        this.catagoryid = catagoryid;
    }

    public String getCatagoryname() {
        return catagoryname;
    }

    public void setCatagoryname(String catagoryname) {
        this.catagoryname = catagoryname;
    }

    public String getAbbriviation() {
        return abbriviation;
    }

    public void setAbbriviation(String abbriviation) {
        this.abbriviation = abbriviation;
    }
}
