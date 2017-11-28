package org.leavemanagement.entity;

import javax.persistence.Entity;

@Entity
public class LeaveCatagory extends Catagory
{
    private boolean carryForwarded;
    private boolean accumulated;

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
}
