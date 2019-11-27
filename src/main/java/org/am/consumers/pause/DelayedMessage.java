package org.am.consumers.pause;

import java.io.Serializable;
import java.time.Instant;

class DelayedMessage implements Serializable {

    private Instant validAfter;

    public Instant getValidAfter() {
        return validAfter;
    }

    public DelayedMessage(Instant validAfter) {
        this.validAfter = validAfter;
    }
}
