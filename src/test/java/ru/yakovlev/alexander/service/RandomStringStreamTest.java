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

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import ru.yakovlev.alexander.util.BoxedCharacters;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isIn;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Random string stream test.
 *
 * @author Yakovlev Alexander (sanyakovlev@yandex.ru)
 * @since 0.1
 */
class RandomStringStreamTest {
    private final SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

    RandomStringStreamTest() throws NoSuchAlgorithmException {
    }

    @Test
    void whenCreateStreamWithCaptchaLengthLessThanOneThenThrowException() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new RandomStringStream(0, new char[]{'a'}, this.random)
        );
        assertThrows(
            IllegalArgumentException.class,
            () -> new RandomStringStream(-1, new char[]{'b'}, this.random)
        );
    }

    @Test
    void whenCreateStreamWithEmptyArrayOfCharsThenThrowException() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new RandomStringStream(1, new char[0], this.random)
        );
    }

    @Test
    void whenCallNextThenReturnStringWithCertainLength() {
        final int length = 5;
        final RandomStringStream stream = new RandomStringStream(
            length, new char[]{'a', 'b', 'c'}, this.random
        );
        final String captcha = stream.next();
        final int actual = captcha.length();
        assertEquals(length, actual);
    }

    @Test
    void whenCallNextThenReturnStringWithCharsPassedInConstructor() {
        final char[] chars = {'a', 'b', 'c'};
        final RandomStringStream stream = new RandomStringStream(
            100, chars, this.random
        );
        final Collection<Character> expected = Arrays.asList(
            new BoxedCharacters(chars).chars()
        );
        final String captcha = stream.next();
        captcha
            .chars()
            .mapToObj((ch) -> (char) ch)
            .collect(Collectors.toSet())
            .forEach(
                (ch) -> assertThat(ch, isIn(expected))
            );
    }

    @Test
    void multipleNextMethodCallReturnRandomStrings() {
        final Set<String> captchaSet = new HashSet<>();
        final RandomStringStream stream = new RandomStringStream(
            100,
            new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h'},
            this.random
        );
        for (int i = 0; i < 10; i++) {
            final String next = stream.next();
            final boolean added = captchaSet.add(next);
            assertTrue(added);
        }
    }
}