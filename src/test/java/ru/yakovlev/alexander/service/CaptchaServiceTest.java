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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;
import ru.yakovlev.alexander.configuration.AppProperties;
import ru.yakovlev.alexander.model.Captcha;
import ru.yakovlev.alexander.model.Client;
import ru.yakovlev.alexander.repository.CaptchaRequestRepository;
import ru.yakovlev.alexander.util.TimeUtc;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * Character range test.
 *
 * @author Yakovlev Alexander (sanyakovlev@yandex.ru)
 * @since 0.1
 */
@ExtendWith(MockitoExtension.class)
class CaptchaServiceTest {
    @Mock
    private ClientService clientService;
    @Mock
    private CaptchaRequestRepository captchaRepository;
    @Mock
    private RandomStringStream randomStringStream;
    @Mock
    private AppProperties appProperties;
    @InjectMocks
    private CaptchaService captchaService;
    private final TimeUtc time = new TimeUtc();

    @Test
    void whenFindActiveCaptchaAndCaptchaNotExistThenThrowException() {
        final UUID clientId = UUID
            .fromString("7f000101-6e1f-192d-816e-1ffa54780000");
        final Long captchaId = 1L;
        when(this.captchaRepository.findByIdAndOwnerId(captchaId, clientId))
            .thenReturn(Optional.empty());
        assertThrows(
            ResponseStatusException.class,
            () -> this.captchaService.findActiveCaptcha(clientId, captchaId)
        );
    }

    @Test
    void whenFindActiveCaptchaAndCaptchaHasBeenSolvedThenThrowException() {
        final UUID clientId = UUID
            .fromString("7f000101-6e1f-192d-816e-1ffa54780001");
        final Long captchaId = 2L;
        when(this.captchaRepository.findByIdAndOwnerId(captchaId, clientId))
            .thenReturn(
                Optional.of(
                    new Captcha(
                        captchaId,
                        new Client(clientId, clientId, 0),
                        "answer", this.time.now(), true, 0
                    )
                )
            );
        assertThrows(
            ResponseStatusException.class,
            () -> this.captchaService.findActiveCaptcha(clientId, captchaId)
        );
    }

    @Test
    void whenFindActiveCaptchaAndTimeoutIsOverThenThrowException() {
        final int timeout = 7;
        final UUID clientId = UUID
            .fromString("7f000101-6e1f-192d-816e-1ffa54780002");
        final Long captchaId = 3L;
        final AppProperties.Captcha properties
            = new AppProperties.Captcha(6, "a", timeout);
        final CaptchaService service = new CaptchaService(
            this.clientService, this.captchaRepository,
            this.randomStringStream, properties
        );
        when(this.captchaRepository.findByIdAndOwnerId(captchaId, clientId))
            .thenReturn(
                Optional.of(
                    new Captcha(
                        captchaId,
                        new Client(clientId, clientId, 0),
                        "answer", this.time.nowAfterTimeout(timeout),
                        false, 0
                    )
                )
            );
        assertThrows(
            ResponseStatusException.class,
            () -> service.findActiveCaptcha(clientId, captchaId)
        );
    }
}