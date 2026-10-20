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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

class PunishmentTest {

    private static final Instant ISSUED = Instant.parse("2026-01-01T00:00:00Z");

    private static Punishment punishment(PunishmentType type, @Nullable Instant expiresAt) {
        return new Punishment(1, type, UUID.randomUUID(), null, "reason", ISSUED, expiresAt);
    }

    @Test
    void permanentBanIsActiveForever() {
        Punishment ban = punishment(PunishmentType.BAN, null);

        assertThat(ban.isPermanent()).isTrue();
        assertThat(ban.isActiveAt(ISSUED)).isTrue();
        assertThat(ban.isActiveAt(ISSUED.plus(Duration.ofDays(10_000)))).isTrue();
        assertThat(ban.isActiveAt(ISSUED.minusSeconds(1))).isFalse();
    }

    @Test
    void temporaryBanEndsAtExpiry() {
        Instant expiry = ISSUED.plus(Duration.ofHours(1));
        Punishment ban = punishment(PunishmentType.BAN, expiry);

        assertThat(ban.isPermanent()).isFalse();
        assertThat(ban.isActiveAt(expiry.minusMillis(1))).isTrue();
        assertThat(ban.isActiveAt(expiry)).isFalse();
    }

    @Test
    void kickIsNeverActiveOrPermanent() {
        Punishment kick = punishment(PunishmentType.KICK, null);

        assertThat(kick.isPermanent()).isFalse();
        assertThat(kick.isActiveAt(ISSUED)).isFalse();
    }

    @Test
    void rejectsExpiryOnKick() {
        assertThatThrownBy(() -> punishment(PunishmentType.KICK, ISSUED.plusSeconds(60)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("KICK");
    }

    @Test
    void rejectsExpiryNotAfterIssue() {
        assertThatThrownBy(() -> punishment(PunishmentType.MUTE, ISSUED))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must be after");
    }

    @Test
    void rejectsNonPositiveId() {
        assertThatThrownBy(() -> new Punishment(0, PunishmentType.WARN, UUID.randomUUID(), null, "r", ISSUED, null))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
