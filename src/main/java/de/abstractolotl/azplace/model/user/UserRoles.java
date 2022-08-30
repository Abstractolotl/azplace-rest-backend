package de.abstractolotl.azplace.model.user;

public enum UserRoles {

    ANONYMOUS,
    NO_BOTS,
    STATISTICS,
    ADMIN;

    public String format(){
        return this.toString().replace("_", "").toLowerCase();
    }

}
