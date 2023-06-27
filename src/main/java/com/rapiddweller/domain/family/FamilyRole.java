package com.rapiddweller.domain.family;

public enum FamilyRole {
    FATHER("father"),
    MOTHER("mother"),
    SON("son"),
    DAUGHTER("daughter");
    private final String label;

    private FamilyRole(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
