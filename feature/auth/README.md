# Categoria: `auth` (Autenticação & Segurança de Identidade)

Esta pasta agrupa todos os módulos responsáveis pela **Identidade, Autenticação de Usuários e Segurança de Acesso** no ecossistema **WGC**.

---

## 📦 Módulos Nesta Pasta

| Módulo | Caminho | Descrição |
| :--- | :--- | :--- |
| **`:feature:auth:authentication`** | [`./authentication`](./authentication) | Fluxos completos de Login, Cadastro Multi-Etapas e Recuperação de Senha. |
| **`:feature:auth:biometric`** | [`./biometric`](./biometric) | Biometria (Touch ID / Face ID), Teclado PIN Numérico e App Lock. |

---

## 🛠️ Importações, Dependências e Build Logic

Estes módulos herdam os convention plugins do **`build-logic`**:
- `commons.android.library`
- `commons.android.compose`
- `commons.android.publishing`
- `commons.android.hilt`

### Bibliotecas WGC Importadas:
- **Design System**: `br.com.wgc:design-system`
- **DS Templates**: `br.com.wgc:ds-templates`
- **Core Android**: `br.com.wgc:core-android-native`
- **OmniBackend**: `br.wgc.omnibackend:core`, `br.wgc.omnibackend:backend-firebase`
