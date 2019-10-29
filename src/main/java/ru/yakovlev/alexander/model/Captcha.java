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
import javax.validation.constraints.PastOrPresent;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import ru.yakovlev.alexander.model.dto.CaptchaCheckResult;

/**
 * Captcha request entity.
 *
 * @author Yakovlev Alexander (sanyakovlev@yandex.ru)
 * @since 0.1
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Captcha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.MERGE)
    private Client owner;

    @NotBlank
    @Column(length = 6, columnDefinition = "char", nullable = false, updatable = false)
    private String answer;

    @PastOrPresent
    @Column(nullable = false, updatable = false)
    private LocalDateTime created;

    @Column(nullable = false)
    private boolean solved;

    @Version
    private int version;

    /**
     * Constructor for new captcha request entity.
     *
     * @param answer answer to captcha.
     * @since 0.1
     */
    public Captcha(final Client owner, final String answer) {
        this.owner = owner;
        this.answer = answer;
        this.created = LocalDateTime.now(ZoneOffset.UTC);
    }

    /**
     * Сhecks if the timeout over.
     * Captcha can be solved in a certain period of time.
     *
     * @param timeoutInSeconds timeout in seconds.
     * @throws ResponseStatusException if timeout is over.
     * @since 0.1
     */
    public void checkTimeout(final int timeoutInSeconds)
        throws ResponseStatusException {
        final boolean timeoutOver = LocalDateTime
            .now(ZoneOffset.UTC)
            .minusSeconds(timeoutInSeconds)
            .isAfter(this.created);
        if (timeoutOver) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                String.format(
                    "For captcha with id: %s timeout is over.",
                    this.id
                )
            );
        }
    }

    /**
     * Checks if captcha has already solved.
     *
     * @throws ResponseStatusException if solved.
     * @since 0.1
     */
    public void checkSolved() throws ResponseStatusException {
        if (this.isSolved()) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                String.format(
                    "Captcha with id: %s has already solved.", this.id
                )
            );
        }
    }

    /**
     * Check answer without state checks and set solved to true,
     * if check successfully.
     *
     * @param requestAnswer answer to captcha.
     * @return result of a captcha check.
     * @since 0.1
     */
    public CaptchaCheckResult check(final String requestAnswer) {
        final CaptchaCheckResult result;
        if (this.answer.equals(requestAnswer)) {
            result = new CaptchaCheckResult(true, "");
        } else {
            if (this.answer.length() != requestAnswer.length()) {
                final String error = String.format(
                    "%d characters entered, but should be %d.",
                    requestAnswer.length(), this.answer.length()
                );
                result = new CaptchaCheckResult(false, error);
            } else {
                result = new CaptchaCheckResult(false, "Wrong answer");
            }
        }
        return result;
    }
}
