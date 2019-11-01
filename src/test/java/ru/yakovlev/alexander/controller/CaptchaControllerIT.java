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

import java.awt.image.BufferedImage;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import ru.yakovlev.alexander.model.dto.CaptchaResponse;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.isEmptyString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Captcha controller integration test.
 *
 * @author Yakovlev Alexander (sanyakovlev@yandex.ru)
 * @since 0.1
 */
@SuppressWarnings("ConstantConditions")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CaptchaControllerIT {
    @Autowired
    private TestRestTemplate testRestTemplate;

    @Test
    void whenSendPostThenNewCaptchaCreated() {
        final UUID clientId = UUID
            .fromString("7f000101-6e06-121f-816e-06ce2f660000");
        final ResponseEntity<CaptchaResponse> response = this.testRestTemplate
            .postForEntity(
                "/clients/{clientId}/captcha", "",
                CaptchaResponse.class, clientId
            );
        final CaptchaResponse captcha = response.getBody();
        final String actualLocation = response
            .getHeaders()
            .getLocation()
            .toString();
        final String expectedLocation = String.format(
            "/clients/%s/captcha/%s", clientId, captcha.getCaptchaId()
        );
        assertEquals(expectedLocation, actualLocation);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(captcha.getCaptchaId());
        assertThat(captcha.getAnswer(), not(isEmptyString()));
    }

    @Test
    void whenGetCaptchaThenGetImage() {
        final UUID clientId = UUID
            .fromString("7f000101-6e06-121f-816e-06ce2f660000");
        final ResponseEntity<CaptchaResponse> response = this.testRestTemplate
            .postForEntity(
                "/clients/{clientId}/captcha", "",
                CaptchaResponse.class, clientId
            );
        final Long captchaId = response.getBody().getCaptchaId();
        final ResponseEntity<BufferedImage> imgResponse = this.testRestTemplate
            .getForEntity(
                "/clients/{clientId}/captcha/{captchaId}",
                BufferedImage.class, clientId, captchaId
            );
        assertEquals(HttpStatus.OK, imgResponse.getStatusCode());
        final MediaType actualType = imgResponse.getHeaders().getContentType();
        assertEquals(MediaType.IMAGE_PNG, actualType);
    }

    @Test
    void whenPostOnSolveThenNewVerificationTokenCreated() {
        final UUID clientId = UUID
            .fromString("7f000101-6e06-121f-816e-06ce2f660000");
        final ResponseEntity<CaptchaResponse> response = this.testRestTemplate
            .postForEntity(
                "/clients/{clientId}/captcha", "",
                CaptchaResponse.class, clientId
            );
        final Long captchaId = response.getBody().getCaptchaId();
        final ResponseEntity<Long> tokenResponse = this.testRestTemplate
            .postForEntity(
                "/clients/{clientId}/captcha/{captchaId}/solve",
                "answer to captcha",
                Long.class, clientId, captchaId
            );
        final Long tokenId = tokenResponse.getBody();
        final String actualLocation = tokenResponse
            .getHeaders()
            .getLocation()
            .toString();
        final String expectedLocation = String.format(
            "/clients/%s/captcha/%s/tokens/%s", clientId, captchaId, tokenId
        );
        assertEquals(expectedLocation, actualLocation);
        assertEquals(HttpStatus.CREATED, tokenResponse.getStatusCode());
        assertNotNull(tokenId);
    }
}