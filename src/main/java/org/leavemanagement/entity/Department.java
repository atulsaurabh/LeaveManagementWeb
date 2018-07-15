package org.leavemanagement.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Department
{
    private int departmentid;
    private String departmentname;
    private String departmentabbriviation;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getDepartmentid() {
        return departmentid;
    }

    public void setDepartmentid(int departmentid) {
        this.departmentid = departmentid;
    }

    public String getDepartmentname() {
        return departmentname;
    }

    public void setDepartmentname(String departmentname) {
        this.departmentname = departmentname;
    }

    public String getDepartmentabbriviation() {
        return departmentabbriviation;
    }

    public void setDepartmentabbriviation(String departmentabbriviation) {
        this.departmentabbriviation = departmentabbriviation;
    }
}
