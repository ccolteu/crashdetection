package com.randmcnally.crashdetection.event;

public class DriveStartEvent {
    public final String trackingId;

    private DriveStartEvent(Builder builder) {
        trackingId = builder.mTrackingId;
    }

    public static final class Builder {
        private String mTrackingId;

        public DriveStartEvent build() {
            return new DriveStartEvent(this);
        }

        public Builder() {
        }

        public Builder trackingId(String trackingId) {
            mTrackingId = trackingId;
            return this;
        }
    }
}
