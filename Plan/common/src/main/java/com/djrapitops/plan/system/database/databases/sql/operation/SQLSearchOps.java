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
package com.djrapitops.plan.system.database.databases.sql.operation;

import com.djrapitops.plan.system.database.databases.operation.SearchOperations;
import com.djrapitops.plan.system.database.databases.sql.SQLDB;

import java.util.Collections;
import java.util.List;

public class SQLSearchOps extends SQLOps implements SearchOperations {

    public SQLSearchOps(SQLDB db) {
        super(db);
    }

    @Override
    public List<String> matchingPlayers(String search) {
        List<String> matchingNames = usersTable.getMatchingNames(search);
        Collections.sort(matchingNames);
        return matchingNames;
    }
}
