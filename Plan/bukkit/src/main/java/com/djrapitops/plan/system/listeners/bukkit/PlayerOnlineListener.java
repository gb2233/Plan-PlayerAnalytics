/*
 *  This file is part of Player Analytics (Plan).
 *
 *  Plan is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License v3 as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Plan is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with Plan. If not, see <https://www.gnu.org/licenses/>.
 */
package com.djrapitops.plan.system.listeners.bukkit;

import com.djrapitops.plan.data.container.Session;
import com.djrapitops.plan.system.cache.SessionCache;
import com.djrapitops.plan.system.info.server.ServerInfo;
import com.djrapitops.plan.system.processing.Processing;
import com.djrapitops.plan.system.processing.processors.Processors;
import com.djrapitops.plan.system.settings.Settings;
import com.djrapitops.plan.system.settings.config.PlanConfig;
import com.djrapitops.plan.system.status.Status;
import com.djrapitops.plugin.logging.L;
import com.djrapitops.plugin.logging.error.ErrorHandler;
import com.djrapitops.plugin.task.RunnableFactory;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import javax.inject.Inject;
import java.net.InetAddress;
import java.util.UUID;

/**
 * Event Listener for PlayerJoin, PlayerQuit and PlayerKickEvents.
 *
 * @author Rsl1122
 * @since 2.0.0
 */
public class PlayerOnlineListener implements Listener {

    private final PlanConfig config;
    private final Processors processors;
    private final Processing processing;
    private final ServerInfo serverInfo;
    private final SessionCache sessionCache;
    private final ErrorHandler errorHandler;
    private final Status status;
    private final RunnableFactory runnableFactory;

    @Inject
    public PlayerOnlineListener(
            PlanConfig config,
            Processors processors,
            Processing processing,
            ServerInfo serverInfo,
            SessionCache sessionCache,
            Status status,
            RunnableFactory runnableFactory,
            ErrorHandler errorHandler
    ) {
        this.config = config;
        this.processors = processors;
        this.processing = processing;
        this.serverInfo = serverInfo;
        this.sessionCache = sessionCache;
        this.status = status;
        this.runnableFactory = runnableFactory;
        this.errorHandler = errorHandler;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerLogin(PlayerLoginEvent event) {
        try {
            PlayerLoginEvent.Result result = event.getResult();
            UUID uuid = event.getPlayer().getUniqueId();
            boolean op = event.getPlayer().isOp();
            boolean banned = result == PlayerLoginEvent.Result.KICK_BANNED;
            processing.submit(processors.player().banAndOpProcessor(uuid, () -> banned, op));
        } catch (Exception e) {
            errorHandler.log(L.ERROR, this.getClass(), e);
        }
    }

    /**
     * PlayerKickEvent Listener.
     * <p>
     * Adds processing information to the ProcessingQueue.
     * After KickEvent, the QuitEvent is automatically called.
     *
     * @param event Fired event
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerKick(PlayerKickEvent event) {
        try {
            if (!status.areKicksCounted() || event.isCancelled()) {
                return;
            }
            UUID uuid = event.getPlayer().getUniqueId();
            if (AFKListener.AFK_TRACKER.isAfk(uuid)) {
                return;
            }

            processing.submit(processors.player().kickProcessor(uuid));
        } catch (Exception e) {
            errorHandler.log(L.ERROR, this.getClass(), e);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        try {
            actOnJoinEvent(event);
        } catch (Exception e) {
            errorHandler.log(L.ERROR, this.getClass(), e);
        }
    }

    private void actOnJoinEvent(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // TODO Move update notification to the website.

        UUID uuid = player.getUniqueId();
        long time = System.currentTimeMillis();

        AFKListener.AFK_TRACKER.performedAction(uuid, time);

        String world = player.getWorld().getName();
        String gm = player.getGameMode().name();

        InetAddress address = player.getAddress().getAddress();

        String playerName = player.getName();
        String displayName = player.getDisplayName();

        boolean gatheringGeolocations = config.isTrue(Settings.DATA_GEOLOCATIONS);

        processing.submitCritical(() -> sessionCache.cacheSession(uuid, new Session(uuid, serverInfo.getServerUUID(), time, world, gm)));
        runnableFactory.create("Player Register: " + uuid,
                processors.player().registerProcessor(uuid, player::getFirstPlayed, playerName,
                        gatheringGeolocations ? processors.player().ipUpdateProcessor(uuid, address, time) : null,
                        processors.player().nameProcessor(uuid, playerName, displayName),
                        processors.info().playerPageUpdateProcessor(uuid)
                )
        ).runTaskAsynchronously();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        try {
            actOnQuitEvent(event);
        } catch (Exception e) {
            errorHandler.log(L.ERROR, this.getClass(), e);
        }
    }

    private void actOnQuitEvent(PlayerQuitEvent event) {
        long time = System.currentTimeMillis();
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        AFKListener.AFK_TRACKER.loggedOut(uuid, time);

        processing.submit(processors.player().banAndOpProcessor(uuid, player::isBanned, player.isOp()));
        processing.submit(processors.player().endSessionProcessor(uuid, time));
        processing.submit(processors.info().playerPageUpdateProcessor(uuid));
    }
}
