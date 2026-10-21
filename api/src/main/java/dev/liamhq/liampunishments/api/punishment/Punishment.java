/*
 * LiamPunishments - a modern punishment plugin for Paper, Folia and Velocity.
 * Copyright (C) 2026 Liam Nic Craney (compileOnlyLiam) <https://liamhq.dev>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package dev.liamhq.liampunishments.api.punishment;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import org.jspecify.annotations.Nullable;

public record Punishment(
        long id,
        PunishmentType type,
        UUID target,
        @Nullable UUID operator,
        String reason,
        Instant issuedAt,
        @Nullable Instant expiresAt) {

    public Punishment {
        if (id <= 0) {
            throw new IllegalArgumentException("id must be positive, was " + id);
        }
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(target, "target");
        Objects.requireNonNull(reason, "reason");
        Objects.requireNonNull(issuedAt, "issuedAt");
        if (expiresAt != null) {
            if (!type.hasDuration()) {
                throw new IllegalArgumentException(type + " punishments cannot have an expiry");
            }
            if (!expiresAt.isAfter(issuedAt)) {
                throw new IllegalArgumentException(
                        "expiresAt (" + expiresAt + ") must be after issuedAt (" + issuedAt + ")");
            }
        }
    }

    public boolean isPermanent() {
        return type.hasDuration() && expiresAt == null;
    }

    public boolean isActiveAt(Instant instant) {
        Objects.requireNonNull(instant, "instant");
        if (!type.hasDuration() || instant.isBefore(issuedAt)) {
            return false;
        }
        return expiresAt == null || instant.isBefore(expiresAt);
    }
}
