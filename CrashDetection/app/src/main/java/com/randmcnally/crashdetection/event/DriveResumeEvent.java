package com.randmcnally.crashdetection.event;

public class DriveResumeEvent {
    public final String trackingId;

    private DriveResumeEvent(Builder builder) {
        trackingId = builder.mTrackingId;
    }

    public static final class Builder {
        private String mTrackingId;

        public DriveResumeEvent build() {
            return new DriveResumeEvent(this);
        }

        public Builder() {
        }

        public Builder trackingId(String trackingId) {
            mTrackingId = trackingId;
            return this;
        }
    }
}
