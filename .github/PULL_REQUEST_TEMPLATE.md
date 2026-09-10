## 📌 Descrição da Mudança
<!-- Descreva de forma clara e objetiva o que este PR implementa, corrige ou refatora. -->

## 🔗 Issues / Demandas Vinculadas
<!-- Exemplo: Closes #123, Fixes #456 -->

## 🏷️ Tipo de Mudança
- [ ] `feat`: Nova funcionalidade ou novo bundle/feature
- [ ] `fix`: Correção de bug
- [ ] `refactor`: Refatoração sem alteração de comportamento
- [ ] `docs`: Atualização de documentação ou KDoc
- [ ] `test`: Adição ou correção de testes automatizados
- [ ] `chore`: Atualização de dependências, gradle ou CI/CD

## 📋 Checklist de Qualidade (Pro Enterprise Standard)
- [ ] **100% KDoc**: Toda classe, interface, método ou propriedade pública/interna adicionada/modificada possui KDoc completo (`@param`, `@return`, `@throws`).
- [ ] **Convenções de Pacote**: O código segue rigorosamente o namespace `br.com.wgc.*`.
- [ ] **Testes Unitários**: Testes unitários foram adicionados/atualizados em `src/test` e estão passando localmente (`./gradlew testDebugUnitTest`).
- [ ] **Testes Instrumentados**: Testes de lifecycle/contexto foram validados em `src/androidTest`.
- [ ] **Análise Estática**: O código foi validado com o Detekt (`./gradlew detekt`) sem novos erros ou suppressions arbitrárias.
- [ ] **Design System**: Nenhum valor arbitrário de cor (`Color(...)`, hex) ou dimensão (`.dp`, `.sp`) foi inserido sem uso de tokens do `DesignSystemAndroid` (`core-ds`).
