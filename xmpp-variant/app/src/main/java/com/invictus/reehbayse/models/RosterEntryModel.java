package com.invictus.reehbayse.models;


import org.jxmpp.jid.BareJid;


public class RosterEntryModel {

    private String name;
    private BareJid Jid;
    private String type;
    private boolean canSeeMyPresence;
    private boolean canSeeHisPresence;
    private boolean isApproved;
    private boolean isSubscriptionPending;

    public RosterEntryModel() {
    }

    public RosterEntryModel(String name, BareJid jid, String type, boolean canSeeMyPresence, boolean canSeeHisPresence, boolean isApproved, boolean isSubscriptionPending) {
        this.name = name;
        Jid = jid;
        this.type = type;
        this.canSeeMyPresence = canSeeMyPresence;
        this.canSeeHisPresence = canSeeHisPresence;
        this.isApproved = isApproved;
        this.isSubscriptionPending = isSubscriptionPending;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BareJid getJid() {
        return Jid;
    }

    public void setJid(BareJid jid) {
        Jid = jid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isCanSeeMyPresence() {
        return canSeeMyPresence;
    }

    public void setCanSeeMyPresence(boolean canSeeMyPresence) {
        this.canSeeMyPresence = canSeeMyPresence;
    }

    public boolean isCanSeeHisPresence() {
        return canSeeHisPresence;
    }

    public void setCanSeeHisPresence(boolean canSeeHisPresence) {
        this.canSeeHisPresence = canSeeHisPresence;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public boolean isSubscriptionPending() {
        return isSubscriptionPending;
    }

    public void setSubscriptionPending(boolean subscriptionPending) {
        isSubscriptionPending = subscriptionPending;
    }
}
