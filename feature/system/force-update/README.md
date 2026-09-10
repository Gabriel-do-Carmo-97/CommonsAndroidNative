# Módulo `:force-update`

Módulo reutilizável para **Bloqueio por Versão Obsoleta (Force Update) e Tela de Manutenção Remota**.

---

## 🎯 Objetivo e Funcionalidades

- **Force Update**: Exibição de tela bloqueante quando a versão do app no dispositivo é descontinuada no backend, forçando o redirecionamento para a Play Store.
- **Modo Manutenção**: Tela amigável informando instabilidade ou manutenção programada da API/servidores.

---

## 📦 Como Usar

```kotlin
dependencies {
    implementation(project(":force-update"))
}
```
