# Módulo `:settings`

Módulo reutilizável para **Configurações Globais do Aplicativo, Aparência e Atualizações In-App**.

---

## 🎯 Objetivo e Funcionalidades

- **Aparência & Temas**: Troca dinâmica entre Tema Claro, Escuro e Seguir Sistema.
- **Idioma Individual**: Suporte ao Per-app Language Preferences do Android 13+.
- **Preferências de Notificação**: Ativar/desativar alertas por categoria (Promoções, Pedidos, Segurança).
- **In-App Updates (Google Play)**: Notificação e atualização do aplicativo em tempo de execução.
- **Gerenciamento de Cache**: Limpeza de cache temporário de imagens e dados locais.

---

## 📦 Como Usar

```kotlin
dependencies {
    implementation(project(":settings"))
}
```
