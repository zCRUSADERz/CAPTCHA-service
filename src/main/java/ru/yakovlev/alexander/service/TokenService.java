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

import javax.transaction.Transactional;
import java.util.UUID;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ru.yakovlev.alexander.configuration.AppProperties;
import ru.yakovlev.alexander.model.Captcha;
import ru.yakovlev.alexander.model.dto.CaptchaCheckResult;
import ru.yakovlev.alexander.model.VerificationToken;
import ru.yakovlev.alexander.repository.VerificationTokenRepository;

/**
 * Verification token service.
 *
 * @author Yakovlev Alexander (sanyakovlev@yandex.ru)
 * @since 0.1
 */
@Service
@AllArgsConstructor
public class TokenService {
    private final ClientService clientService;
    private final CaptchaService captchaService;
    private final VerificationTokenRepository tokenRepository;
    private final AppProperties.Captcha captchaProperties;

    /**
     * Additional constructor.
     *
     * @param clientService   client service.
     * @param captchaService  captcha service.
     * @param tokenRepository token repository.
     * @param appProperties   application properties.
     * @since 0.1
     */
    @Autowired
    public TokenService(
        final ClientService clientService, final CaptchaService captchaService,
        final VerificationTokenRepository tokenRepository,
        final AppProperties appProperties
    ) {
        this(
            clientService, captchaService,
            tokenRepository, appProperties.getCaptcha()
        );
    }

    /**
     * Find token by client, captcha and token id with their eager fetch.
     *
     * @param clientId  client UUID.
     * @param captchaId captcha ID.
     * @param tokenId   token ID.
     * @return verification token.
     * @throws ResponseStatusException if not found.
     * @since 0.1
     */
    public VerificationToken findByIdsWithFetch(
        final UUID clientId, final Long captchaId, final Long tokenId
    ) throws ResponseStatusException {
        return this.tokenRepository
            .findByIdsWithFetch(clientId, captchaId, tokenId)
            .orElseThrow(
                () -> new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format(
                        "Verification token for client id: %s, captcha id: %s "
                            + "and token id: %s not found.",
                        clientId, captchaId, tokenId
                    )
                )
            );
    }

    /**
     * Create new verification token.
     *
     * @param clientId  client UUID.
     * @param captchaId captcha ID.
     * @param answer    answer to captcha.
     * @return token.
     * @since 0.1
     */
    public VerificationToken create(
        final UUID clientId, final Long captchaId, final String answer
    ) {
        final Captcha captcha = this.captchaService
            .findActiveCaptcha(clientId, captchaId);
        final VerificationToken token = new VerificationToken(answer, captcha);
        this.tokenRepository.save(token);
        return token;
    }

    /**
     * Verification token activation. Token can be activated only once.
     *
     * @param clientId  client id.
     * @param captchaId captcha id.
     * @param tokenId   verification token id.
     * @param secretKey client secret key.
     * @return captcha check result.
     * @since 0.1
     */
    @Transactional
    public CaptchaCheckResult activate(
        final UUID clientId, final Long captchaId,
        final Long tokenId, final UUID secretKey
    ) {
        final VerificationToken token = this.findByIdsWithFetch(
            clientId, captchaId, tokenId
        );
        final CaptchaCheckResult result = token.activate(
            secretKey, this.captchaProperties.getTimeout()
        );
        this.tokenRepository.save(token);
        return result;
    }

    /**
     * Returns result of a captcha check.
     * @param clientId client UUID.
     * @param captchaId captcha ID.
     * @param tokenId token ID.
     * @return result of a captcha check.
     * @since 0.1
     */
    public CaptchaCheckResult resultOfCaptchaCheck(
        final UUID clientId, final Long captchaId,
        final Long tokenId
    ) {
        final VerificationToken token = this.findByIdsWithFetch(
            clientId, captchaId, tokenId
        );
        return token.resultOfCaptchaCheck();
    }
}
