# CommonsAndroidNative

Monorepo de bibliotecas e SDKs Android reutilizáveis da organização **WGC**, publicados e distribuídos via **GitHub Packages** (`br.com.wgc:*`).

---

## 📦 Estrutura de Módulos

| Módulo | Tipo | Descrição |
| :--- | :--- | :--- |
| [`:app`](./app) | Aplicação (Sample) | Aplicativo de testes e demonstração para validar os componentes da biblioteca localmente. |
| [`:authentication`](./authentication) | Biblioteca Android | Fluxo completo de autenticação e cadastro (Login, Esqueci Minha Senha, Cadastro de Usuário, Endereço e Veículo) integrado com `ds-templates` e Hilt. |
| [`:maps`](./maps) | Biblioteca Android | Módulo commons para serviços de mapas e geolocalização. |
| [`:message`](./message) | Biblioteca Android | Módulo commons para mensageria, chat e notificações. |
| [`:payment`](./payment) | Biblioteca Android | Módulo commons para integrações de fluxo de pagamento. |

---

## 🛠️ Tecnologias e Ferramentas

- **Linguagem**: Kotlin 2.2+ (Target JVM 17)
- **UI Framework**: Jetpack Compose com Material 3
- **Injeção de Dependência**: Dagger Hilt com KSP
- **Arquitetura**: MVVM com Unidirectional Data Flow (UDF), Coroutines e Kotlin Flows
- **Build System**: Gradle 8.x com Version Catalogs (`gradle/libs.versions.toml`)
- **CI/CD**: GitHub Actions com publicação automática de AARs e GitHub Releases
- **Segurança e Manutenção**: Dependabot para atualização semanal de dependências

---

## 🔑 Configuração Local de Credenciais (GitHub Packages)

Para que o Gradle consiga baixar e publicar dependências do GitHub Packages da organização, defina as credenciais de uma das seguintes formas:

### Opção 1: Via variáveis de ambiente
```bash
export GPR_USER="seu-usuario-github"
export GPR_KEY="seu-personal-access-token" # token com permissão read:packages / write:packages
```
*(No Windows PowerShell: `$env:GPR_USER="seu-usuario"` e `$env:GPR_KEY="seu-token"`)*

### Opção 2: No arquivo `local.properties` (não commitado)
Adicione as seguintes linhas ao seu arquivo `local.properties`:
```properties
gpr.user=seu-usuario-github
gpr.key=seu-personal-access-token
```

---

## 🚀 Como Executar

### 1. Compilar o projeto
```bash
./gradlew build
```

### 2. Executar o aplicativo de teste (`:app`)
Instale e execute a aplicação de demonstração diretamente no emulador ou dispositivo conectado:
```bash
./gradlew :app:installDebug
```

### 3. Publicar módulos localmente
Para publicar um módulo no repositório Maven local (`~/.m2/repository`):
```bash
./gradlew :authentication:publishToMavenLocal
```

---

## 🤖 Integração Contínua (CI/CD)

O workflow em [`.github/workflows/android.yaml`](./.github/workflows/android.yaml) é disparado a cada `push` e `pull_request` na branch `master`:
1. **Detecção de Mudanças**: Identifica quais módulos foram modificados (`maps`, `message`, `payment` ou `authentication`).
2. **Compilação e Testes**: Executa `./gradlew build`.
3. **Upload de Artefatos**: Salva os AARs de debug e release nos artefatos da execução.
4. **Publicação Seletiva**: Executa `./gradlew :<modulo>:publish` apenas nos módulos alterados.
5. **Release e Tagging**: Cria automaticamente a tag `v0.0.<run_number>` e a Release no GitHub com os arquivos `.aar` gerados.
