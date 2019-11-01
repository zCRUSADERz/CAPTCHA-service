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
import ru.yakovlev.alexander.model.dto.CaptchaCheckResult;
import ru.yakovlev.alexander.util.TimeUtc;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verification token test.
 *
 * @author Yakovlev Alexander (sanyakovlev@yandex.ru)
 * @since 0.1
 */
class VerificationTokenTest {
    private final TimeUtc time = new TimeUtc();

    @Test
    void whenActivateSuccessfullyThenTokenIsActivated() {
        final int timeout = 10;
        final String answer = "abswer";
        final UUID uuid = UUID
            .fromString("7f000101-6e1f-192d-816e-1ffa54780000");
        final Captcha captcha = new Captcha(
            0L,
            new Client(uuid, uuid, 0),
            answer, this.time.now(), false, 0
        );
        final VerificationToken token = new VerificationToken(
        0L, answer, captcha, false, 0
        );
        final CaptchaCheckResult result = token.activate(uuid, timeout);
        assertTrue(result.isSuccess());
        assertTrue(token.isActivated());
    }

    @Test
    void whenActivateTokenThatIsAlreadyActivatedThenThrowException() {
        final String answer = "brty";
        final UUID uuid = UUID
            .fromString("7f000101-6e1f-192d-816e-1ffa54780001");
        final VerificationToken token = new VerificationToken(
            1L, answer,
            new Captcha(
                1L,
                new Client(uuid, uuid, 0),
                "answer", this.time.now(), true, 0
            ),
            true, 0
        );
        assertThrows(
            ResponseStatusException.class,
            () -> token.activate(uuid, 1)
        );
    }

    @Test
    void whenCallResultOfCaptchaCheckOnTokenWithRightAnswerThenReturnSuccess() {
        final int timeout = 15;
        final String answer = "entr";
        final UUID uuid = UUID
            .fromString("7f000101-6e1f-192d-816e-1ffa54780002");
        final VerificationToken token = new VerificationToken(
            2L, answer,
            new Captcha(
                2L,
                new Client(uuid, uuid, 0),
                answer, this.time.nowAfterTimeout(timeout), true, 0
            ),
            true, 0
        );
        final CaptchaCheckResult result = token.resultOfCaptchaCheck();
        assertTrue(result.isSuccess());
    }

    @Test
    void whenCallResultOfCaptchaCheckOnNotActivatedTokenThenThrowException() {
        final UUID uuid = UUID
            .fromString("7f000101-6e1f-192d-816e-1ffa54780003");
        final VerificationToken token = new VerificationToken(
            3L, "answer",
            new Captcha(
                3L,
                new Client(uuid, uuid, 0),
                "wrong answer", this.time.now(), false, 0
            ),
            false, 0
        );
        assertThrows(ResponseStatusException.class, token::resultOfCaptchaCheck);
    }
}