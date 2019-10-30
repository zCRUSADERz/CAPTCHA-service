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

import org.junit.jupiter.api.Test;
import ru.yakovlev.alexander.util.BoxedCharacters;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayContainingInAnyOrder;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Character range test.
 *
 * @author Yakovlev Alexander (sanyakovlev@yandex.ru)
 * @since 0.1
 */
class CharacterRangeTest {
    private final CharacterRange characterRange = new CharacterRange();

    @Test
    void whenSingleCharacterThenReturnSameCharacter() {
        final char character = 'a';
        final char[] characters = this.characterRange.characters(
            Character.toString(character)
        );
        assertArrayEquals(new char[]{character}, characters);
    }

    @Test
    void whenSeveralCharactersCommaSeparatedThenReturnSameCharacters() {
        final String range = "1,2,3,4";
        final char[] characters = this.characterRange.characters(range);
        assertArrayEquals(new char[]{'1','2','3','4'}, characters);
    }

    @Test
    void whenSingleRangeThenReturnCharactersFromThisRangeInclusive() {
        final String range = "[a-d]";
        final char[] characters = this.characterRange.characters(range);
        assertArrayEquals(new char[]{'a','b','c','d'}, characters);
    }

    @Test
    void whenSingleCharactersWithRangesThenReturnThisCharacters() {
        final String range = "[a-c],1,[e-g],0,9";
        final char[] characters = this.characterRange.characters(range);
        final Character[] boxedCharacters = new BoxedCharacters(characters)
            .chars();
        assertThat(
            boxedCharacters,
            arrayContainingInAnyOrder('a', 'b', 'c', '1', 'e', 'f', 'g', '0', '9')
        );
    }

    @Test
    void whenEmptyRangeThenThrowException() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.characterRange.characters("     ")
        );
    }

    @Test
    void whenCommaWithoutCharactersThenThrowException() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.characterRange.characters(",")
        );
    }

    @Test
    void whenRangeWithoutCharactersThenThrowException() {
        assertThrows(
            IllegalArgumentException.class,
            () -> this.characterRange.characters("[-]")
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> this.characterRange.characters("[a-]")
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> this.characterRange.characters("[-b]")
        );
    }
}