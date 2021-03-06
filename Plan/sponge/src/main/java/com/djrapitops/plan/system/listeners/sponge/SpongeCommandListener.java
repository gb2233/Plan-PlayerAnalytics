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
package com.djrapitops.plan.system.listeners.sponge;

import com.djrapitops.plan.system.processing.Processing;
import com.djrapitops.plan.system.processing.processors.Processors;
import com.djrapitops.plan.system.settings.Permissions;
import com.djrapitops.plan.system.settings.Settings;
import com.djrapitops.plan.system.settings.config.PlanConfig;
import com.djrapitops.plugin.logging.L;
import com.djrapitops.plugin.logging.error.ErrorHandler;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandMapping;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.Order;
import org.spongepowered.api.event.command.SendCommandEvent;
import org.spongepowered.api.event.filter.cause.First;

import javax.inject.Inject;
import java.util.Optional;

/**
 * Listener that keeps track of used commands.
 *
 * @author Rsl1122
 */
public class SpongeCommandListener {

    private final PlanConfig config;
    private final Processors processors;
    private final Processing processing;
    private ErrorHandler errorHandler;

    @Inject
    public SpongeCommandListener(
            PlanConfig config,
            Processors processors,
            Processing processing,
            ErrorHandler errorHandler
    ) {
        this.config = config;
        this.processors = processors;
        this.processing = processing;
        this.errorHandler = errorHandler;
    }

    @Listener(order = Order.POST)
    public void onPlayerCommand(SendCommandEvent event, @First Player player) {
        boolean hasIgnorePermission = player.hasPermission(Permissions.IGNORE_COMMAND_USE.getPermission());
        if (event.isCancelled() || hasIgnorePermission) {
            return;
        }
        try {
            actOnCommandEvent(event);
        } catch (Exception e) {
            errorHandler.log(L.ERROR, this.getClass(), e);
        }
    }

    private void actOnCommandEvent(SendCommandEvent event) {
        String commandName = event.getCommand();

        boolean logUnknownCommands = config.isTrue(Settings.LOG_UNKNOWN_COMMANDS);
        boolean combineCommandAliases = config.isTrue(Settings.COMBINE_COMMAND_ALIASES);

        if (!logUnknownCommands || combineCommandAliases) {
            Optional<? extends CommandMapping> existingCommand = Sponge.getCommandManager().get(commandName);
            if (!existingCommand.isPresent()) {
                if (!logUnknownCommands) {
                    return;
                }
            } else if (combineCommandAliases) {
                commandName = existingCommand.get().getPrimaryAlias();
            }
        }
        processing.submit(processors.commandProcessor(commandName));
    }

}