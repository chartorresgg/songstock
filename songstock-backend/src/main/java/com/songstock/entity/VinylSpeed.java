package com.songstock.entity;

public enum VinylSpeed {
    RPM_33("33 RPM"),
    RPM_45("45 RPM"),
    RPM_78("78 RPM");

    private final String displayName;

    VinylSpeed(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}