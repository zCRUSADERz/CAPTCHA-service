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

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yakovlev.alexander.model.dto.CaptchaCheckResult;
import ru.yakovlev.alexander.model.dto.ClientKey;
import ru.yakovlev.alexander.service.TokenService;

/**
 * Verification token controller.
 *
 * @author Yakovlev Alexander (sanyakovlev@yandex.ru)
 * @since 0.1
 */
@AllArgsConstructor
@RestController
@RequestMapping("/clients/{clientId}/captcha/{captchaId}/tokens")
public class VerificationTokenController {
    private final TokenService tokenService;

    /**
     * Returns result of a captcha check if the token has been activated.
     *
     * @param clientId  client UUID.
     * @param captchaId captcha ID.
     * @param tokenId   token ID.
     * @return result of a captcha check.
     */
    @GetMapping("/{tokenId}")
    public CaptchaCheckResult getResult(
        @PathVariable final UUID clientId,
        @PathVariable final Long captchaId,
        @PathVariable final Long tokenId
    ) {
        return this.tokenService.resultOfCaptchaCheck(
            clientId, captchaId, tokenId
        );
    }

    /**
     * Verification token activation.
     *
     * @param clientId  client id.
     * @param captchaId captcha id.
     * @param tokenId   verification token id.
     * @param clientKey client secret key.
     * @return captcha check result.
     * @since 0.1
     */
    @PostMapping("/{tokenId}/activate")
    public CaptchaCheckResult activateToken(
        @PathVariable final UUID clientId,
        @PathVariable final Long captchaId,
        @PathVariable final Long tokenId,
        @RequestBody final ClientKey clientKey
    ) {
        return this.tokenService.activate(
            clientId, captchaId, tokenId, clientKey.getKey()
        );
    }
}
