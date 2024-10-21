package tech.loftydev.loftyDailyRewards.enums;

public enum ItemType {

    FRAME,
    UNCLAIMED,
    CLAIMED,
    UNCLAIMABLE,
    CLAIMABLE,
    NEXT_PAGE,
    PREVIOUS_PAGE;


    public String getKey() {
        return "gui.items." + this.toString().toLowerCase();
    }

}
