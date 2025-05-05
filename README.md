# Weather Service

Микросервис для хранения и предоставления информации о температуре в городах.

## Описание проекта

Weather Service - это микросервис, который собирает и предоставляет информацию о температуре воздуха в различных городах. Сервис интегрируется с несколькими внешними API погоды, усредняет полученные данные и сохраняет их в базу данных для последующего использования.

## Основные возможности

- 📍 Поддержка списка городов с указанием:
  - Названия города
  - Страны
  - Географических координат
- 🌡️ Получение температуры из двух независимых источников:
  - OpenWeatherMap API
  - Open-Meteo API
- 📊 Усреднение значений температуры от обоих источников
- 💾 Сохранение исторических данных в PostgreSQL
- 🔄 Периодическое обновление данных
- 📡 REST API для получения актуальной и исторической информации

## Технологический стек

- Java 21
- Spring Boot 3.2.3
- Liquibase
- PostgreSQL
- Maven
- JUnit 5
- Mockito
- AssertJ
- Docker & Docker Compose

## Требования к системе

- Java 21
- Maven 3.8+
- Docker и Docker Compose
- PostgreSQL (если запуск без Docker)

## Установка и запуск

### Вариант 1: Запуск через Docker (рекомендуется)

1. Клонируйте репозиторий:
```bash
git clone <repository-url>
cd weather-service
```

2. Запустите сервис с помощью Docker Compose:
```bash
docker-compose up -d
```

Сервис будет доступен по адресу: http://localhost:8180

### Вариант 2: Локальный запуск

1. Убедитесь, что у вас установлены все необходимые зависимости
2. Соберите проект:
```bash
mvn clean install
```

3. Запустите приложение:
```bash
mvn spring-boot:run
```

Сервис будет доступен по адресу: http://localhost:8080


## Swagger
1. http://localhost:8180/swagger-ui/index.html#/ - если через Docker
2. http://localhost:8080/swagger-ui/index.html#/ - если локально

## API Endpoints

### 1. Получение температуры

```
GET /api/weather
```

Параметры запроса:
- `city` (обязательный) - название города
- `country` (обязательный) - название страны
- `date` (опциональный) - дата в формате YYYY-MM-DD
- `timezone` (опциональный) - часовой пояс (например, Europe/London, Europe/Moscow)
  - По умолчанию: Europe/Moscow

Примеры запросов:

1. Получение последней температуры:
```
GET /api/weather?city=Moscow&country=Russia
```

2. Получение температуры на конкретную дату
```
GET /api/weather?city=Moscow&country=Russia&date=2025-05-04
```

3. Получение температуры с указанием часового пояса:
```
GET /api/weather?city=London&country=United%20Kingdom&timezone=Europe/London
```
4. Ошибка — город не найден:
```
GET /api/weather?city=UnknownCity&country=France
```
Ответ: 404 CityNotFoundException

Пример корректного ответа:
```json
[
  {
    "city": "London",
    "temperature": 13.40,
    "temperatureUnit": "°C",
    "timestamp": "2025-05-04T16:46:06"
  }
]
```