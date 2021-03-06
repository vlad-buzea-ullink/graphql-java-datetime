/*
 *  Copyright 2017 Alexey Zhokhov
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.zhokhov.graphql.datetime;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static com.zhokhov.graphql.datetime.DateTimeHelper.DATE_FORMATTERS;

/**
 * @author <a href='mailto:alexey@zhokhov.com'>Alexey Zhokhov</a>
 */
class LocalDateTimeConverter {

    private final boolean zoneConversionEnabled;

    LocalDateTimeConverter(boolean zoneConversionEnabled) {
        this.zoneConversionEnabled = zoneConversionEnabled;
    }

    // ISO_8601
    String toISOString(LocalDateTime dateTime) {
        Objects.requireNonNull(dateTime, "dateTime");

        return DateTimeFormatter.ISO_INSTANT.format(ZonedDateTime.of(toUTC(dateTime), ZoneOffset.UTC));
    }

    LocalDateTime parseDate(String date) {
        Objects.requireNonNull(date, "date");
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            // try to parse as dateTime
            try {
                LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
                return fromUTC(dateTime);
            } catch (java.time.format.DateTimeParseException ignored) {}

            // try to parse as date
            try {
                // equals ISO_LOCAL_DATE or custom date format
                LocalDate localDate = LocalDate.parse(date, formatter);
                return localDate.atStartOfDay();
            } catch (java.time.format.DateTimeParseException ignored) {}
        }

        return null;
    }

    private LocalDateTime convert(LocalDateTime dateTime, ZoneId from, ZoneId to) {
        if (zoneConversionEnabled) {
            return dateTime.atZone(from).withZoneSameInstant(to).toLocalDateTime();
        }
        return dateTime;
    }

    private LocalDateTime fromUTC(LocalDateTime dateTime) {
        return convert(dateTime, ZoneOffset.UTC, ZoneId.systemDefault());
    }

    private LocalDateTime toUTC(LocalDateTime dateTime) {
        return convert(dateTime, ZoneId.systemDefault(), ZoneOffset.UTC);
    }

}
