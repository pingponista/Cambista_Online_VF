# 💱 Cambista Online VF — Plataforma Fintech de Cambio de Divisas
## Migración Arquitectural: Del Monolito Modular Hexagonal a Microservicios Distribuidos Cloud-Native

> **Contexto del Proyecto Base Replicado:**
> Esta solución (`cambista_online_vf`) es la evolución y migración a microservicios del proyecto base **[CambistaOnline (Rama: `Google-Github-Facebok-MFA`)](https://github.com/pingponista/cambista_online/tree/Google-Github-Facebok-MFA)**. 
> El proyecto origen ya implementaba una **Arquitectura Hexagonal (Ports & Adapters / DDD)** validada con ArchUnit, **autenticación social (Google, GitHub, Facebook)**, **doble factor de autenticación (2FA / MFA TOTP RFC 6238 con Google Authenticator)** y mensajería orientada a eventos con **Apache Kafka y RabbitMQ**.
> 
> **El objetivo de esta migración (`cambista_online_vf`):** Desacoplar el monolito modular hexagonal en un **ecosistema distribuido de microservicios autónomos Cloud-Native**, implementando el patrón **Database-per-Service** sobre **Neon Serverless PostgreSQL**, transacciones distribuidas con **Orquestación SAGA y compensaciones**, **Spring Cloud Gateway** perimetral reactivo con CORS global, **Apache Kafka KRaft** (Zookeeper-less) y una pila completa de observabilidad con **Prometheus, Grafana y Tempo**.

---

## 📑 Tabla de Contenidos
1. [Visión General de la Arquitectura](#-visión-general-de-la-arquitectura)
2. [Tabla Comparativa: Monolito Hexagonal vs. Microservicios Distribuidos](#-tabla-comparativa-monolito-hexagonal-vs-microservicios-distribuidos)
3. [Fundamentos y Decisiones de la Migración](#-fundamentos-y-decisiones-de-la-migración)
   - [Bounded Contexts y Descomposición de Dominios Hexagonales](#1-bounded-contexts-y-descomposición-de-dominios-hexagonales)
   - [Patrón Database-per-Service (Neon Serverless PostgreSQL)](#2-patrón-database-per-service-neon-serverless-postgresql)
   - [Transacciones Distribuidas: Orquestación SAGA con Compensación](#3-transacciones-distribuidas-orquestación-saga-con-compensación)
   - [Evolución de Mensajería: De Broker Dual a Kafka KRaft](#4-evolución-de-mensajería-de-broker-dual-a-kafka-kraft)
   - [Clasificación CAP / PACELC y Resiliencia con Redis](#5-clasificación-cap--pacelc-y-resiliencia-con-redis)
   - [Seguridad Distribuida: OAuth2 y Doble Factor (2FA TOTP con Google Authenticator)](#6-seguridad-distribuida-oauth2-y-doble-factor-2fa-totp-con-google-authenticator)
4. [Estructura del Repositorio](#-estructura-del-repositorio)
5. [Matriz de Puertos y Coordinación del Sistema](#-matriz-de-puertos-y-coordinación-del-sistema)
6. [Guía para Levantar en Entorno Local (Paso a Paso)](#-guía-para-levantar-en-entorno-local-paso-a-paso)
   - [Requisitos Previos](#requisitos-previos)
   - [Paso 1: Configurar las 4 Bases de Datos en Neon (Gratis)](#paso-1-configurar-las-4-bases-de-datos-en-neon-gratis)
   - [Paso 2: Variables de Entorno del Backend (`.env`)](#paso-2-variables-de-entorno-del-backend-env)
   - [Paso 3: Variables de Entorno del Frontend (`frontend/.env`)](#paso-3-variables-de-entorno-del-frontend-frontendenv)
   - [Paso 4: Iniciar la Infraestructura y Microservicios con Docker](#paso-4-iniciar-la-infraestructura-y-microservicios-con-docker)
   - [Paso 5: Iniciar el Frontend React + Vite](#paso-5-iniciar-el-frontend-react--vite)
7. [Funcionamiento del Doble Factor (2FA) y Redes Sociales](#-funcionamiento-del-doble-factor-2fa-y-redes-sociales)
8. [Observabilidad, Métricas y Trazas Distribuidas](#-observabilidad-métricas-y-trazas-distribuidas)
9. [Comandos Útiles de Diagnóstico](#-comandos-útiles-de-diagnóstico)

---

## 🏛 Visión General de la Arquitectura

```mermaid
flowchart TB
    subgraph ClientLayer["Capa de Cliente & Visualización"]
        UI["Frontend React 18 + Vite\n(Glassmorphism Fintech UI)\n:3000"]
        GRAFANA["Grafana Dashboard\n(Métricas & Trazas Waterfall)\n:3001"]
    end

    subgraph EdgeLayer["Capa de Entrada Perimetral (Edge Gateway)"]
        GW["Spring Cloud Gateway\n(Global CORS, Routing Reactivo, Correlation-ID)\n:8080"]
    end

    subgraph CoreServices["Ecosistema de Microservicios Autónomos (Spring Boot 3.3 / Java 17)"]
        AUTH["auth-service\n(OAuth2 Google/GitHub, JWT, 2FA TOTP)\n:8081"]
        RATE["exchange-rate-service\n(Motor SBS, Spreads, Promos, AP Cache)\n:8082"]
        WALLET["wallet-ledger-service\n(Libro Mayor Partida Doble, Billeteras)\n:8083"]
        TRX["transaction-service\n(Orquestador SAGA, Órdenes de Cambio)\n:8084"]
    end

    subgraph ObservabilityLayer["Pila de Observabilidad y Telemetría"]
        PROM["Prometheus 2.53\n(Métricas de Rendimiento y Tráfico)\n:9090"]
        TEMPO["Grafana Tempo 2.5\n(Trazabilidad Distribuida OTLP)\n:3200 / :4317 / :4318"]
    end

    subgraph MessagingLayer["Mensajería Asíncrona & Caché"]
        KAFKA["Apache Kafka KRaft (Zookeeper-less)\nTopics: users.registered, orders.created, orders.completed\n:9092"]
        REDIS["Redis 7 (In-Memory Cache)\nTasa de Cambio Contingente\n:6379"]
    end

    subgraph CloudDatabases["Persistencia Aislada: Database-per-Service (Neon Serverless PostgreSQL)"]
        DB_AUTH[("cambista_auth_db\n(tb_users, credentials, oauth)")]
        DB_RATE[("cambista_rate_db\n(tb_rates, spreads, rules)")]
        DB_WALLET[("cambista_wallet_db\n(tb_wallets, tb_ledger_entries)")]
        DB_TRX[("cambista_transaction_db\n(tb_exchange_orders, audit)")]
    end

    UI -->|HTTP /api/v1| GW
    GW -->|/api/v1/auth/**| AUTH
    GW -->|/api/v1/rates/**| RATE
    GW -->|/api/v1/wallets/**| WALLET
    GW -->|/api/v1/orders/**| TRX

    AUTH -->|Events: user.registered| KAFKA
    TRX -->|SAGA Events: order.created| KAFKA
    WALLET -->|Consume & Settle| KAFKA
    KAFKA -->|Ack & Complete| TRX

    TRX -.->|Sync Lock/Verify Funds| WALLET
    RATE -.->|Cache Fallback| REDIS

    AUTH --> DB_AUTH
    RATE --> DB_RATE
    WALLET --> DB_WALLET
    TRX --> DB_TRX

    CoreServices -.->|Export OTLP Traces| TEMPO
    CoreServices -.->|Scrape Actuator /prometheus| PROM
    PROM --> GRAFANA
    TEMPO --> GRAFANA
```

---

## ⚖ Tabla Comparativa: Monolito Hexagonal vs. Microservicios Distribuidos

| Dimensión Arquitectural | Monolito Hexagonal Base ([`Google-Github-Facebok-MFA`](https://github.com/pingponista/cambista_online/tree/Google-Github-Facebok-MFA)) | Nueva Arquitectura Distribuida (`cambista_online_vf`) | Fundamentación Técnica del Cambio |
| :--- | :--- | :--- | :--- |
| **Topología y Despliegue** | Monolito modular en un único artefacto JAR (`cambista-backend:8080`) empaquetando los módulos `auth`, `engine` y `order`. | **Ecosistema de 4 Microservicios Autónomos** (`:8081` a `:8084`) + **Edge API Gateway** (`:8080`) + Módulo transversal `common-proto`. | Despliegues independientes con ciclo de vida desacoplado. El motor de cotizaciones puede escalar horizontalmente sin replicar autenticación o contabilidad. |
| **Arquitectura de Software Interna** | **Arquitectura Hexagonal (Ports & Adapters)** validada por ArchUnit, con puertos `inbound`/`outbound` en un solo runtime. | **Arquitectura Hexagonal mantenida dentro de cada Bounded Context** pero desacoplada a nivel de proceso de sistema operativo y red. | Preserva la pureza de dominio (DDD) y el desacoplamiento de frameworks; cada hexágono es un servicio independiente e invocable por red. |
| **Patrón de Persistencia** | Persistencia híbrida acoplada al mismo proceso: **PostgreSQL** para transacciones y **MongoDB Atlas** para identidades de usuario. | **Database-per-Service Estricto** con **4 Bases de Datos lógicas independientes en Neon Serverless PostgreSQL**. | Elimina el riesgo de "Base de Datos Compartida" (*Shared Database Anti-pattern*). Garantiza soberanía de datos y escalabilidad serverless sin bloqueos cruzados. |
| **Gestión de Esquemas de BD** | Scripts Flyway centralizados (`V1__...` a `V7__...`) ejecutados en un solo arranque. | **Flyway descentralizado e independiente** en el `resources/db/migration` de cada microservicio. | Migraciones aisladas. Un cambio en las tablas de spreads de cotización jamás interrumpe la disponibilidad de la base de datos de usuarios o transacciones. |
| **Transacciones de Negocio** | Transacciones ACID locales gestionadas por `@Transactional` en memoria sobre el mismo pool de conexiones. | **Patrón SAGA Orquestado con Transacciones de Compensación** (`lockFunds` $\rightarrow$ `settleFunds` / `unlockFunds`). | En microservicios se evitan transacciones distribuidas XA (2PC) por latencia y bloqueos. SAGA garantiza consistencia eventual resiliente. |
| **Capa de Entrada y Ruteo** | Sin API Gateway. El frontend se comunicaba directamente al único puerto del backend monolítico (`:8080`). | **Spring Cloud Gateway Reactivo (Netty)** en `:8080` con configuración de **CORS Global**, filtro de trazas y balanceo dinámico. | Oculta la topología interna, elimina problemas de CORS entre microservicios y actúa como punto centralizado de seguridad perimetral. |
| **Infraestructura de Mensajería** | Broker dual acoplado: **Apache Kafka 7.5.0** (requería Zookeeper) + **RabbitMQ 3.13** para correos de bienvenida. | **Apache Kafka 7.6 KRaft Puro (Zookeeper-less)** con eventos de dominio transaccionales e idempotencia (`tb_processed_events`). | Reduce la complejidad operativa eliminando Zookeeper y unificando el bus de eventos en una arquitectura Event-Driven pura de alto rendimiento. |
| **Tolerancia y Disponibilidad (CAP / PACELC)** | Modelo no diferenciado: una caída de la base de datos o de proveedores externos degradaba toda la aplicación. | **CP (Consistencia Lineal)** para Wallet y Transacciones; **AP (Alta Disponibilidad con Caché Redis)** para Tasas de Cambio. | Cumple principios financieros: la integridad del dinero no se transige (CP), mientras que la consulta de cotizaciones puede tolerar lecturas cacheadas ante fallos (AP). |
| **Seguridad: Social OAuth2** | Implementación de adaptadores para Google, GitHub y Facebook dentro del mismo monolito. | Adaptadores optimizados en `auth-service` con **timeouts estrictos de 5s, fallback resiliente de desarrollo** y secretos protegidos detrás del Gateway. | Garantiza que caídas de red o credenciales de prueba nunca congelen la interfaz de usuario del frontend. |
| **Seguridad: 2FA TOTP (RFC 6238)** | Algoritmo TOTP con ventana `WINDOW = 4` (±120s), pero con vulnerabilidad de regeneración de secreto al consultar `/setup`. | **Secreto TOTP inmutable y persistente**; soporte completo con Google Authenticator y **ventana ampliada a ±7 min (14 pasos)**. | Resuelve la pérdida del secreto al refrescar la pantalla y absorbe el desvío de reloj (*clock drift*) común entre dispositivos móviles y máquinas locales. |
| **Frontend Web** | React 18 + Vite con llamadas directas al puerto del monolito. | **React 18 + Vite** modernizado (Glassmorphism Dark-Mode), estado centralizado con Zustand y proxy reverso `/api` hacia el Gateway. | Interfaz fintech moderna con retroalimentación visual inmediata, gestión reactiva de órdenes y compatibilidad total con 2FA y OAuth. |

---

## 🧠 Fundamentos y Decisiones de la Migración

### 1. Bounded Contexts y Descomposición de Dominios Hexagonales
En el proyecto base (`cambista_online`), la arquitectura hexagonal encapsulaba el dominio con puertos y adaptadores pero compartía el mismo runtime de Spring Boot (`CambistaApplication`). En `cambista_online_vf`, cada **Contexto Delimitado (DDD)** fue promovido a un microservicio independiente con su propia arquitectura hexagonal interna:
- **`auth-service`**: Bounded Context de Identidad y Acceso (IAM). Administra usuarios, contraseñas encriptadas con BCrypt, emisión de JWT, secretos TOTP para Google Authenticator e integración OAuth2 federada (Google, GitHub, Facebook) detrás del Gateway.
- **`exchange-rate-service`**: Bounded Context de Cotizaciones. Motor algorítmico que calcula el tipo de cambio final aplicando precio base SBS, spread de cliente (Estándar/Preferente), horario bancario y promociones estacionales.
- **`wallet-ledger-service`**: Bounded Context Financiero y de Saldos. Implementa un **Libro Mayor de Partida Doble inmutable** (*Double-Entry Bookkeeping*). Toda operación genera un asiento contable (`tb_ledger_entries`) donde no se modifican filas históricas, garantizando auditoría bancaria.
- **`transaction-service`**: Bounded Context de Operaciones de Cambio. Orquesta el ciclo de vida de cada orden de cambio mediante una máquina de estados finitos (`PENDING`, `FUNDS_LOCKED`, `COMPLETED`, `FAILED`, `CANCELLED`).
- **`common-proto`**: Librería compartida que contiene contratos de eventos (`OrderCreatedEvent`, `OrderCompletedEvent`, `UserRegisteredEvent`) y DTOs comunes, eliminando el acoplamiento directo de código fuente entre microservicios.

### 2. Patrón Database-per-Service (Neon Serverless PostgreSQL)
- Se implementó el patrón estricto **Database-per-Service** con **4 bases de datos relacionales lógicas aisladas en Neon Serverless PostgreSQL**:
  1. `cambista_auth_db`: Almacena `tb_users`, credenciales, cuentas OAuth vinculadas y secretos TOTP.
  2. `cambista_rate_db`: Almacena pares de divisas, reglas de spread y promociones.
  3. `cambista_wallet_db`: Almacena billeteras multimoneda (PEN, USD, EUR), libro mayor y tabla de idempotencia `tb_processed_events`.
  4. `cambista_transaction_db`: Almacena órdenes de cambio y logs de auditoría transaccional.
- Se eliminaron las llaves foráneas (`FOREIGN KEY`) cruzadas entre servicios; las entidades se correlacionan mediante identificadores universales de negocio (`userEmail`, `orderNumber`, `correlationId`).
- Cada microservicio gestiona sus propias migraciones con **Flyway independiente**, permitiendo evolucionar esquemas sin dependencias.

### 3. Transacciones Distribuidas: Orquestación SAGA con Compensación
1. `transaction-service` crea la orden en estado `PENDING` en `cambista_transaction_db`.
2. Solicita el bloqueo preventivo del saldo en origen (`USD`) a `wallet-ledger-service`.
3. `wallet-ledger-service` bloquea los fondos dentro de una transacción ACID local y emite un evento `order.created` al broker de Kafka.
4. Si la conversión se confirma, se asientan las partidas dobles en el ledger contable y la orden pasa a `COMPLETED`.
5. Si ocurre algún error en la cotización o liquidación, se ejecuta la **transacción de compensación** (`unlockFunds`), liberando el saldo retenido sin inconsistencias financieras y sin bloqueos de dos fases (2PC).

### 4. Evolución de Mensajería: De Broker Dual a Kafka KRaft
- En `cambista_online_vf` se unificó la mensajería asíncrona en **Apache Kafka 7.6 en modo KRaft (Zookeeper-less)**, reduciendo drásticamente la huella de memoria y la complejidad operativa.
- Se implementó un mecanismo de **idempotencia y deduplicación** en los consumidores mediante la tabla `tb_processed_events`, evitando procesamiento duplicado ante reintentos de red.

### 5. Clasificación CAP / PACELC y Resiliencia con Redis
- **Wallet & Transaction (`CP` en CAP / `PC/EC` en PACELC)**: Durante una partición de red, el sistema prefiere denegar la operación antes que permitir un saldo negativo o un gasto duplicado.
- **Exchange Rate (`AP` en CAP / `PA/EL` en PACELC)**: Si la comunicación con la base de datos de tasas experimenta latencia o caída, el servicio recurre inmediatamente a **Redis Cache (puerto :6379)** sirviendo la última cotización válida para mantener alta disponibilidad en el cliente.
- **Timeouts Controlados**: Todas las llamadas de salida a proveedores externos (Google, GitHub, Facebook) tienen un timeout estricto de 5000 ms para evitar que los hilos de Tomcat queden bloqueados.

---

## 📂 Estructura del Repositorio

```text
cambista_online_vf/
├── pom.xml                               # POM padre Maven Multi-Módulo (Java 17)
├── docker-compose.neon.yml               # Orquestación Docker conectada a Neon Serverless
├── .env.example                          # Plantilla segura de variables de entorno del backend
├── common-proto/                         # Módulo compartido de contratos de eventos y DTOs
├── auth-service/                         # Microservicio de Autenticación, OAuth2 y 2FA TOTP (:8081)
│   ├── Dockerfile
│   └── src/main/resources/db/migration/  # Migraciones Flyway para cambista_auth_db
├── exchange-rate-service/                # Microservicio de Cotizaciones SBS y Spreads (:8082)
│   ├── Dockerfile
│   └── src/main/resources/db/migration/  # Migraciones Flyway para cambista_rate_db
├── wallet-ledger-service/                # Microservicio de Billeteras y Libro Mayor (:8083)
│   ├── Dockerfile
│   └── src/main/resources/db/migration/  # Migraciones Flyway para cambista_wallet_db
├── transaction-service/                  # Microservicio de Órdenes y Orquestación SAGA (:8084)
│   ├── Dockerfile
│   └── src/main/resources/db/migration/  # Migraciones Flyway para cambista_transaction_db
├── api-gateway/                          # Spring Cloud Gateway (CORS global y ruteo reactivo) (:8080)
│   └── Dockerfile
├── observability/                        # Pila de Observabilidad y Telemetría
│   ├── prometheus/                       # Configuración de scrapes de Prometheus (:9090)
│   ├── tempo/                            # Configuración de almacenamiento de trazas Tempo (:3200)
│   └── grafana/                          # Dashboards y datasources autoprovisionados (:3001)
├── frontend/                             # Frontend React 18 + Vite (Glassmorphism UI) (:3000)
│   ├── .env.example                      # Plantilla segura de variables públicas del cliente
│   ├── package.json
│   ├── vite.config.js                    # Proxy reverso /api hacia API Gateway (:8080)
│   └── src/
└── k8s/                                  # Manifiestos de Kubernetes (Deployments, Services, ConfigMaps)
```

---

## 📡 Matriz de Puertos y Coordinación del Sistema

Todos los componentes han sido diseñados y mapeados para correr coordinados en el entorno local **sin colisiones de puertos**:

| Servicio / Componente | Puerto Host | Protocolo | URL / Endpoint Local | Coordinación y Rol en la Arquitectura |
| :--- | :---: | :---: | :--- | :--- |
| **Frontend Web** | **`3000`** | HTTP | [http://localhost:3000](http://localhost:3000) | UI Fintech en React 18 / Vite. Rutea peticiones `/api` al Gateway mediante proxy reverso interno. |
| **Grafana Dashboard** | **`3001`** | HTTP | [http://localhost:3001](http://localhost:3001) | Panel de Observabilidad centralizado. Mapeado a `:3001` para **evitar colisión con el puerto `:3000` del frontend**. Acceso anónimo Admin habilitado. |
| **API Gateway** | **`8080`** | HTTP | [http://localhost:8080/api/v1](http://localhost:8080/api/v1) | Punto de entrada perimetral único. Gestiona CORS global para `localhost:3000`, asigna `X-Correlation-Id` y balancea el tráfico. |
| **Auth Service** | **`8081`** | HTTP | [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html) | Microservicio IAM: JWT, Social Login (Google, GitHub, Facebook) y 2FA TOTP con Google Authenticator. |
| **Exchange Rate Service** | **`8082`** | HTTP | [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html) | Motor de cotizaciones SBS, reglas de margen y promociones con fallback automático en Redis. |
| **Wallet & Ledger Service** | **`8083`** | HTTP | [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html) | Libro mayor contable de partida doble inmutable y saldos multimoneda (PEN, USD, EUR). |
| **Transaction Service** | **`8084`** | HTTP | [http://localhost:8084/swagger-ui.html](http://localhost:8084/swagger-ui.html) | Orquestador SAGA de órdenes de cambio de divisas con transacciones de compensación. |
| **Prometheus Metrics** | **`9090`** | HTTP | [http://localhost:9090](http://localhost:9090) | Servidor de series temporales que recolecta métricas de Actuator (`/actuator/prometheus`) cada 5s. |
| **Apache Kafka Broker** | **`9092`** | PLAINTEXT | `localhost:9092` *(host)* / `kafka:29092` *(docker)* | Bus de eventos transaccionales en modo KRaft (sin Zookeeper). |
| **Redis Cache** | **`6379`** | RESP | `localhost:6379` | Almacén en memoria de alta velocidad para contingencia de cotizaciones SBS. |
| **Grafana Tempo (OTLP)** | **`3200`**<br>`4317`<br>`4318` | HTTP<br>gRPC<br>HTTP | `http://localhost:3200`<br>`tempo:4317` (OTLP gRPC)<br>`tempo:4318` (OTLP HTTP) | Colector de trazas distribuidas OpenTelemetry con soporte para diagramas Waterfall en Grafana. |
| **Neon PostgreSQL** | Cloud SSL | TCP / JDBC | `sa-east-1.aws.neon.tech:5432` | 4 bases de datos lógicas independientes en la nube serverless con SSL obligatorio. |

---

## 🚀 Guía para Levantar en Entorno Local (Paso a Paso)

Sigue estos sencillos pasos para levantar toda la plataforma localmente **sin exponer credenciales sensibles**:

### Requisitos Previos
- **Docker Desktop** (con WSL2 habilitado en Windows o Docker Engine en Linux/macOS).
- **Node.js 18+** y **npm** (para ejecutar el frontend local).
- Una cuenta gratuita en [Neon Console](https://console.neon.tech).

---

### Paso 1: Configurar las 4 Bases de Datos en Neon (Gratis)
1. Ingresa a tu proyecto en [Neon Console](https://console.neon.tech).
2. En el menú lateral, ve a **Databases** $\rightarrow$ **New Database** y crea 4 bases de datos:
   - `cambista_auth_db`
   - `cambista_rate_db`
   - `cambista_wallet_db`
   - `cambista_transaction_db`
3. En la página principal de Neon, copia tu string de conexión PostgreSQL en modo **Pooled** (ejemplo):
   ```text
   postgresql://neondb_owner:npg_xxxxxxxxx@ep-tu-proyecto-pooler.sa-east-1.aws.neon.tech/neondb?sslmode=require
   ```

> [!NOTE]
> **No necesitas crear tablas manualmente.** Cuando los microservicios arranquen, **Flyway ejecutará automáticamente los scripts SQL** y creará todas las tablas e índices en Neon.

---

### Paso 2: Variables de Entorno del Backend (`.env`)
En la raíz del proyecto, copia la plantilla `.env.example` para crear tu `.env` privado (ignorado por Git):

```powershell
cp .env.example .env
```

Abre tu archivo `.env` y coloca el host y la contraseña de tu base de datos de Neon:

```env
# Endpoint de tu proyecto en Neon (usa el host con -pooler)
NEON_HOST=ep-tu-proyecto-pooler.sa-east-1.aws.neon.tech
NEON_USER=neondb_owner
NEON_PASSWORD=tu_password_de_neon_aqui

# Conexiones JDBC por microservicio (Database-per-Service)
SPRING_DATASOURCE_AUTH_URL=jdbc:postgresql://${NEON_HOST}/cambista_auth_db?sslmode=require
SPRING_DATASOURCE_RATE_URL=jdbc:postgresql://${NEON_HOST}/cambista_rate_db?sslmode=require
SPRING_DATASOURCE_WALLET_URL=jdbc:postgresql://${NEON_HOST}/cambista_wallet_db?sslmode=require
SPRING_DATASOURCE_TRX_URL=jdbc:postgresql://${NEON_HOST}/cambista_transaction_db?sslmode=require

SPRING_DATASOURCE_USERNAME=${NEON_USER}
SPRING_DATASOURCE_PASSWORD=${NEON_PASSWORD}

# Seguridad JWT
JWT_SECRET=404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970
JWT_EXPIRATION_MS=3600000

# Kafka
KAFKA_BOOTSTRAP_SERVERS=localhost:9092

# Credenciales OAuth2 para Redes Sociales (Opcionales en local)
# Si se dejan con valores por defecto o 'placeholder', el sistema activa el modo seguro de desarrollo
OAUTH_GOOGLE_CLIENT_ID=google-client-id-placeholder
OAUTH_GOOGLE_CLIENT_SECRET=google-client-secret-placeholder
OAUTH_GITHUB_CLIENT_ID=github-client-id-placeholder
OAUTH_GITHUB_CLIENT_SECRET=github-client-secret-placeholder
```

---

### Paso 3: Variables de Entorno del Frontend (`frontend/.env`)
En la subcarpeta `frontend/`, copia la plantilla `.env.example`:

```powershell
cd frontend
cp .env.example .env
```

El archivo `frontend/.env` solo contiene variables públicas de cliente:

```env
VITE_API_URL=/api/v1
VITE_OAUTH_GOOGLE_CLIENT_ID=tu_google_client_id_publico_si_deseas
VITE_OAUTH_GITHUB_CLIENT_ID=tu_github_client_id_publico_si_deseas
VITE_OAUTH_FACEBOOK_APP_ID=
```

---

### Paso 4: Iniciar la Infraestructura y Microservicios con Docker
En la raíz del proyecto, ejecuta:

```powershell
docker compose -f docker-compose.neon.yml up -d
```

Este comando compilará y levantará los 10 contenedores en segundo plano:
1. `cambista-kafka` & `cambista-redis` (Mensajería y caché).
2. `cambista-prometheus`, `cambista-tempo` & `cambista-grafana` (Observabilidad).
3. `cambista-auth-service`, `cambista-exchange-rate-service`, `cambista-wallet-ledger-service` y `cambista-transaction-service` (Microservicios de negocio).
4. `cambista-api-gateway` (Puerta de enlace perimetral).

Para verificar que todos estén en estado saludable:
```powershell
docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
```

---

### Paso 5: Iniciar el Frontend React + Vite
En otra terminal, entra a la carpeta `frontend`:

```powershell
cd frontend
npm install
npm run dev
```

El servidor Vite arrancará en:
👉 **[http://localhost:3000](http://localhost:3000)**

---

## 🔐 Funcionamiento del Doble Factor (2FA) y Redes Sociales

### ¿Cómo funciona Google Authenticator (TOTP RFC 6238)?
1. **Registro e Inicio Inicial:** Cuando inicias sesión por primera vez (sea con tus credenciales o con un botón de red social como Google o GitHub), la cuenta inicia con **2FA INACTIVO** por defecto para no bloquear al usuario.
2. **Activación de Google Authenticator:**
   - En el Dashboard de la aplicación, haz clic en el menú lateral izquierdo en **"Mi Perfil"**.
   - En la tarjeta **"🛡️ Autenticación en Dos Pasos (2FA)"**, haz clic en el botón azul **"📲 Configurar Google Authenticator"**.
   - Se mostrará un **código QR único**.
   - Abre la aplicación **Google Authenticator** en tu celular, pulsa `+`, selecciona **Escanear código QR** y enfoca la pantalla.
   - Escribe el código de 6 dígitos que aparezca en tu celular y pulsa **"Confirmar y Activar"**. El estado cambiará a **`ACTIVO`** en verde.
3. **Inicio de Sesión Posterior (2FA Obligatorio):**
   - Cada vez que vuelvas a iniciar sesión (por correo/contraseña o por red social), el backend detectará `user.isMfaEnabled() == true` y retornará `{ mfaRequired: true }`.
   - La aplicación abrirá de inmediato la **ventana emergente de verificación (Modal 2FA)** para que ingreses el código de 6 dígitos de tu celular antes de darte acceso a tus fondos.
4. **Resiliencia y Timeouts en Social Login:**
   - Los adaptadores de Google, GitHub y Facebook cuentan con un **timeout estricto de 5 segundos** para llamadas de red hacia proveedores externos y un **fallback automático de desarrollo**, garantizando que la interfaz jamás se congele si la conexión exterior tarda o si se están utilizando credenciales de prueba locales.

---

## 📊 Observabilidad, Métricas y Trazas Distribuidas

La plataforma incluye una pila completa de observabilidad Cloud-Native lista para usar:

### 1. Panel de Control Grafana
- URL: **[http://localhost:3001](http://localhost:3001)**
- Acceso: Pre-autenticado en modo **Admin** (no requiere ingresar clave, o puedes usar `admin` / `admin`).
- Dashboard principal: **`Cambista Online - Observabilidad de 5 Microservicios`** ([http://localhost:3001/d/cambista-fintech-observability/](http://localhost:3001/d/cambista-fintech-observability/cambista-online-observabilidad-de-5-microservicios?orgId=1&refresh=5s)).

### 2. Métricas en Tiempo Real (Prometheus)
- Tasa de peticiones por segundo (RPS) por microservicio.
- Latencia percentil P95 / P99 de transacciones.
- Tasa de errores HTTP 4xx y 5xx.
- Consumo de recursos de JVM y pools de conexiones HikariCP.

### 3. Trazabilidad Distribuida (Grafana Tempo & OpenTelemetry)
- **Visor de Cascada (Waterfall Diagram)**: Inspecciona cada paso de la transacción SAGA (Gateway $\rightarrow$ Transaction Service $\rightarrow$ Wallet Ledger $\rightarrow$ Kafka $\rightarrow$ Base de Datos).
- **Selector de Trace ID**: Puedes pegar cualquier identificador de traza generado en los logs (`traceId=...`) para ver el desglose exacto de milisegundos consumidos por cada componente.

---

## 🛠 Comandos Útiles de Diagnóstico

- **Ver logs en tiempo real de todos los microservicios:**
  ```powershell
  docker compose -f docker-compose.neon.yml logs -f
  ```
- **Ver logs de un servicio específico (ej. autenticación y 2FA):**
  ```powershell
  docker compose -f docker-compose.neon.yml logs -f auth-service
  ```
- **Verificar la salud global desde el Gateway:**
  ```powershell
  curl.exe http://localhost:8080/actuator/health
  ```
- **Reiniciar o reconstruir un servicio tras modificar código:**
  ```powershell
  docker compose -f docker-compose.neon.yml build auth-service
  docker compose -f docker-compose.neon.yml up -d auth-service
  ```
- **Detener el ecosistema completo:**
  ```powershell
  docker compose -f docker-compose.neon.yml down
  ```

---
*Desarrollado con Arquitectura Hexagonal Limpia, Domain-Driven Design (DDD), OpenTelemetry y Patrones de Sistemas Distribuidos para Fintech.*
