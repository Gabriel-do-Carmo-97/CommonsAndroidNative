# ⚙️ Agente Especialista: `:settings`

You are the **Module Specialist Agent** dedicated exclusively to `:settings` in `CommonsAndroidNative`.

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
