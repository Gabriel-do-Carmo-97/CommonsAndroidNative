# Módulo `:biometric`

Módulo reutilizável para **Autenticação Biométrica, Teclado Numérico de PIN e Bloqueio de Segurança (App Lock)**.

---

## 🎯 Objetivo e Funcionalidades

- **BiometricPrompt Nativo**: Leitura de Impressão Digital e Reconhecimento Facial.
- **Teclado de PIN Customizável**: Componente de digitação de código numérico de 4 ou 6 dígitos.
- **App Lock**: Bloqueio de tela após tempo de inatividade em segundo plano.
- **Proteção Anti-Screenshot (`FLAG_SECURE`)**: Prevenção de captura e gravação de tela em dados confidenciais.

---

## 📦 Como Usar

```kotlin
dependencies {
    implementation(project(":biometric"))
}
```
