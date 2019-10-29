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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.UUID;

import org.hamcrest.core.IsNot;
import org.hamcrest.text.IsEmptyString;
import org.hamcrest.text.StringContainsInOrder;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import ru.yakovlev.alexander.model.dto.CaptchaCheckResult;
import ru.yakovlev.alexander.util.TimeUtc;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Captcha test.
 *
 * @author Yakovlev Alexander (sanyakovlev@yandex.ru)
 * @since 0.1
 */
class CaptchaTest {
    private final TimeUtc time = new TimeUtc();

    @Test
    void whenTimeoutIsNotOverThenCheckTimeoutNotThrowException() {
        final int timeout = 10;
        final UUID uuid = UUID
            .fromString("7f000101-6e1f-192d-816e-1ffa54780000");
        final Captcha captcha = new Captcha(
            0L,
            new Client(uuid, uuid, 0),
            "", this.time.now(), false, 0
        );
        assertDoesNotThrow(() -> captcha.checkTimeout(timeout));
    }

    @Test
    void whenTimeoutIsOverThenCheckTimeoutThrowException() {
        final int timeout = 10;
        final LocalDateTime afterTimeout = this.time.nowAfterTimeout(timeout);
        final UUID uuid = UUID
            .fromString("7f000101-6e1f-192d-816e-1ffa54780001");
        final Captcha captcha = new Captcha(
            1L,
            new Client(uuid, uuid, 0),
            "", afterTimeout, false, 0
        );
        assertThrows(
            ResponseStatusException.class,
            () -> captcha.checkTimeout(timeout))
        ;
    }

    @Test
    void whenCaptchaIsNotSolvedThenCheckSolvedDoesNotThrowException() {
        final UUID uuid = UUID
            .fromString("7f000101-6e1f-192d-816e-1ffa54780002");
        final Captcha captcha = new Captcha(
            2L,
            new Client(uuid, uuid, 0),
            "", this.time.now(), false, 0
        );
        assertDoesNotThrow(captcha::checkSolved);
    }

    @Test
    void whenCaptchaIsSolvedThenCheckSolvedThrowException() {
        final UUID uuid = UUID
            .fromString("7f000101-6e1f-192d-816e-1ffa54780003");
        final Captcha captcha = new Captcha(
            3L,
            new Client(uuid, uuid, 0),
            "", this.time.now(), true, 0
        );
        assertThrows(ResponseStatusException.class, captcha::checkSolved);
    }

    @Test
    void whenSecretKeyWrongThenSolveThrowException() {
        final LocalDateTime afterTimeout = this.time.now();
        final UUID uuid = UUID
            .fromString("7f000101-6e1f-192d-816e-1ffa54780004");
        final UUID secret = UUID
            .fromString("7f000101-6e1f-192d-816e-1ffa54780005");
        final UUID wrongSecret = UUID
            .fromString("7f000101-6e1f-192d-816e-1ffa54780006");
        final Captcha captcha = new Captcha(
            20L,
            new Client(uuid, secret, 0),
            "", afterTimeout, false, 0
        );
        assertThrows(
            ResponseStatusException.class,
            () -> captcha.solve("av", wrongSecret, 7)
        );
    }

    @Test
    void whenSolveCaptchaAndTimeoutIsOverThenThrowException() {
        final int timeout = 5;
        final LocalDateTime afterTimeout = this.time.nowAfterTimeout(timeout);
        final UUID uuid = UUID
            .fromString("7f000101-6e1f-192d-816e-1ffa54780007");
        final Captcha captcha = new Captcha(
            4L,
            new Client(uuid, uuid, 0),
            "", afterTimeout, false, 0
        );
        assertThrows(
            ResponseStatusException.class,
            () -> captcha.solve("av", uuid, timeout)
        );
    }

    @Test
    void whenSolveCaptchaWhichHasBeenSolvedThenThrowException() {
        final UUID uuid = UUID
            .fromString("7f000101-6e1f-192d-816e-1ffa54780008");
        final Captcha captcha = new Captcha(
            5L,
            new Client(uuid, uuid, 0),
            "", this.time.now(), true, 0
        );
        assertThrows(
            ResponseStatusException.class,
            () -> captcha.solve("arg", uuid, 30)
        );
    }

    @Test
    void whenSolveCaptchaWithRightAnswerThenReturnSuccessResult() {
        final String answer = "absdef";
        final UUID uuid = UUID
            .fromString("7f000101-6e1f-192d-816e-1ffa54780009");
        final Captcha captcha = new Captcha(
            6L,
            new Client(uuid, uuid, 0),
            answer, this.time.now(), false, 0
        );
        final CaptchaCheckResult result = captcha.solve(answer, uuid, 20);
        assertTrue(result.isSuccess());
    }

    @Test
    void whenCaptchaIsSuccessfullySolvedThenCaptchaIsSolved() {
        final String answer = "qnrt";
        final UUID uuid = UUID
            .fromString("7f000101-6e1f-192d-816e-1ffa54780010");
        final Captcha captcha = new Captcha(
            7L,
            new Client(uuid, uuid, 0),
            answer, this.time.now(), false, 0
        );
        captcha.solve(answer, uuid, 15);
        assertTrue(captcha.isSolved());
    }

    @Test
    void whenCaptchaNotSolvedThenCaptchaIsNotSolved() {
        final UUID uuid = UUID
            .fromString("7f000101-6e1f-192d-816e-1ffa54780011");
        final Captcha captcha = new Captcha(
            12L,
            new Client(uuid, uuid, 0),
            "het", this.time.now(), false, 0
        );
        captcha.solve("wrong answer", uuid, 19);
        assertFalse(captcha.isSolved());
    }
    
    @Test
    void whenCheckRightAnswerThenReturnSuccessResult() {
        final String answer = "123";
        final UUID uuid = UUID
            .fromString("7f000101-6e1f-192d-816e-1ffa54780012");
        final Captcha captcha = new Captcha(
            8L,
            new Client(uuid, uuid, 0),
            answer, this.time.now(), false, 0
        );
        final CaptchaCheckResult result = captcha.check(answer);
        assertTrue(result.isSuccess());
    }
    
    @Test
    void whenCheckThenNoStateChecksPerformed() {
        final int timeout = 15;
        final LocalDateTime now = this.time.nowAfterTimeout(timeout);
        final UUID uuid = UUID
            .fromString("7f000101-6e1f-192d-816e-1ffa54780013");
        final Captcha captcha = new Captcha(
            9L,
            new Client(uuid, uuid, 0),
            "qbywf", now, true, 0
        );
        assertDoesNotThrow(() -> captcha.check(""));
    }

    @Test
    void whenAnswerHasDifferentLengthThenNotSuccess() {
        final String captchaWord = "a";
        final String answer = "abht";
        final UUID uuid = UUID
            .fromString("7f000101-6e1f-192d-816e-1ffa54780014");
        final Captcha captcha = new Captcha(
            10L,
            new Client(uuid, uuid, 0),
            captchaWord, this.time.now(), false, 0
        );
        final CaptchaCheckResult result = captcha.check(answer);
        assertFalse(result.isSuccess());
        assertThat(
            result.getError(),
            new StringContainsInOrder(
                Arrays.asList(
                    Integer.toString(answer.length()),
                    Integer.toString(captchaWord.length())
                )
            )
        );
    }

    @Test
    void whenAnswerWrongThenNotSuccess() {
        final UUID uuid = UUID
            .fromString("7f000101-6e1f-192d-816e-1ffa54780015");
        final Captcha captcha = new Captcha(
            11L,
            new Client(uuid, uuid, 0),
            "abc", this.time.now(), false, 0
        );
        final CaptchaCheckResult result = captcha.check("cba");
        assertFalse(result.isSuccess());
        assertThat(result.getError(), new IsNot<>(new IsEmptyString()));
    }
}