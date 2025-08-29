# 🏦 Bank Cards REST API

REST API для управления банковскими картами и пользователями, построенное на Spring Boot, с поддержкой аутентификации и авторизации.

## 📋 Содержание

- [Описание проекта](#-описание-проекта)
- [Архитектура](#%EF%B8%8F-архитектура)
- [Технологический стек](#%EF%B8%8F-технологический-стек)
- [Функциональность](#-функциональность)
- [API Endpoints](#-api-endpoints)
- [Установка и запуск](#-установка-и-запуск)
- [Конфигурация](#%EF%B8%8F-конфигурация)
- [База данных](#%EF%B8%8F-база-данных)
- [Безопасность](#-безопасность)
- [Тестирование](#-тестирование)
- [Документация](#-документация)

## 🎯 Описание проекта

Bank Cards REST API - это полнофункциональное приложение для управления банковскими картами, которое предоставляет:

- **Аутентификация и авторизация** с использованием JWT токенов
- **Управление банковскими картами** (создание, блокировка, активация)
- **Переводы между картами** с проверкой баланса
- **Административные функции** для управления пользователями и картами
- **Безопасное хранение данных** с шифрованием чувствительной информации
- **RESTful API** с полной документацией OpenAPI/Swagger

## 🏗️ Архитектура

### Структура проекта
```
src/
├── main/
│   ├── java/com/example/bankcards/
│   │   ├── config/          # Конфигурации Spring
│   │   ├── controller/      # REST контроллеры
│   │   ├── dto/            # Data Transfer Objects
│   │   ├── entity/         # JPA сущности
│   │   ├── exception/      # Кастомные исключения
│   │   ├── repository/     # JPA репозитории
│   │   ├── security/       # Spring Security
│   │   ├── service/        # Бизнес-логика
│   │   └── util/           # Утилиты
│   └── resources/
│       ├── db/changelog/   # Liquibase миграции
│       └── application.yml # Конфигурация
└── test/                   # Unit и интеграционные тесты
```

### Слои приложения
1. **Controller Layer** - REST API endpoints
2. **Service Layer** - Бизнес-логика
3. **Repository Layer** - Доступ к данным
4. **Entity Layer** - Модели данных
5. **Security Layer** - Аутентификация и авторизация

## 🛠️ Технологический стек

### Основные технологии
- **Java 17** - Основной язык программирования
- **Spring Boot 3.5.4** - Фреймворк для создания приложений
- **Spring Security** - Безопасность и аутентификация
- **Spring Data JPA** - Работа с базой данных
- **PostgreSQL** - Реляционная база данных
- **Liquibase** - Управление миграциями БД

### Дополнительные технологии
- **JWT (JSON Web Tokens)** - Аутентификация
- **Lombok** - Уменьшение boilerplate кода
- **MapStruct** - Маппинг между объектами
- **Swagger/OpenAPI** - Документация API
- **Validation** - Валидация данных
- **Docker** - Контейнеризация

### Инструменты разработки
- **Maven** - Управление зависимостями
- **JUnit 5** - Unit тестирование
- **Mockito** - Мокирование в тестах
- **Git** - Система контроля версий

## 🚀 Функциональность

### Аутентификация и авторизация
- ✅ Регистрация новых пользователей
- ✅ Вход в систему с JWT токенами
- ✅ Обновление токенов доступа
- ✅ Выход из системы
- ✅ Ролевая модель (USER, ADMIN)

### Управление картами
- ✅ Создание новых карт
- ✅ Просмотр списка карт с пагинацией
- ✅ Получение детальной информации о карте
- ✅ Блокировка/активация карт
- ✅ Удаление карт
- ✅ Обновление баланса карт

### Переводы
- ✅ Переводы между собственными картами
- ✅ Проверка достаточности средств
- ✅ Валидация статуса карт
- ✅ Точные расчеты с BigDecimal

### Административные функции
- ✅ Управление всеми картами в системе
- ✅ Создание пользователей администратором
- ✅ Управление статусом пользователей
- ✅ Просмотр всех пользователей

### Безопасность
- ✅ Шифрование номеров карт
- ✅ Хеширование паролей
- ✅ JWT токены с refresh механизмом
- ✅ Валидация входных данных
- ✅ Обработка исключений

## 📡 API Endpoints

### Аутентификация (`/api/v1/auth`)
| Метод | Endpoint | Описание | Роли |
|-------|----------|----------|------|
| POST | `/register` | Регистрация пользователя | Все |
| POST | `/login` | Вход в систему | Все |
| POST | `/refresh` | Обновление токена | Все |
| POST | `/logout` | Выход из системы | Все |

### Пользователи (`/api/v1/user/cards`)
| Метод | Endpoint | Описание | Роли |
|-------|----------|----------|------|
| GET | `/` | Список карт пользователя | USER |
| GET | `/{cardId}` | Детали карты | USER |
| POST | `/{cardId}/block` | Блокировка карты | USER |
| POST | `/transfer` | Перевод между картами | USER |
| GET | `/balance` | Общий баланс пользователя | USER |
| GET | `/card/{cardId}/balance` | Баланс конкретной карты | USER |

### Администраторы (`/api/v1/admin`)
| Метод | Endpoint | Описание | Роли |
|-------|----------|----------|------|
| GET | `/cards` | Все карты в системе | ADMIN |
| PATCH | `/cards/balance` | Обновление баланса карты | ADMIN |
| GET | `/cards/{cardId}` | Детали карты | ADMIN |
| POST | `/cards/create` | Создание новой карты | ADMIN |
| POST | `/cards/{cardId}/block` | Блокировка карты | ADMIN |
| POST | `/cards/{cardId}/activate` | Активация карты | ADMIN |
| DELETE | `/cards/{cardId}/delete` | Удаление карты | ADMIN |
| POST | `/users/create` | Создание пользователя | ADMIN |
| GET | `/users` | Все пользователи | ADMIN |
| GET | `/users/{userId}` | Детали пользователя | ADMIN |
| PATCH | `/users/{userId}/status` | Обновление статуса пользователя | ADMIN |
| PATCH | `/users/{userId}/lock-status` | Блокировка/разблокировка пользователя | ADMIN |
| PATCH | `/users/{userId}` | Обновление данных пользователя | ADMIN |
| DELETE | `/users/{userId}` | Удаление пользователя | ADMIN |

## 🚀 Установка и запуск

### Предварительные требования
- Java 17 или выше
- Maven 3.6+
- PostgreSQL 12+
- Docker (опционально)

### Локальная установка

#### 1. Клонирование репозитория
```bash
git clone https://github.com/LilYxa/Bank_REST.git
cd Bank_REST
```

#### 2. Настройка базы данных
```bash
# Создание базы данных PostgreSQL
createdb bankcards

# Или через psql
psql -U postgres
CREATE DATABASE bankcards;
```

#### 3. Настройка переменных окружения
```bash
# Создание .env файла
cp env.example .env

# Редактирование .env файла
nano .env
```

#### 4. Запуск приложения
```bash
# Сборка проекта
mvn clean install

# Запуск приложения
mvn spring-boot:run
```

### Docker установка

#### 1. Быстрый запуск с Docker
```bash
# Клонирование и настройка
git clone https://github.com/LilYxa/Bank_REST.git
cd Bank_REST
cp env.example .env

# Запуск с Docker Compose
docker-compose up -d
```

#### 2. Проверка работоспособности
```bash
# Проверка статуса контейнеров
docker-compose ps

# Проверка API
curl http://localhost:8080/actuator/health
```

## ⚙️ Конфигурация

### Переменные окружения

| Переменная | Описание | Значение по умолчанию |
|------------|----------|----------------------|
| `USER_LOGIN` | Пользователь PostgreSQL | `bankuser` |
| `USER_PASSWORD` | Пароль PostgreSQL | `bankpass123` |
| `TOKEN_SECRET_KEY` | Секретный ключ JWT | `your-secret-key` |
| `ACCESS_TOKEN_TTL` | Время жизни access токена (мс) | `900000` (15 мин) |
| `REFRESH_TOKEN_TTL` | Время жизни refresh токена (мс) | `86400000` (24 часа) |
| `ENCRYPTION_PASSWORD` | Пароль для шифрования | `your-encryption-password` |
| `ENCRYPTION_SALT` | Соль для шифрования | `your-encryption-salt` |

### Настройки приложения

#### application.yml
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/bankcards
    username: ${USER_LOGIN}
    password: ${USER_PASSWORD}
  
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true

server:
  port: 8080

token:
  signing:
    key: ${TOKEN_SECRET_KEY}
    expirationInMs: ${ACCESS_TOKEN_TTL}

refreshToken:
  expirationInMs: ${REFRESH_TOKEN_TTL}

encryption:
  password: ${ENCRYPTION_PASSWORD}
  salt: ${ENCRYPTION_SALT}
```

## 🗄️ База данных

### Схема базы данных

#### Таблица `users`
- `id` (UUID) - Уникальный идентификатор
- `first_name` (VARCHAR) - Имя
- `last_name` (VARCHAR) - Фамилия
- `patronymic` (VARCHAR) - Отчество
- `email` (VARCHAR, UNIQUE) - Email
- `password` (VARCHAR) - Хешированный пароль
- `role` (ENUM) - Роль (USER/ADMIN)
- `enabled` (BOOLEAN) - Активность аккаунта
- `account_non_locked` (BOOLEAN) - Блокировка аккаунта

#### Таблица `cards`
- `id` (UUID) - Уникальный идентификатор
- `card_number` (VARCHAR) - Зашифрованный номер карты
- `last_four_digits` (VARCHAR) - Последние 4 цифры
- `card_owner` (VARCHAR) - Владелец карты
- `expiry_date` (DATE) - Дата истечения
- `status` (ENUM) - Статус карты (ACTIVE/BLOCKED/EXPIRED)
- `balance` (NUMERIC) - Баланс карты
- `user_id` (UUID, FK) - Ссылка на пользователя

#### Таблица `tokens`
- `id` (UUID) - Уникальный идентификатор
- `token` (VARCHAR, UNIQUE) - JWT токен
- `type` (VARCHAR) - Тип токена
- `revoked` (BOOLEAN) - Отозван ли токен
- `expired` (BOOLEAN) - Истек ли токен
- `expires_at` (TIMESTAMP) - Время истечения
- `user_id` (UUID, FK) - Ссылка на пользователя

## 🔐 Безопасность

### Аутентификация
- **JWT токены** для аутентификации
- **Refresh токены** для обновления доступа
- **Хеширование паролей** с использованием BCrypt
- **Валидация токенов** на каждом запросе

### Авторизация
- **Ролевая модель** (USER, ADMIN)
- **Spring Security** для защиты endpoints
- **PreAuthorize** аннотации для контроля доступа
- **Method-level security** для защиты методов

### Шифрование данных
- **Шифрование номеров карт** в базе данных
- **TextEncryptor** для криптографических операций
- **Безопасное хранение** чувствительных данных

### Валидация
- **Bean Validation** для проверки входных данных
- **Кастомные валидаторы** для бизнес-правил
- **Обработка исключений** с информативными сообщениями

## 🧪 Тестирование

### Типы тестов
- **Unit тесты** - Тестирование отдельных компонентов
- **Integration тесты** - Тестирование взаимодействия компонентов
- **Controller тесты** - Тестирование REST endpoints
- **Service тесты** - Тестирование бизнес-логики

### Запуск тестов
```bash
# Запуск всех тестов
mvn test

# Запуск конкретного теста
mvn test -Dtest=AuthControllerTest

```

### Структура тестов
```
src/test/java/com/example/bankcards/
├── controller/     # Тесты контроллеров
├── service/        # Тесты сервисов
```

## 📚 Документация

### API Документация
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs
- **OpenAPI YAML**: http://localhost:8080/v3/api-docs.yaml

### Контакты
- **Email**: ilia.elland@mail.ru
- **GitHub**: https://github.com/LilYxa
- **Telegram**: @illiyae
