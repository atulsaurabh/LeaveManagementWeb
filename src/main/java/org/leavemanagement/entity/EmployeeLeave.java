package org.leavemanagement.entity;

import javax.persistence.*;

@Entity
public class EmployeeLeave
{
    private int leaveid;
    private LeaveCatagory leaveCatagory;
    private float totalApplicable;
    private float balance;


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int getLeaveid() {
        return leaveid;
    }

    public void setLeaveid(int leaveid) {
        this.leaveid = leaveid;
    }

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "emp_leave")
    public LeaveCatagory getLeaveCatagory() {
        return leaveCatagory;
    }

    public void setLeaveCatagory(LeaveCatagory leaveCatagory) {
        this.leaveCatagory = leaveCatagory;
    }

    public float getTotalApplicable() {
        return totalApplicable;
    }

    public void setTotalApplicable(float totalApplicable) {
        this.totalApplicable = totalApplicable;
    }

    public float getBalance() {
        return balance;
    }

    public void setBalance(float balance) {
        this.balance = balance;
    }

}
