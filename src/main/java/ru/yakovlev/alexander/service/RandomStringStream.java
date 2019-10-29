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

import java.security.SecureRandom;

/**
 * Endless stream of random strings.
 *
 * @author Yakovlev Aleander (sanyakovlev@yandex.ru)
 * @since 0.1
 */
public class RandomStringStream {
    private final int length;

    /**
     * Array of characters from which the string will be built.
     */
    private final char[] chars;
    private final SecureRandom secureRandom;

    /**
     * Primary constructor.
     * @param length result string length.
     * @param chars array of characters from which the string will be built.
     * @param secureRandom secure random.
     */
    public RandomStringStream(
        final int length, final char[] chars,
        final SecureRandom secureRandom
    ) {
        if (length <= 0) {
            throw new IllegalArgumentException(
                "Length must be greater than zero."
            );
        }
        if (chars.length == 0) {
            throw new IllegalArgumentException(
                "An array of characters must contain at least one character"
            );
        }
        this.length = length;
        this.chars = chars;
        this.secureRandom = secureRandom;
    }

    /**
     * Generates a new random string for each call.
     *
     * @return random string.
     * @since 0.1
     */
    public String next() {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < this.length; i++) {
            final int index = this.secureRandom.nextInt(this.chars.length);
            builder.append(this.chars[index]);
        }
        return builder.toString();
    }
}
