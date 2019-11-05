[![Build Status](https://travis-ci.org/zCRUSADERz/CAPTCHA-service.svg?branch=master)](https://travis-ci.org/zCRUSADERz/Arenaspring)
[![codecov](https://codecov.io/gh/zCRUSADERz/CAPTCHA-service/branch/master/graph/badge.svg)](https://codecov.io/gh/zCRUSADERz/Arenaspring)

# CAPTCHA service
Тестовое задание: HTTP сервис обеспечивающий простой механизм выявления ботов.

#### Реализованые запросы:
1. POST /clients - регистрация нового клиента. 
   * Тело запроса пустое. 
   * Пример ответа: 
{ "public": "93bbf5c8-f223-4d70-83a9-a23a7683c2e9", "secret": "7810ebec-b343-42ef-84da-a8990c6a9125" }
 (public - clientId).
2. POST /clients/{clientId}/captcha - создание новой каптчи.
   * Тело запроса пустое.
   * Ответ отличается в зависимости от выбранного режима сервера, 
   режим TEST возвращает так же и ответ на каптчу. 
   Пример ответа: { "captchaId" : 1, "answer" : "qwerty" } 
   или Long идиентификатор в виде строки "341".
3. GET /clients/{clientId}/captcha/{captchaId} - возвращает PNG рисунок каптчи.
4. POST /clients/{clientId}/captcha/{captchaId}/solve - запрос на решение каптчи.
   * В теле запроса отправляем строку с ответом на каптчу.
   * Ответ Long идентификатор токена в виде строки "123".
5. POST /clients/{clientId}/captcha/{captchaId}/tokens/{tokenId}/activate - активация токена.
   * В теле запроса необходимо отправить JSON с секретным ключом клиента, 
   выданным при регистрации. Пример: { "secret" : "7810ebec-b343-42ef-84da-a8990c6a9125" }
   * Пример ответа: { "success": true, "errorCode": "" }. 
   Означает успешность ответа на каптчу и при неудаче описание ошибки.
   
