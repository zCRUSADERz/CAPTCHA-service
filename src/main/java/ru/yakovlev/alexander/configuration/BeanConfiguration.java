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

package ru.yakovlev.alexander.configuration;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import ru.yakovlev.alexander.service.CharacterRange;
import ru.yakovlev.alexander.service.RandomStringStream;

/**
 * Bean configuration class.
 *
 * @author Yakovlev Alexander (sanyakovlev@yandex.ru)
 * @since 0.1
 */
@Configuration
public class BeanConfiguration {

    @Bean
    public ServerMode serverMode(final AppProperties properties) {
        return ServerMode.valueOf(properties.getMode().toUpperCase());
    }

    @Bean
    @Scope(scopeName = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
    public SecureRandom secureRandom() throws NoSuchAlgorithmException {
        return SecureRandom.getInstance("SHA1PRNG");
    }

    /**
     * Return service: random string stream.
     *
     * @param appProperties application properties.
     * @param secureRandom  secure random.
     * @return random string stream.
     * @since 0.1
     */
    @Bean
    public RandomStringStream randomStringStream(
        final AppProperties appProperties,
        final SecureRandom secureRandom
    ) {
        final CharacterRange range = new CharacterRange();
        final AppProperties.Captcha captchaProperties
            = appProperties.getCaptcha();
        return new RandomStringStream(
            captchaProperties.getLength(),
            range.characters(captchaProperties.getCharacterRange()),
            secureRandom
        );
    }
}
