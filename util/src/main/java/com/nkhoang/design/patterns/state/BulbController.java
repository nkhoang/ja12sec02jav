package com.nkhoang.design.patterns.state;

public class BulbController {
    enum BulbState {
        ON,
        OFF
    }

    private BulbState currentBulbState = BulbState.OFF;

    public String getBulbState() {
        return currentBulbState.name();
    }

    public void pressSwitch() {
        switch (currentBulbState) {
            case ON:
                currentBulbState = BulbState.OFF;
                break;
            case OFF:
                currentBulbState = BulbState.ON;
                break;
        }
    }
}
