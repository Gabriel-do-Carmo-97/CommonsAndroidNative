# Módulo `:profile`

Módulo reutilizável para **Gestão de Perfil de Usuário, Privacidade e Conformidade com LGPD/GDPR**.

---

## 🎯 Objetivo e Funcionalidades

- **Edição de Perfil**: Alteração de foto de perfil (com crop e compressão), nome, telefone, endereço.
- **Segurança da Conta**: Alterar senha e gerenciamento de dispositivos conectados.
- **Conformidade LGPD**: Fluxo integrado para solicitação de exclusão permanente de conta e exportação de dados pessoais.
- **Central de Ajuda**: Links para Termos de Uso, Política de Privacidade e suporte via WhatsApp/E-mail.

---

## 🏗️ Arquitetura e Estrutura

```
profile/
├── navigation/   # ProfileNavDestinations
├── screen/       # ProfileScreen, EditProfileScreen, PrivacyScreen
└── viewmodel/    # ProfileViewModel (Integrado ao OmniBackend AuthRepository)
```

---

## 📦 Como Usar

```kotlin
dependencies {
    implementation(project(":profile"))
}
```
