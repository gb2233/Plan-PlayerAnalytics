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
package com.djrapitops.plan.system.database.databases;

import java.util.Optional;

/**
 * An enum which stores the name, the config name and if the Database supports MySQL Queries
 *
 * @author Fuzzlemann
 * @since 4.5.1
 */
public enum DBType {

    MYSQL("MySQL", true),
    SQLITE("SQLite", false),
    H2("H2", true);

    private final String name;
    private final String configName;
    private final boolean supportingMySQLQueries;

    DBType(String name, boolean supportingMySQLQueries) {
        this.name = name;
        this.configName = name.toLowerCase().trim();
        this.supportingMySQLQueries = supportingMySQLQueries;
    }

    /**
     * Gets the name of the {@code DBType}
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the config name of the {@code DBType}
     *
     * @return the config name
     */
    public String getConfigName() {
        return configName;
    }

    /**
     * Used to check if the {@code DBType} supports <b>most</b> MySQL Queries.<p>
     * When specific Statements are not compatible, the {@code DBType} should be checked.
     *
     * @return if the database supports MySQL Queries
     */
    public boolean supportsMySQLQueries() {
        return supportingMySQLQueries;
    }

    /**
     * Gets an {@code Optional<DBType>} which matches {@code name}.<p>
     * This method is case-insensitive.<p>
     * The {@code Optional<DBType>} is empty when no {@code DBType} is found.
     *
     * @param name the name of the {@code DBType}
     * @return an {@code Optional<DBType>}
     */
    public static Optional<DBType> getForName(String name) {
        for (DBType dbType : DBType.values()) {
            if (dbType.getName().equalsIgnoreCase(name)) {
                return Optional.of(dbType);
            }
        }

        return Optional.empty();
    }

    /**
     * Checks if the name of a {@code DBType} corresponds to {@code name}.<p>
     * This method is case-insensitive.
     *
     * @param name the name of the {@code DBType}
     * @return if the {@code DBType} exists
     * @see DBType#getForName(String)
     */
    public static boolean exists(String name) {
        for (DBType dbType : DBType.values()) {
            if (dbType.getName().equalsIgnoreCase(name)) {
                return true;
            }
        }

        return false;
    }
}
