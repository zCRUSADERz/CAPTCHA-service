/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 Yakovlev Alexander
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ru.yakovlev.alexander.service;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Characters range.
 * Converts character ranges to an array of characters.
 * Example of character range: '1,a,[4-9],q,[b-f]'.
 *
 * @author Yakvolev Alexander (sanyakovlev@yandex.ru)
 * @since 0.1
 */
public class CharacterRange {
    private final Pattern pattern = Pattern.compile(
        "(?<=(?:^)|(?:,))(?:([^,])|(?:\\[(.)-(.)\\]))(?=(?:,)|(?:$))"
    );

    /**
     * Array of characters for a given character range.
     *
     * @param characterRange character range ([a-z]).
     * @return array of characters.
     * @since 0.1
     */
    public char[] characters(final String characterRange) {
        if (characterRange.isBlank()) {
            throw new IllegalArgumentException(
                "Character range string must not be empty."
            );
        }
        final String[] ranges = characterRange.split(",", -1);
        final Matcher matcher = this.pattern.matcher(characterRange);
        final Set<Character> characters = new HashSet<>();
        int counter = 0;
        while (matcher.find()) {
            counter++;
            final String singleCharacter = matcher.group(1);
            if (Objects.nonNull(singleCharacter)) {
                characters.add(singleCharacter.charAt(0));
            } else {
                final String start = matcher.group(2);
                final String finish = matcher.group(3);
                char current = start.charAt(0);
                final char end = finish.charAt(0);
                while (current <= end) {
                    characters.add(current);
                    current++;
                }
            }
        }
        if (counter != ranges.length) {
            throw new IllegalArgumentException(
                String.format(
                    "Supported format: comma separated list of values - "
                        + "\"['character'-'character']\" or \"'character'\"."
                        + "%nExample: 1,[a-d],e. Your: %s",
                    characterRange
                )
            );
        }
        final char[] result = new char[characters.size()];
        int index = 0;
        for (Character character : characters) {
            result[index] = character;
            index++;
        }
        return result;
    }
}
