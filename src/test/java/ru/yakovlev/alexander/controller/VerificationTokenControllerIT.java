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

package ru.yakovlev.alexander.controller;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.yakovlev.alexander.model.dto.CaptchaCheckResult;
import ru.yakovlev.alexander.model.dto.CaptchaResponse;
import ru.yakovlev.alexander.model.dto.ClientKey;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Verification token controller integration test.
 *
 * @author Yakovlev Alexander (sanyakovlev@yandex.ru)
 * @since 0.1
 */
@SuppressWarnings("ConstantConditions")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class VerificationTokenControllerIT {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void whenActivateTokenThenResponseContainCheckResult() {
        final UUID clientId = UUID
            .fromString("7f000101-6e06-121f-816e-06ce2f660000");
        final CaptchaResponse captcha = this.testRestTemplate
            .postForEntity(
                "/clients/{clientId}/captcha", "",
                CaptchaResponse.class, clientId
            ).getBody();
        final Long tokenId = this.testRestTemplate
            .postForEntity(
                "/clients/{clientId}/captcha/{captchaId}/solve",
                captcha.getAnswer(),
                Long.class, clientId, captcha.getCaptchaId()
            ).getBody();
        final ResponseEntity<CaptchaCheckResult> checkResult = this.testRestTemplate
            .postForEntity(
                "/clients/{clientId}/captcha/{captchaId}/" +
                    "tokens/{tokenId}/activate",
                new ClientKey(
                    UUID.fromString("7f000101-6e06-121f-816e-06ce2f660001")
                ),
                CaptchaCheckResult.class,
                clientId, captcha.getCaptchaId(), tokenId
            );
        assertEquals(HttpStatus.OK, checkResult.getStatusCode());
        final CaptchaCheckResult result = checkResult.getBody();
        assertTrue(result.isSuccess());
        assertThat(result.getError(), isEmptyString());
    }
}