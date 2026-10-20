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

/** The kinds of punishment that can be issued against a player. */
public enum PunishmentType {
    /** Prevents the player from joining. May be permanent or temporary. */
    BAN(true),
    /** Prevents the player from chatting. May be permanent or temporary. */
    MUTE(true),
    /** A recorded warning. May expire so it stops counting towards escalation. */
    WARN(true),
    /** Removes the player from the server once. Has no duration. */
    KICK(false);

    private final boolean hasDuration;

    PunishmentType(boolean hasDuration) {
        this.hasDuration = hasDuration;
    }

    /** Whether punishments of this type stay in effect over a period of time. */
    public boolean hasDuration() {
        return hasDuration;
    }
}
