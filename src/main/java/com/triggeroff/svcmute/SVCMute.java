package com.triggeroff.svcmute;

import litebans.api.*;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public final class SVCMute extends JavaPlugin {

    @Override
    public void onEnable() {
        registerEvents();
    }

    public void registerEvents() {
        Events.get().register(new Events.Listener() {
            @Override
            public void entryAdded(Entry entry) {
                if ("mute".equals(entry.getType()) && entry.getUuid() != null) {
                    // Мут выдан - забираем право
                    Bukkit.getScheduler().runTask(SVCMute.this, () -> {
                        updateLuckPerms(entry.getUuid(), false);
                    });
                }
            }

            @Override
            public void entryRemoved(Entry entry) {
                if ("mute".equals(entry.getType()) && entry.getUuid() != null) {
                    // Мут снят - возвращаем право
                    Bukkit.getScheduler().runTask(SVCMute.this, () -> {
                        updateLuckPerms(entry.getUuid(), true);
                    });
                }
            }
        });
    }

    private void updateLuckPerms(String uuidString, boolean allowSpeak) {
        try {
            UUID uuid = UUID.fromString(uuidString);

            if (!allowSpeak) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + uuid.toString() + " permission set voicechat.speak false");
            } else {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "lp user " + uuid.toString() + " permission unset voicechat.speak");
            }

        } catch (IllegalArgumentException e) {
            getLogger().warning("Invalid UUID format from LiteBans: " + uuidString);
        }
    }
}