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

import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import ru.yakovlev.alexander.configuration.ServerMode;
import ru.yakovlev.alexander.model.Captcha;
import ru.yakovlev.alexander.model.VerificationToken;
import ru.yakovlev.alexander.model.dto.CaptchaResponse;
import ru.yakovlev.alexander.service.CaptchaService;
import ru.yakovlev.alexander.service.TokenService;

/**
 * Captcha controller.
 *
 * @author Yakovlev Alexander (sanyakovlev@yandex.ru)
 * @since 0.1
 */
@RestController
@RequestMapping("/clients/{clientId}/captcha")
@AllArgsConstructor
public class CaptchaController {
    private final CaptchaService captchaService;
    private final TokenService tokenService;
    private final ServerMode serverMode;

    /**
     * Return captcha png image.
     *
     * @param clientId  client UUID.
     * @param captchaId captcha id.
     * @return captcha png image.
     * @since 0.1
     */
    @GetMapping(path = "/{captchaId}", produces = MediaType.IMAGE_PNG_VALUE)
    public BufferedImage captchaImage(
        @PathVariable final UUID clientId,
        @PathVariable final Long captchaId
    ) {
        return this.captchaService.captchaImage(clientId, captchaId);
    }

    /**
     * Creates a new captcha for the client.
     *
     * @param clientId client UUID.
     * @return captcha id with optional answer.
     * @since 0.1
     */
    @PostMapping
    public ResponseEntity createCaptcha(@PathVariable UUID clientId) {
        final Captcha captcha = this.captchaService.createNew(clientId);
        final Long captchaId = captcha.getId();
        final Object result;
        if (this.serverMode.equals(ServerMode.TEST)) {
            result = new CaptchaResponse(captchaId, captcha.getAnswer());
        } else if (this.serverMode.equals(ServerMode.PRODUCTION)) {
            result = captchaId;
        } else {
            throw new IllegalStateException(
                String.format(
                    "Unknown mode: %s", this.serverMode
                )
            );
        }
        return ResponseEntity
            .created(
                UriComponentsBuilder
                    .fromUriString("/clients/{clientId}/captcha/{captchaId}")
                    .build(clientId, captchaId)
            )
            .body(result);
    }

    /**
     * Solve captcha.
     *
     * @param clientId  client id.
     * @param captchaId captcha id.
     * @param answer    answer to captcha.
     * @return verification token id.
     * @since 0.1
     */
    @PostMapping("/{captchaId}/solve")
    public ResponseEntity<Long> solve(
        @PathVariable UUID clientId,
        @PathVariable Long captchaId,
        @RequestBody String answer
    ) {
        final VerificationToken token = this.tokenService
            .create(clientId, captchaId, answer);
        return ResponseEntity
            .created(
                UriComponentsBuilder
                    .fromUriString(
                        "/clients/{clientId}/captcha/{captchaId}/tokens/{tokenId}"
                    ).build(clientId, captchaId, token.getId())
            )
            .body(token.getId());
    }
}
