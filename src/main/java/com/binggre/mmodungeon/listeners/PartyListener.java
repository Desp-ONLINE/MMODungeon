package com.binggre.mmodungeon.listeners;

import net.Indyuce.mmocore.api.event.social.PartyChatEvent;
import net.Indyuce.mmocore.party.provided.Party;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PartyListener implements Listener {



    @EventHandler
    public void onPartyInvite(PartyChatEvent event) {
        Party party = event.getParty();
    }
}