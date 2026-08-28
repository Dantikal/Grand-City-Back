# Grand City — бэкенд (Java Spring Boot + PostgreSQL)

Реализация REST API по ТЗ [`BACKEND_TASK.md`](./BACKEND_TASK.md) / [`API.md`](./API.md).
Стек: **Java 17, Spring Boot 3.3, Spring Data JPA, Spring Security (JWT), PostgreSQL, Flyway**.

Фронтенд ничего не знает про этот бэкенд, кроме HTTP-контракта — см. `API.md`.
Всё сделано так, чтобы просто поднять БД, запустить приложение и подключить фронт через
`NEXT_PUBLIC_API_MODE=live` + `NEXT_PUBLIC_API_URL=http://localhost:8080`.

---

## 1. Что внутри

```
src/main/java/com/grandcity/backend/
  config/       — SecurityConfig (JWT + CORS), WebConfig (раздача /uploads)
  security/     — JwtService, JwtAuthFilter
  entity/       — Property, Agent, ContactRequest, Booking, AdminUser (JPA-сущности)
  repository/   — Spring Data репозитории (+ Specification для фильтров каталога)
  dto/          — DTO с bean-validation аннотациями (полностью повторяют Zod-схемы фронта)
  mapper/       — ручной маппинг Entity <-> DTO
  service/      — бизнес-логика
  controller/   — REST-контроллеры (пути 1-в-1 из API.md)
  common/       — GlobalExceptionHandler (единый формат ошибок { "message": "..." })
src/main/resources/
  application.yml           — конфигурация (через переменные окружения)
  db/migration/V1__...sql   — схема БД (Flyway)
  db/migration/V2__...sql   — сиды: 4 агента + 3 объекта из мок-данных фронта + админ
```

Данные приходят/уходят в точности в формате из `API.md` (camelCase JSON,
`coordinates: { lat, lng }`, ISO-даты и т.д.) — фронт не нужно менять.

---

## 2. Установка PostgreSQL и создание БД

Если PostgreSQL ещё не установлен — поставьте PostgreSQL 14+ (postgresql.org, либо через
Docker: `docker run --name grand-city-db -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=grand_city -p 5432:5432 -d postgres:16`).

Если PostgreSQL уже есть, просто создайте базу:

```sql
CREATE DATABASE grand_city;
```

Таблицы и сиды создавать вручную **не нужно** — при первом запуске Flyway сам применит
`V1__init_schema.sql` и `V2__seed_data.sql`.

---

## 3. Открытие проекта в IntelliJ IDEA Ultimate

1. `File → Open...` → выбрать папку с этим проектом (там, где `pom.xml`).
2. IDEA сама распознает Maven-проект и подтянет зависимости (нужен интернет при первом
   импорте — Maven скачает Spring Boot, Postgres-драйвер, JJWT, Flyway, Lombok).
3. **Lombok**: установите плагин Lombok (`Settings → Plugins → Lombok`, обычно уже
   встроен) и включите annotation processing:
   `Settings → Build, Execution, Deployment → Compiler → Annotation Processors` →
   ✅ Enable annotation processing.
4. Убедитесь, что Project SDK = Java 17 (`File → Project Structure → Project`).

### Переменные окружения

Приложение читает настройки из переменных окружения (см. `application.yml`), но у всех
есть разумные значения по умолчанию для локальной разработки:

| Переменная | По умолчанию | Что это |
|---|---|---|
| `DB_URL` | `jdbc:postgresql://localhost:5432/grand_city` | строка подключения к БД |
| `DB_USERNAME` | `postgres` | пользователь БД |
| `DB_PASSWORD` | `postgres` | пароль БД |
| `PORT` | `8080` | порт приложения |
| `JWT_SECRET` | (тестовый) | секрет для подписи JWT — **обязательно смените в проде**, минимум 32 символа |
| `JWT_EXPIRATION_MINUTES` | `1440` (сутки) | срок жизни токена |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:3000` | через запятую, origin(ы) фронтенда |
| `UPLOADS_DIR` | `uploads` | папка на диске для загруженных файлов |
| `PUBLIC_BASE_URL` | `http://localhost:8080` | базовый URL, который подставляется в ответ `/uploads` (`{url}`) |

Если пароль/логин от вашей локальной Postgres отличаются от `postgres/postgres` —
задайте `DB_USERNAME` / `DB_PASSWORD` в конфигурации запуска IntelliJ
(`Run → Edit Configurations → Environment variables`), либо поправьте значения по
умолчанию прямо в `application.yml`.

### Запуск

Запустите `GrandCityBackendApplication.main()` (зелёная стрелка в IntelliJ — это самый
простой способ, Maven-зависимости IDE подтянет сама). Либо из терминала, если у вас
установлен Maven (`mvn -v` для проверки):

```bash
mvn spring-boot:run
```

При первом старте Flyway создаст таблицы и засеет 4 агентов + 3 объекта (те же, что в
мок-данных фронта: Osipenko Club House, Semak Residence, Fuchika Park Residence), плюс
одного админа.

---

## 4. Админ по умолчанию

```
логин:  admin
пароль: admin123
```

Захардкожен только для старта. **Смените пароль**, сгенерировав новый bcrypt-хэш и
обновив запись в таблице (или добавив свою миграцию `V3__...sql`):

```sql
UPDATE admin_users SET password_hash = '<новый bcrypt-хэш>' WHERE username = 'admin';
```

Хэш можно сгенерировать, например, через IntelliJ Scratch file на Java со
`BCryptPasswordEncoder`, либо через https://bcrypt-generator.com (для теста — на проде
лучше генерировать локально).

---

## 5. Быстрая проверка (curl)

```bash
# каталог
curl http://localhost:8080/properties
curl "http://localhost:8080/properties?category=complex&sort=price-asc"
curl http://localhost:8080/properties/zhk-osipenko    # по slug
curl http://localhost:8080/properties/prop-osipenko   # по id

# агенты
curl http://localhost:8080/agents

# заявка (публично)
curl -X POST http://localhost:8080/requests \
  -H "Content-Type: application/json" \
  -d '{"name":"Иван Иванов","email":"ivan@example.com","kind":"general","message":"Интересует ЖК Осипенко, можно подробнее?"}'

# логин
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
# → { "token": "eyJ..." }

# защищённый эндпоинт
TOKEN="<вставьте токен>"
curl http://localhost:8080/requests -H "Authorization: Bearer $TOKEN"

# загрузка файла
curl -X POST http://localhost:8080/uploads \
  -H "Authorization: Bearer $TOKEN" \
  -F "file=@/path/to/photo.jpg"
# → { "url": "http://localhost:8080/uploads/<uuid>.jpg" }
```

---

## 6. Подключение фронтенда

В `.env.local` фронтенда:

```
NEXT_PUBLIC_API_MODE=live
NEXT_PUBLIC_API_URL=http://localhost:8080
```

Перезапустить `npm run dev`. Убедитесь, что `CORS_ALLOWED_ORIGINS` бэкенда содержит
origin фронта (по умолчанию `http://localhost:3000` уже включён).

---

## 7. Соответствие ТЗ / чеклист приёмки

- [x] `POST /auth/login` → `{ token }`; без токена защищённые ручки → 401 (JSON `{ "message": ... }`).
- [x] `GET /properties` — фильтры `query, category, listingType, kind, minPrice, maxPrice,
      beds, featured, sort, limit`, все комбинируются через AND.
- [x] `GET /properties/:idOrSlug`, `GET /agents/:idOrSlug` — поиск и по id, и по slug.
- [x] `POST/PUT/DELETE /properties` и `/agents` — под JWT.
- [x] `POST /requests`, `POST /bookings` — публично, валидация как во фронтовых Zod-схемах.
- [x] `GET/PATCH/DELETE /requests`, `GET /bookings` — под JWT.
- [x] `POST /uploads` — multipart `file`, только изображения, лимит 5MB, `{ url }`,
      файл реально открывается по этому URL (раздаётся статикой из `UPLOADS_DIR`).
- [x] CORS настраивается через `CORS_ALLOWED_ORIGINS`.
- [x] Ошибки — `{ "message": "..." }` (+ `errors` при 400 валидации), коды 400/401/404/500.
- [x] Сиды совпадают с мок-данными фронта (3 ЖК, 4 агента).

## 8. Важные заметки

- **id/slug** для `Property`/`Agent`: если фронт присылает свои `id`/`slug` в `POST` —
  они используются как есть (после проверки уникальности `slug`, при конфликте
  добавляется суффикс `-2`, `-3`...). Если не присылает — генерируются на бэке.
- **images / features / specialties / areas** хранятся как `text[]` в Postgres и
  отдаются как обычный JSON-массив строк — как и ожидает фронт.
- Для продакшена: смените `JWT_SECRET`, пароль админа, ограничьте `CORS_ALLOWED_ORIGINS`
  ровно доменом сайта, поставьте `spring.jpa.hibernate.ddl-auto=validate` (уже так) и
  переключите хранение загрузок на S3-совместимое хранилище при необходимости
  (сейчас — локальный диск, `UploadService`).
