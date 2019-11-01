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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yakovlev.alexander.configuration.ServerMode;
import ru.yakovlev.alexander.model.Captcha;
import ru.yakovlev.alexander.model.Client;
import ru.yakovlev.alexander.service.CaptchaService;
import ru.yakovlev.alexander.service.TokenService;
import ru.yakovlev.alexander.util.TimeUtc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Captcha controller unit test.
 *
 * @author Yakovlev Alexander (sanyakovlev@yandex.ru)
 * @since 0.1
 */
@ExtendWith(MockitoExtension.class)
class CaptchaControllerTest {
    @Mock
    private CaptchaService captchaService;
    @Mock
    private TokenService tokenService;
    private TimeUtc time = new TimeUtc();

    @Test
    void whenModeProductionThenResponseNotContainAnswer() throws Exception {
        final UUID clientId = UUID
            .fromString("7f000101-6e06-121f-816e-06ce2f660000");
        final CaptchaController controller = new CaptchaController(
            this.captchaService, this.tokenService, ServerMode.PRODUCTION
        );
        final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .build();
        when(this.captchaService.createNew(clientId))
            .thenReturn(
                new Captcha(
                    1L,
                    new Client(clientId, clientId, 0),
                    "answer", this.time.now(), false, 0
                )
            );
        mockMvc.perform(
            post("/clients/{clientId}/captcha", clientId)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.answer").doesNotExist()
        );
    }

    @Test
    void whenModeTestThenResponseContainAnswer() throws Exception {
        final UUID clientId = UUID
            .fromString("7f000101-6e06-121f-816e-06ce2f660001");
        final CaptchaController controller = new CaptchaController(
            this.captchaService, this.tokenService, ServerMode.TEST
        );
        final MockMvc mockMvc = MockMvcBuilders
            .standaloneSetup(controller)
            .build();
        final String answer = "qwerty";
        when(this.captchaService.createNew(clientId))
            .thenReturn(
                new Captcha(
                    2L,
                    new Client(clientId, clientId, 0),
                    answer, this.time.now(), false, 0
                )
            );
        mockMvc.perform(
            post("/clients/{clientId}/captcha", clientId)
                .accept(MediaType.APPLICATION_JSON)
        ).andExpect(
            MockMvcResultMatchers.jsonPath("$.answer").value(answer)
        );
    }
}