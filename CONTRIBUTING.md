# Guia de Contribuicao â€” CommonsAndroidNative ðŸš€

Obrigado pelo seu interesse em contribuir com o **CommonsAndroidNative**!
Este monorepo reune os modulos funcionais reutilizaveis e bundles comerciais da organizacao **WGC**, operando sob os mais altos padroes de engenharia, Clean Architecture e governanca de agentes especialistas.

---

## ðŸ›ï¸ Principios Arquiteturais Obrigatorios

1. **Clean Architecture Estrita:**
   - Camadas `domain` (modelos, contratos de repository e use cases) 100% puras, sem dependencia de frameworks Android.
   - Camadas `data` implementando repositorios locais e remotos (OmniBackend).
   - Camadas `presentation` com State Hoisting em Jetpack Compose e `StateFlow` em ViewModels com Hilt.
2. **Design System Unificado:**
   - Todos os componentes visuais devem consumir exclusivamente os tokens e componentes do **DesignSystemAndroid** (`core-ds`).
   - Cores ou dimensoes arbitrarias hardcoded sao terminantemente proibidas.
3. **KDoc Obrigatorio (100% de Cobertura):**
   - Toda classe, interface, funcao e propriedade publica ou interna deve possuir KDoc com `@param`, `@return` e `@throws`.
4. **Retrocompatibilidade e Estabilidade:**
   - Mudancas que quebrem contratos publicos das bibliotecas e bundles nao devem ser enviadas sem aprovacao previa do Tech Lead / Orquestrador.

---

## ðŸ› ï¸ Padrao de Commits (Conventional Commits)

Todas as mensagens de commit e titulos de Pull Requests **DEVEM** seguir a especificacao de [Conventional Commits](https://www.conventionalcommits.org/):

- `feat(<escopo>):` Nova funcionalidade (ex: `feat(storefront): add paginated catalog flow`)
- `fix(<escopo>):` Correcao de bug (ex: `fix(checkout): resolve cart total calculation on coupon update`)
- `refactor(<escopo>):` Refatoracao de codigo sem alteracao funcional
- `test(<escopo>):` Adicao ou atualizacao de testes unitarios ou de integracao
- `docs(<escopo>):` Atualizacoes de documentacao e KDocs
- `chore(<escopo>):` Tarefas de build, dependencias ou scripts CI/CD

---

## ðŸ§ª Validacao Local Obrigatoria

Antes de abrir um Pull Request, certifique-se de que todas as validacoes locais passam com sucesso:

```bash
# 1. Analise estatica com Detekt
./gradlew detekt

# 2. Executar testes unitarios
./gradlew testDebugUnitTest

# 3. Compilar AARs de release
./gradlew assembleRelease

# 4. Validar compilacao do showcase app
./gradlew :app:compileDebugKotlin
```

---

## ðŸ”€ Fluxo de Branches e Pull Requests

1. Crie uma branch a partir de `master`:
   ```bash
   git checkout -b feat/<nome-da-feature>
   # ou
   git checkout -b fix/<nome-do-bug>
   ```
2. Realize commits atomicos e semanticos.
3. Envie a branch para o GitHub:
   ```bash
   git push -u origin feat/<nome-da-feature>
   ```
4. Abra um **Pull Request** para `master` preenchendo detalhadamente o template.
5. Aguarde a execucao da esteira hiper-granular de 8 niveis no GitHub Actions.