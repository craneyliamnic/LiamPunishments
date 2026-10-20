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

import java.time.Duration;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jspecify.annotations.Nullable;

/**
 * Parses and formats compact durations such as {@code 1d12h} or {@code 2w}.
 *
 * <p>Supported units: {@code y} (365 days), {@code mo} (30 days), {@code w}, {@code d}, {@code h}, {@code m},
 * {@code s}. Units are case-insensitive and must appear at most once each, in descending order.
 */
public final class DurationParser {

    /**
     * The longest duration accepted by {@link #parse(String)}. Anything beyond this is effectively permanent and
     * risks overflowing timestamps once added to the current time; issue a permanent punishment instead.
     */
    public static final Duration MAXIMUM = Duration.ofDays(365L * 1000);

    private static final Pattern SEGMENT = Pattern.compile("(\\d+)(mo|[ywdhms])");

    private DurationParser() {}

    /**
     * Parses a compact duration.
     *
     * @throws IllegalArgumentException if the input is empty, malformed, zero, or longer than {@link #MAXIMUM}
     */
    public static Duration parse(String input) {
        Objects.requireNonNull(input, "input");
        String normalized = input.strip().toLowerCase(Locale.ROOT);
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Duration must not be empty");
        }

        Matcher matcher = SEGMENT.matcher(normalized);
        @Nullable Unit previous = null;
        Duration total = Duration.ZERO;
        int position = 0;
        while (position < normalized.length()) {
            if (!matcher.find(position) || matcher.start() != position) {
                throw new IllegalArgumentException(
                        "Invalid duration '" + input + "' at position " + position + "; expected e.g. 1d12h");
            }
            Unit unit = Unit.fromSymbol(matcher.group(2));
            if (previous != null && unit.ordinal() <= previous.ordinal()) {
                throw new IllegalArgumentException(
                        "Units in '" + input + "' must each appear once, from largest to smallest");
            }
            previous = unit;
            try {
                long amount = Long.parseLong(matcher.group(1));
                total = total.plus(unit.length.multipliedBy(amount));
            } catch (NumberFormatException | ArithmeticException e) {
                throw new IllegalArgumentException("Duration '" + input + "' is too large", e);
            }
            if (total.compareTo(MAXIMUM) > 0) {
                throw new IllegalArgumentException(
                        "Duration '" + input + "' is too large; the maximum is " + format(MAXIMUM));
            }
            position = matcher.end();
        }

        if (total.isZero()) {
            throw new IllegalArgumentException("Duration '" + input + "' must be greater than zero");
        }
        return total;
    }

    /**
     * Formats a duration in the same notation, separated by spaces, e.g. {@code 1d 12h}. Sub-second parts are
     * dropped.
     *
     * @throws IllegalArgumentException if the duration is negative
     */
    public static String format(Duration duration) {
        Objects.requireNonNull(duration, "duration");
        if (duration.isNegative()) {
            throw new IllegalArgumentException("Duration must not be negative: " + duration);
        }
        long remaining = duration.toSeconds();
        if (remaining == 0) {
            return "0s";
        }
        StringBuilder builder = new StringBuilder();
        for (Unit unit : Unit.values()) {
            long unitSeconds = unit.length.toSeconds();
            long amount = remaining / unitSeconds;
            if (amount > 0) {
                if (!builder.isEmpty()) {
                    builder.append(' ');
                }
                builder.append(amount).append(unit.symbol);
                remaining -= amount * unitSeconds;
            }
        }
        return builder.toString();
    }

    /** Units ordered from largest to smallest. */
    private enum Unit {
        YEARS("y", Duration.ofDays(365)),
        MONTHS("mo", Duration.ofDays(30)),
        WEEKS("w", Duration.ofDays(7)),
        DAYS("d", Duration.ofDays(1)),
        HOURS("h", Duration.ofHours(1)),
        MINUTES("m", Duration.ofMinutes(1)),
        SECONDS("s", Duration.ofSeconds(1));

        private final String symbol;
        private final Duration length;

        Unit(String symbol, Duration length) {
            this.symbol = symbol;
            this.length = length;
        }

        static Unit fromSymbol(String symbol) {
            for (Unit unit : values()) {
                if (unit.symbol.equals(symbol)) {
                    return unit;
                }
            }
            throw new IllegalStateException("Pattern matched unknown unit '" + symbol + "'");
        }
    }
}
