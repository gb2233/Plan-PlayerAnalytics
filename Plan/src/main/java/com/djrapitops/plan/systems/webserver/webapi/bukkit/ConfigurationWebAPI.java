/* 
 * Licence is provided in the jar as license.yml also here:
 * https://github.com/Rsl1122/Plan-PlayerAnalytics/blob/master/Plan/src/main/resources/license.yml
 */
package main.java.com.djrapitops.plan.systems.webserver.webapi.bukkit;

import com.djrapitops.plugin.utilities.Compatibility;
import main.java.com.djrapitops.plan.Log;
import main.java.com.djrapitops.plan.Plan;
import main.java.com.djrapitops.plan.ServerSpecificSettings;
import main.java.com.djrapitops.plan.Settings;
import main.java.com.djrapitops.plan.api.IPlan;
import main.java.com.djrapitops.plan.api.exceptions.WebAPIException;
import main.java.com.djrapitops.plan.systems.webserver.response.Response;
import main.java.com.djrapitops.plan.systems.webserver.webapi.WebAPI;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Fuzzlemann
 */
public class ConfigurationWebAPI extends WebAPI {

    @Override
    public Response onRequest(IPlan plugin, Map<String, String> variables) {
        if (!Compatibility.isBukkitAvailable()) {
            return badRequest("Called a Bungee Server");
        }
        if (Settings.BUNGEE_COPY_CONFIG.isFalse() || Settings.BUNGEE_OVERRIDE_STANDALONE_MODE.isTrue()) {
            Log.debug("Bungee Config settings overridden on this server.");
            return success();
        }
        variables.remove("sender");
        Settings.serverSpecific().updateSettings((Plan) plugin, variables);
        return success();
    }

    @Override
    public void sendRequest(String address) throws WebAPIException {
        throw new IllegalStateException("Wrong method call for this WebAPI, call sendRequest(String, UUID, UUID) instead.");
    }

    public void sendRequest(String address, UUID serverUUID) throws WebAPIException {
        Map<String, Object> configValues = getConfigValues(serverUUID);
        for (Map.Entry<String, Object> entry : configValues.entrySet()) {
            addVariable(entry.getKey(), entry.getValue().toString());
        }
        super.sendRequest(address);
    }

    private void addConfigValue(Map<String, Object> configValues, Settings setting, Object value) {
        configValues.put(setting.getPath(), value);
    }

    private Map<String, Object> getConfigValues(UUID serverUUID) throws WebAPIException {
        Map<String, Object> configValues = new HashMap<>();
        if (!Compatibility.isBungeeAvailable()) {
            throw new WebAPIException("Attempted to send config values from Bukkit to Bungee.");
        }
        addConfigValue(configValues, Settings.DB_TYPE, "mysql");
        Settings[] sameStrings = new Settings[]{
                Settings.DB_HOST, Settings.DB_USER, Settings.DB_PASS,
                Settings.DB_DATABASE, Settings.FORMAT_DECIMALS, Settings.FORMAT_SECONDS,
                Settings.FORMAT_DAY, Settings.FORMAT_DAYS, Settings.FORMAT_HOURS,
                Settings.FORMAT_MINUTES, Settings.FORMAT_MONTHS, Settings.FORMAT_MONTH,
                Settings.FORMAT_YEAR, Settings.FORMAT_YEARS,
        };
        for (Settings setting : sameStrings) {
            addConfigValue(configValues, setting, setting.toString());
        }
        addConfigValue(configValues, Settings.DB_PORT, Settings.DB_PORT.getNumber());
        addServerSpecificValues(configValues, serverUUID);

        return configValues;
    }

    private void addServerSpecificValues(Map<String, Object> configValues, UUID serverUUID) {
        ServerSpecificSettings settings = Settings.serverSpecific();
        addConfigValue(configValues, Settings.THEME_BASE, settings.getString(serverUUID, Settings.THEME_BASE));
        addConfigValue(configValues, Settings.WEBSERVER_PORT, settings.getInt(serverUUID, Settings.WEBSERVER_PORT));
        addConfigValue(configValues, Settings.SERVER_NAME, settings.getString(serverUUID, Settings.SERVER_NAME));
    }
}