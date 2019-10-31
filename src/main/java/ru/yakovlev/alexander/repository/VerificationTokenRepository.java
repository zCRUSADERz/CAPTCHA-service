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

package ru.yakovlev.alexander.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.yakovlev.alexander.model.VerificationToken;

/**
 * Verification token repository.
 *
 * @author Yakovlev Alexander (sanyakovlev@yandex.ru)
 * @since 0.1
 */
public interface VerificationTokenRepository
    extends JpaRepository<VerificationToken, Long> {

    /**
     * Return verification token found by client, captcha and token ids.
     * Client and captcha will be eager fetched.
     *
     * @param clientId  client id.
     * @param captchaId captcha id.
     * @param tokenId   token id.
     * @return optional of verification token.
     * @since 0.1
     */
    @Query(
        "SELECT t FROM VerificationToken t JOIN FETCH t.captcha c JOIN FETCH c.owner o "
            + "WHERE o.id = :clientId AND c.id = :captchaId AND t.id = :tokenId"
    )
    Optional<VerificationToken> findByIdsWithFetch(
        final UUID clientId, final Long captchaId, final Long tokenId
    );
}
