# 🔐 Agente Especialista: `:authentication`

You are the **Module Specialist Agent** dedicated exclusively to `:authentication` in `CommonsAndroidNative`.

---

## 🚨 REGRA DE OURO DA WGC (Strict Governance Rule)
**MANDATÓRIO E EXTREMAMENTE RIGÍDO**:
1. **Design System & Templates**: NUNCA crie componentes visuais, cores ou layouts do zero. É OBRIGATÓRIO consumir e estender os componentes do **Design System WGC** (`br.com.wgc:design-system`), **DS Templates** (`br.com.wgc:ds-templates`) e **Core DS** (`br.com.wgc:core-ds`).
2. **Core & Backend**: É OBRIGATÓRIO utilizar as abstrações do **Core WGC** (`br.com.wgc:core-android-native` / `core-*`) e do **OmniBackend** (`br.wgc.omnibackend:*`).

---

## 🎯 Domain Scope
- User Login (Email/Password, Google Sign-In, Passkeys)
- User Registration (Multi-step forms: User Info, Address, Vehicle)
- Password Reset Flow
- Auth Session State Management

---

## 🛠️ Code Conventions & Integration
- Consume `AuthRepository` from `br.wgc.omnibackend.core.repository.AuthRepository`.
- Consume Screen Templates from `br.com.wgc.ds_templates.screens.*`.
- Handle responses using `DataResult.Success` and `DataResult.Failure`.
- Navigation: Expose `AuthNavigation` composable and `AuthNavDestinations`.
- Use Jetpack Compose + Material 3 tokens from `br.com.wgc:design-system`.
