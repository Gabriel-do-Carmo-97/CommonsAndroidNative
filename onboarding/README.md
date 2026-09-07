# Módulo `:onboarding`

Módulo reutilizável para **Apresentação Inicial, Boas-Vindas e Pedido Inteligente de Permissões (Soft Prompts)** do ecossistema **WGC**.

---

## 🎯 Objetivo e Funcionalidades

- **Carousel de Apresentação (Walkthrough)**: Pagers interativos com ilustrações, animações Lottie e indicador de progresso.
- **Soft Prompts de Permissões**: Exibição de diálogos explicativos e amigáveis antes de disparar os diálogos nativos do sistema (Notificações Android 13+, Câmera, Localização).
- **Seleção de Perfil/Região Inicial**: Opção para o usuário definir seu interesse inicial ou localização ao abrir o app pela primeira vez.

---

## 🏗️ Arquitetura e Estrutura

```
onboarding/
├── navigation/   # Rotas de navegação Compose (OnboardingNavDestinations)
├── screen/       # Telas Compose (OnboardingScreen, SoftPromptScreen)
└── viewmodel/    # ViewModel e gerenciamento de estado (OnboardingViewModel)
```

---

## 📦 Como Usar

### Dependência no Gradle
```kotlin
dependencies {
    implementation(project(":onboarding"))
}
```

### Exemplo no Jetpack Compose
```kotlin
OnboardingScreen(
    onFinishOnboarding = {
        // Redirecionar para login ou home
    }
)
```
