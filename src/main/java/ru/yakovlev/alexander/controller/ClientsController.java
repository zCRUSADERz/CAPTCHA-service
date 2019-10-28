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

import com.fasterxml.jackson.annotation.JsonView;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import ru.yakovlev.alexander.model.Client;
import ru.yakovlev.alexander.model.Views;
import ru.yakovlev.alexander.service.ClientService;

/**
 * Clients controller.
 *
 * @author Yakovlev Alexander (sanyakovlev@yandex.ru)
 * @since 0.1
 */
@RestController
@RequestMapping("/clients")
@AllArgsConstructor
public class ClientsController {
    private final ClientService clientService;

    /**
     * Registers a new client, provides a public and secret key for access.
     *
     * @return client principal.
     * @since 0.1
     */
    @JsonView(Views.Secure.class)
    @PostMapping
    public ResponseEntity<Client> register() {
        final Client client = this.clientService.registerClient();
        return ResponseEntity
            .created(
                UriComponentsBuilder
                    .fromUriString("/clients/{clientId}")
                    .build(client.getId())
            )
            .body(client);
    }
}
