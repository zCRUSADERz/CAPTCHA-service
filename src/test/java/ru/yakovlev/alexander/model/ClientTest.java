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

package ru.yakovlev.alexander.model;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Client test.
 *
 * @author Yakovlev Alexander (sanyakovlev@yandex.ru)
 * @since 0.1
 */
class ClientTest {

    @Test
    void whenAuthenticateWithSameSecretKeyThenNotThrowException() {
        final UUID uuid = UUID
            .fromString("7f000101-6e1f-192d-816e-1ffa54780000");
        final UUID secret = UUID
            .fromString("7f000101-6e1f-192d-816e-1ffa54780001");
        final Client client = new Client(uuid, secret, 0);
        assertDoesNotThrow(() -> client.authenticate(secret));
    }

    @Test
    void whenAuthenticateWithDifferentSecretKeyThenThrowException() {
        final UUID uuid = UUID
            .fromString("7f000101-6e1f-192d-816e-1ffa54780002");
        final UUID secret = UUID
            .fromString("7f000101-6e1f-192d-816e-1ffa54780003");
        final UUID wrongSecret = UUID
            .fromString("7f000101-6e1f-192d-816e-1ffa54780004");
        final Client client = new Client(uuid, secret, 0);
        assertThrows(
            ResponseStatusException.class,
            () -> client.authenticate(wrongSecret)
        );
    }
}