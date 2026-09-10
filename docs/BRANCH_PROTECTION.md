# 🛡️ Guia de Proteção de Branch (Branch Protection Rules)

Para garantir que nenhum código quebre o monorepo **CommonsAndroidNative** ou viole nossos padrões de engenharia e governança de agentes, configure as seguintes **Branch Protection Rules** na branch `master` no GitHub:

---

## ⚙️ Configurações Obrigatórias no GitHub

1. Acesse: **Settings** $\rightarrow$ **Branches** $\rightarrow$ **Branch protection rules** $\rightarrow$ **Add rule**.
2. **Branch name pattern**: `master`
3. Marque as opções recomendadas:
   - [x] **Require a pull request before merging**
     - [x] Require approvals: **1 approval**
     - [x] Dismiss stale pull request approvals when new commits are pushed
   - [x] **Require status checks to pass before merging**
     - [x] Require branches to be up to date before merging
      - **Status checks recomendados para merge**:
        - `PR: Validate Conventional Title`
        - `feature: auth`
        - `feature: account`
        - `feature: system`
        - `feature: platform`
        - `feature: storefront`
        - `feature: checkout`
        - `feature: customer`
        - `feature: delivery`
        - `feature: communication`
        - `feature: services`
        - `app: sample-app`
    - [x] **Require conversation resolution before merging**
    - [x] **Do not allow bypassing the above settings**
