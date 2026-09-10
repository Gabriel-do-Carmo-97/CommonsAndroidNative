# 🧪 Estratégia de Testes do CommonsAndroidNative

Para assegurar estabilidade em produção, interoperabilidade e regressão zero nos mais de 70 submódulos, todas as features e bundles do **CommonsAndroidNative** seguem uma estratégia estrita baseada na pirâmide de testes.

---

## 🏛️ Camadas de Testes

### 1. Testes Unitários (`src/test/`)
- **Foco**: Lógicas de negócio, ViewModels (`UiState`, reduções de evento), Use Cases e Mappers.
- **Ferramentas**: JUnit 4, MockK e Turbine (para validação de `StateFlow` e `SharedFlow`).
- **Diretriz**: Isolamento total do Android Framework. Testes rápidos, determinísticos e sem delays (`UnconfinedTestDispatcher` ou `StandardTestDispatcher`).

### 2. Testes de Integração & Repositórios (`src/test/` ou `src/androidTest/`)
- **Foco**: Repositórios locais (Room Database em memória) e comunicação simulada com OmniBackend (MockWebServer).
- **Diretriz**: Garantir integridade de serialização de payloads e persistência transacional.

### 3. Testes Instrumentados (`src/androidTest/`)
- **Foco**:
  - Validação de inicialização de Singletons de Bundle (`initialize(context)`).
  - Integridade do `AndroidManifest.xml` (permissões declaradas, services, receivers).
  - Testes de UI em Jetpack Compose (`createComposeRule` / `createAndroidComposeRule`).

---

## ⚡ Comandos Úteis

### Executar todos os testes unitários do projeto:
```bash
./gradlew testDebugUnitTest
```

### Executar testes de um bundle específico:
```bash
./gradlew :bundles:ecommerce:pro:testDebugUnitTest
```

### Executar testes instrumentados em emulador ou dispositivo conectado:
```bash
./gradlew connectedDebugAndroidTest
```
