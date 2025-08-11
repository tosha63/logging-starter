# logging-starter

## 1. Назначение и состав 

- Стартер предназначен:
1) для логгирования времени выполнения методов
2) для логгирования входящих и исходящих HTTP-запросов


## 2. Подключение стартера в проект

### 2.1. Требования к используемой инфраструктуре

- Java **17**
- SpringBoot **3.2.5**

### 2.2. Gradle

```yaml
repositories {
  mavenLocal()
  mavenCentral()
  maven {
  url = uri("https://raw.githubusercontent.com/tosha63/logging-starter/master/maven-repo")
  }
}

implementation "ru.shtanko:logging-starter:${версия_клиента}"
```

## 3. Пример конфигурирования стартера

### 3.1. Пример *application.yml*

```yaml
logging:
  log-exec-time: true
  web-logging:
    enabled: true
    log-body: true
    exclude-endpoints:
      - /link-shortener/api/v1/**
  mask:
    headers:
      - content-length
      - postman-token
      - cache-control
      - user-agent
      - accept-encoding
      - content-length
```

| Параметр                                  | Назначение                                                                                                                                     |
|-------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------------------|
| **logging.log-exec-time**                 | При выставление значения в `true` включает логгирование времени выполнения методов, которые помечены @LogExecutionTime. По умолчанию выключена |
| **logging.web-logging.enabled**           | При выставление значения в `true` включает логгирование входящих и исходящих HTTP-запросов. По умолчанию включена                              |
| **logging.web-logging.log-body**          | При выставление значения в `true` включает логгирование тела входящих HTTP-запросов. По умолчанию выключена                                    |
| **logging.web-logging.exclude-endpoints** | Можно указать список эндпоинтов, для которых будет отключено логгирование входящих и исходящих HTTP-запросов                                   |
| **logging.mask.headers**                  | Можно указать список заголовков, для которых стоит применить маскирование, если это необходимо                                                 |

