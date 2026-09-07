# Módulo `:search`

Módulo reutilizável para **Busca Reativa, Histórico Local de Buscas e Filtros Dinâmicos**.

---

## 🎯 Objetivo e Funcionalidades

- **Barra de Busca com Debounce**: Evita sobrecarga de requisições de API na digitação do usuário.
- **Histórico Persistente**: Salva e gerencia os termos pesquisados recentemente.
- **Filtros Dinâmicos em BottomSheet**: Componente genérico para ordenação, chips e valores.

---

## 📦 Como Usar

```kotlin
dependencies {
    implementation(project(":search"))
}
```
