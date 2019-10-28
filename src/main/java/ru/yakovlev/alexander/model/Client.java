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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Version;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Client entity.
 *
 * @author Yakovlev Alexander (sanyakovlev@yandex.ru)
 * @since 0.1
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Client {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator",
        parameters = {
            @Parameter(
                name = "uuid_gen_strategy_class",
                value = "org.hibernate.id.uuid.CustomVersionOneStrategy"
            )
        }
    )
    @Column(updatable = false, nullable = false)
    @JsonView(Views.Public.class)
    @JsonProperty("public")
    private UUID id;

    @JsonView(Views.Secure.class)
    @Column(nullable = false)
    private UUID secret;

    @Version
    private int version;

    /**
     * Additional constructor for new client.
     * @param secret secret key.
     * @since 0.1
     */
    public Client(final UUID secret) {
        this.secret = secret;
    }

    /**
     * Authenticate by client secret key.
     *
     * @param secretKey secret key.
     * @throws ResponseStatusException if not authenticated.
     * @since 0.1
     */
    public void authenticate(final UUID secretKey)
        throws ResponseStatusException {
        if (!this.secret.equals(secretKey)) {
            throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Client authentication failed: wrong secret key."
            );
        }
    }
}
