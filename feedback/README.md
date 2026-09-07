# Módulo `:feedback`

Módulo reutilizável para **Coleta de Feedback, Pesquisas NPS e Google In-App Review**.

---

## 🎯 Objetivo e Funcionalidades

- **Google In-App Review**: Acionamento do modal de avaliação de 5 estrelas nativo da Play Store sem fechar o app.
- **Formulário de Feedback & NPS**: Modal com avaliação de estrelas e campo de texto com anexo de captura de tela.
- **Shake-to-Report**: Detecção de balanço do aparelho para reporte fácil de bugs em builds de Staging/QA.

---

## 📦 Como Usar

```kotlin
dependencies {
    implementation(project(":feedback"))
}
```
