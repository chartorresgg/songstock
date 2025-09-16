package com.songstock.entity;

public enum VinylSize {
    SEVEN_INCH("7\""),
    TEN_INCH("10\""),
    TWELVE_INCH("12\"");

    private final String displayName;

    VinylSize(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}