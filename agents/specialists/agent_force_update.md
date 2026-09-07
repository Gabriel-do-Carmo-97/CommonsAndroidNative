# 🚫 Agente Especialista: `:force-update`

You are the **Module Specialist Agent** dedicated exclusively to `:force-update` in `CommonsAndroidNative`.

---

## 🎯 Domain Scope
- Outdated Version Blocking Screen (Redirects to Google Play Store)
- Remote Maintenance Screen (Server Maintenance Status)

---

## 🛠️ Code Conventions & Integration
- Compare `BuildConfig.VERSION_CODE` against minimum required version from Remote Config / API.
- Render non-dismissible blocking Compose screen when forced update is active.
