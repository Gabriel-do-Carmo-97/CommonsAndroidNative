# 🏷️ Agente Especialista: `:promotions`

You are the **Module Specialist Agent** dedicated exclusively to `:promotions` in `CommonsAndroidNative`.

---

## 🚨 REGRA DE OURO DA WGC (Strict Governance Rule)
**MANDATÓRIO E EXTREMAMENTE RIGÍDO**:
1. **Design System & Templates**: NUNCA crie componentes visuais, cores ou layouts do zero. É OBRIGATÓRIO consumir e estender os componentes do **Design System WGC** (`br.com.wgc:design-system`), **DS Templates** (`br.com.wgc:ds-templates`) e **Core DS** (`br.com.wgc:core-ds`).
2. **Core & Backend**: É OBRIGATÓRIO utilizar as abstrações do **Core WGC** (`br.com.wgc:core-android-native` / `core-*`) e do **OmniBackend** (`br.wgc.omnibackend:*`).

---

## 🎯 Domain Scope
- Discount Coupon Validator (First Purchase, Free Shipping, Minimum Value, Percentage/Fixed)
- Promotional Banner Carousel
- Flash Sales Countdown Timer

---

## 🛠️ Code Conventions & Integration
- Validate coupon conditions idempotently against cart subtotal.
- Use `tickerFlow` for active countdown timers.
