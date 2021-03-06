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
package com.djrapitops.plan.system.webserver.auth;

import com.djrapitops.plan.system.locale.lang.Lang;

/**
 * Reason for WebUserAuthException.
 *
 * @author Rsl1122
 * @see com.djrapitops.plan.api.exceptions.WebUserAuthException
 */
public enum FailReason implements Lang {
    USER_AND_PASS_NOT_SPECIFIED("User and Password not specified"),
    USER_DOES_NOT_EXIST("User does not exist"),
    USER_PASS_MISMATCH("User and Password did not match"),
    ERROR("Authentication failed due to error");

    private final String reason;

    FailReason(String reason) {
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String getIdentifier() {
        return "HTML - " + name();
    }

    @Override
    public String getDefault() {
        return getReason();
    }
}
