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

import java.awt.image.BufferedImage;
import java.util.UUID;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yakovlev.alexander.configuration.AppProperties;
import ru.yakovlev.alexander.model.Captcha;
import ru.yakovlev.alexander.model.Client;
import ru.yakovlev.alexander.repository.CaptchaRequestRepository;

/**
 * Captcha service.
 *
 * @author Yakovlev Alexander (sanyakovlev@yandex.ru)
 * @since 0.1
 */
@Service
@AllArgsConstructor
public class CaptchaService {
    private final ClientService clientService;
    private final CaptchaRequestRepository captchaRepository;
    private final RandomStringStream randomStringStream;
    private final AppProperties.Captcha captchaProperties;

    /**
     * Additional constructor.
     * @param clientService client service
     * @param captchaRepository captcha repository.
     * @param randomStringStream random string stream.
     * @param appProperties application properties.
     * @since 0.1
     */
    @Autowired
    public CaptchaService(
        final ClientService clientService,
        final CaptchaRequestRepository captchaRepository,
        final RandomStringStream randomStringStream,
        final AppProperties appProperties
    ) {
        this(
            clientService, captchaRepository,
            randomStringStream, appProperties.getCaptcha()
        );
    }

    /**
     * Return active(timeout is not over and not solved) captcha.
     *
     * @param clientId  client UUID.
     * @param captchaId captcha ID.
     * @return active captcha.
     * @throws ResponseStatusException if not found.
     * @since 0.1
     */
    public Captcha findActiveCaptcha(
        final UUID clientId, final Long captchaId
    ) throws ResponseStatusException {
        final Captcha captcha = this.captchaRepository
            .findByIdAndOwnerId(captchaId, clientId)
            .orElseThrow(
                () -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format(
                        "Captcha with id: %s, for client: %s, not found.",
                        captchaId, clientId
                    )
                )
            );
        captcha.checkSolved();
        captcha.checkTimeout(this.captchaProperties.getTimeout());
        return captcha;
    }
}
