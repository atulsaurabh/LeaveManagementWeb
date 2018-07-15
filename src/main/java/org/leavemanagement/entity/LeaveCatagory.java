package org.leavemanagement.entity;

import javax.persistence.Column;
import javax.persistence.Entity;

@Entity
public class LeaveCatagory extends Catagory
{
    private boolean carryForwarded;
    private boolean accumulated;
    private float commonvalue;
    public boolean isCarryForwarded() {
        return carryForwarded;
    }

    public void setCarryForwarded(boolean carryForwarded) {
        this.carryForwarded = carryForwarded;
    }

    public boolean isAccumulated() {
        return accumulated;
    }

    public void setAccumulated(boolean accumulated) {
        this.accumulated = accumulated;
    }

    @Column(name = "default_value")
    public float getCommonvalue() {
        return commonvalue;
    }

    public void setCommonvalue(float commonvalue) {
        this.commonvalue = commonvalue;
    }
}
