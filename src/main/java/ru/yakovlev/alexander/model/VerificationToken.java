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

package ru.yakovlev.alexander.model;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.yakovlev.alexander.model.dto.CaptchaCheckResult;

/**
 * Captcha verification token.
 *
 * @author Yakovlev Alexander (sanyakovlev@yandex.ru)
 * @since 0.1
 */
@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class VerificationToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, updatable = false)
    private String answerToCaptcha;

    @NotNull
    @ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
    private Captcha captcha;

    @Column(nullable = false)
    private boolean activated;

    @Version
    private int version;

    /**
     * Constructor for new verification token.
     *
     * @param answerToCaptcha answer to captcha.
     * @param captcha         captcha.
     * @since 0.1
     */
    public VerificationToken(
        final String answerToCaptcha, final Captcha captcha
    ) {
        this.answerToCaptcha = answerToCaptcha;
        this.captcha = captcha;
    }

    /**
     * Activate token. Verification token can be activated only once.
     * Note: this method mutate this object and maybe captcha!
     *
     * @param secretKey        client secret key.
     * @param timeoutInSeconds captcha timeout in seconds.
     * @return result of a captcha check.
     * @throws ResponseStatusException if token has already activated.
     * @since 0.1
     */
    public CaptchaCheckResult activate(
        final UUID secretKey, final int timeoutInSeconds
    ) {
        if (this.activated) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                String.format(
                    "Token with id: %s has already activated.",
                    this.id
                )
            );
        }
        final CaptchaCheckResult result = this.captcha.solve(
            this.answerToCaptcha, secretKey, timeoutInSeconds
        );
        this.activated = true;
        return result;
    }

    /**
     * Returns the result of a captcha check if the token was activated.
     *
     * @return result of a captcha check.
     * @throws ResponseStatusException if the token has not been activated.
     * @since 0.1
     */
    public CaptchaCheckResult resultOfCaptchaCheck()
        throws ResponseStatusException {
        if (!this.activated) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                String.format(
                    "Token with id: %s has not yet been activated.",
                    this.id
                )
            );
        }
        return this.captcha.check(this.answerToCaptcha);
    }
}
