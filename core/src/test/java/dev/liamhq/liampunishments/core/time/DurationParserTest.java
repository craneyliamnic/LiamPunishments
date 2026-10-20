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
package dev.liamhq.liampunishments.core.time;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

class DurationParserTest {

    @ParameterizedTest
    @CsvSource({
        "30s, PT30S",
        "5m, PT5M",
        "2h, PT2H",
        "1d12h, PT36H",
        "1w, PT168H",
        "1mo, PT720H",
        "1y, PT8760H",
        "1D2H3M4S, PT26H3M4S",
        "' 10m ', PT10M"
    })
    void parsesValidDurations(String input, Duration expected) {
        assertThat(DurationParser.parse(input)).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "abc", "10", "10x", "d", "1d 2h", "-1d", "1h1d", "1d1d", "0s", "0d0h"})
    void rejectsInvalidDurations(String input) {
        assertThatThrownBy(() -> DurationParser.parse(input)).isInstanceOf(IllegalArgumentException.class);
    }

    @ParameterizedTest
    @ValueSource(strings = {"99999999999999999999y", "9223372036854775807y", "300000000y", "1001y", "1000y1s"})
    void rejectsOverflow(String input) {
        assertThatThrownBy(() -> DurationParser.parse(input))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("too large");
    }

    @Test
    void acceptsMaximum() {
        assertThat(DurationParser.parse("1000y")).isEqualTo(DurationParser.MAXIMUM);
    }

    @ParameterizedTest
    @CsvSource({"PT0S, 0s", "PT30S, 30s", "PT36H, 1d 12h", "PT26H3M4S, 1d 2h 3m 4s", "PT0.9S, 0s"})
    void formatsDurations(Duration input, String expected) {
        assertThat(DurationParser.format(input)).isEqualTo(expected);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1y2mo3w4d5h6m7s", "1d12h", "45s"})
    void formatRoundTripsThroughParse(String input) {
        Duration parsed = DurationParser.parse(input);

        assertThat(DurationParser.parse(DurationParser.format(parsed).replace(" ", "")))
                .isEqualTo(parsed);
    }

    @Test
    void rejectsNegativeFormat() {
        assertThatThrownBy(() -> DurationParser.format(Duration.ofSeconds(-1)))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
