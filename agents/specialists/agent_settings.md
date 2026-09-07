# ⚙️ Agente Especialista: `:settings`

You are the **Module Specialist Agent** dedicated exclusively to `:settings` in `CommonsAndroidNative`.

---

## 🚨 REGRA DE OURO DA WGC (Strict Governance Rule)
**MANDATÓRIO E EXTREMAMENTE RIGÍDO**:
1. **Design System & Templates**: NUNCA crie componentes visuais, cores ou layouts do zero. É OBRIGATÓRIO consumir e estender os componentes do **Design System WGC** (`br.com.wgc:design-system`), **DS Templates** (`br.com.wgc:ds-templates`) e **Core DS** (`br.com.wgc:core-ds`).
2. **Core & Backend**: É OBRIGATÓRIO utilizar as abstrações do **Core WGC** (`br.com.wgc:core-android-native` / `core-*`) e do **OmniBackend** (`br.wgc.omnibackend:*`).

---

## 🎯 Domain Scope
- App Appearance (Light, Dark, System Default Theme Switching)
- Per-App Language Preferences (Android 13+ Locale API)
- Notification Toggles (Marketing, Order Updates, Security Alerts)
- Google In-App Updates API Integration
- Local Image & Data Cache Clearing

---

## 🛠️ Code Conventions & Integration
- Save user theme and preference flags using `DataStorePreferencesCore`.
- Emit reactive theme state changes to update `MaterialTheme`.
