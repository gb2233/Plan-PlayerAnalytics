/*
 * Licence is provided in the jar as license.yml also here:
 * https://github.com/Rsl1122/Plan-PlayerAnalytics/blob/master/Plan/src/main/resources/license.yml
 */
package com.djrapitops.plan.system.processing.processors;

import com.djrapitops.plan.Plan;
import com.djrapitops.plugin.api.utility.log.Log;

import java.sql.SQLException;

/**
 * Updates Command usage amount in the database.
 *
 * @author Rsl1122
 */
public class CommandProcessor extends Processor<String> {

    public CommandProcessor(String object) {
        super(object);
    }

    @Override
    public void process() {
        try {
            Plan.getInstance().getDB().getCommandUseTable().commandUsed(object);
        } catch (SQLException e) {
            Log.toLog(this.getClass().getName(), e);
        }
    }
}