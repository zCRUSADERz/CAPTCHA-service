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

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import ru.yakovlev.alexander.configuration.AppProperties;
import ru.yakovlev.alexander.repository.VerificationTokenRepository;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * Token service test.
 *
 * @author Yakovlev Alexander (sanyakovlev@yandex.ru)
 * @since 0.1
 */
@ExtendWith(MockitoExtension.class)
class TokenServiceTest {
    @Mock
    private ClientService clientService;
    @Mock
    private CaptchaService captchaService;
    @Mock
    private VerificationTokenRepository tokenRepository;

    @Test
    void whenFindByIdsWithFetchNotExistingTokenThenThrowException() {
        final UUID clientId = UUID
            .fromString("7f000101-6e1f-192d-816e-1ffa54780000");
        final Long captchaId = 1L;
        final Long tokenId = 1L;
        final TokenService service = new TokenService(
            this.clientService, this.captchaService,
            this.tokenRepository, new AppProperties()
        );
        when(
            this.tokenRepository
                .findByIdsWithFetch(clientId, captchaId, tokenId)
        ).thenReturn(Optional.empty());
        assertThrows(
            ResponseStatusException.class,
            () -> service.findByIdsWithFetch(clientId, captchaId, tokenId)
        );
    }
}