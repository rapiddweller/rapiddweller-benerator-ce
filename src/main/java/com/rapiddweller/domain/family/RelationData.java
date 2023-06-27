package com.rapiddweller.domain.family;

public class RelationData {
    private long familyID;
    private FamilyRole familyRole;

    public RelationData() {
    }

    public long getFamilyID() {
        return familyID;
    }

    public void setFamilyID(long familyID) {
        this.familyID = familyID;
    }

    public FamilyRole getFamilyRole() {
        return familyRole;
    }

    public void setFamilyRole(FamilyRole familyRole) {
        this.familyRole = familyRole;
    }
}
