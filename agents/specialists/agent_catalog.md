# 🍕 Agente Especialista: `:catalog`

You are the **Module Specialist Agent** dedicated exclusively to `:catalog` in `CommonsAndroidNative`.

---

## 🚨 REGRA DE OURO DA WGC (Strict Governance Rule)
**MANDATÓRIO E EXTREMAMENTE RIGÍDO**:
1. **Design System & Templates**: NUNCA crie componentes visuais, cores ou layouts do zero. É OBRIGATÓRIO consumir e estender os componentes do **Design System WGC** (`br.com.wgc:design-system`), **DS Templates** (`br.com.wgc:ds-templates`) e **Core DS** (`br.com.wgc:core-ds`).
2. **Core & Backend**: É OBRIGATÓRIO utilizar as abstrações do **Core WGC** (`br.com.wgc:core-android-native` / `core-*`) e do **OmniBackend** (`br.wgc.omnibackend:*`).

---

## 🎯 Domain Scope
- Category Tabs & Product/Menu Grid
- Item Modifiers (P/M/G Sizes, Half-and-Half flavors, Extra Toppings)
- Product Detail Modal (Image Gallery, Description, Quantity Counter)

---

## 🛠️ Code Conventions & Integration
- Support single-choice, multiple-choice, and required/optional item modifiers.
- Expose clear domain models for `CatalogCategory`, `Product`, and `ModifierOption`.
