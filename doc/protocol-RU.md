# Протокол клиент-серверного взаимодействия в игре Кассандра

## Номер версии

Номер версии протокола, описанного в данном документе — 1.

## Общее для всех методов

Если метод добавляет/изменяет/удаляет данные на сервере только POST
(singup/score)
Если метод требователен к безопасности только POST (signin/signout)
Если метод только читает данные тогда GET
Поле "status" пересекается с http статусами
https://ru.wikipedia.org/wiki/Список_кодов_состояния_HTTP

* 200 — OK
* 400 — Bad Request — Ошибка запроса
* 401 — Unauthorized — Необходима авторизация
* 404 — Not Found — Данные не найдены
* 405 — Method Not Allowed — Пришли с GET на метод требующий POST
* 500 — Internal Server Error — Ошибка сервера
* 501 — Not Implemented — Метод не реализован
и т.д.

В случае не правильного заполнения необходимых полей метод возвращает статус
400 и в body содержится информация об ошибках в формате:
```javascript
{
  status: 400,
  body: {
    "name": {
      /* Тип ошибки */
      "error": "required",
      /* Значение, которое валидировал сервер */
      "value": ""
    },
    "password": {
      /* Тип ошибки */
      "error": "badvalue",
      /* Значение, которое валидировал сервер */
      /* Если кто-то отдаст пароль, будет писать юнит тесты на модели для проверки отсутствия пароля в ответах сервера ;-) */
      "value": ""
    }
  }
}
```

## Регистрация

    /api/v<НОМЕР_ВЕРСИИ>/auth/signup

Принимает только POST запросы

### Пример POST-запроса
```javascript
{
    name: String,
    email: String,
    password: String
}
```
#### Ответ: 200
```javascript
{
    status: 200,
    body: {
        id: 2,
        name: "Vasya",
        email: "vasya@mail.ru",
        password: ""
    }
}
```

## Авторизация

    /api/v<НОМЕР_ВЕРСИИ>/auth/signin

### Пример POST-запроса
```javascript
{
    email: String,
    password: String
}
```
#### Ответ: 200
```javascript
{
    status: 200,
    body: {
        id: 2,
        name: "Vasya",
        email: "vasya@mail.ru",
        password: "",
        score: 100500
    }
}
```

## Проверка авторизации и игрового состояния

    /api/v<НОМЕР_ВЕРСИИ>/auth/check

### Пример GET-запроса

    Без параметров

Ответ сервера будет зависеть от того, в каком игровом состоянии находится пользователь:

#### Ещё не выбрал свою команду
Ответ: 200
```javascript
{
    status: 200,
    body: {
        id: 2,
        name: "Vasya",
        email: "vasya@mail.ru",
        score: 100500,
        team: null
    }
}
```

#### Играет в некоторой команде
Ответ: 200
```javascript
{
    status: 200,
    body: {
        id: 2
        name: "Vasya",
        email: "vasya@mail.ru",
        score: 100500,
        team: 13
    }
}
```

#### Пользователь не залогинен
Ответ: 401
```javascript
{
    status: 401,
    body: {}
}
```

## Сброс авторизации

    /api/v<НОМЕР_ВЕРСИИ>/auth/signout
### Запрос: POST — без параметров
#### Ответ: 200 / 401
```javascript
{
    status: 200,
    body: {}
}
```

## Игровая статистика
    /api/v<НОМЕР_ВЕРСИИ>/scores
### Запрос: GET
```javascript
{
    sort: {
      by: "date",
      order: "asc"
    }
}
```
#### Ответ: 200 / 401
```javascript
{
    status: 200,
    body: {}
}
```

## Команды

### Запрос: GET list

```javascript
[
  {
    artist: {
      id: 13,
      name: "Bob",
      score: 255
    },
    judges: [
      {
        id: 12,
        name: "Dave",
        score: 239
      },
      {
        id: 27,
        name: "Trent",
        score: 127
      }
    ],
    secret: "Cow"
  }
]
