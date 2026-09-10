# Politica de Seguranca â€” CommonsAndroidNative ðŸ”’

O **CommonsAndroidNative** e uma suite corporativa de bibliotecas Android que gerencia autenticacao, biometria, pagamentos, rastreamento de delivery e dados criticos de usuarios. A seguranca, integridade e conformidade com a LGPD/GDPR sao prioridades absolutas.

---

## ðŸ›¡ï¸ Versoes Suportadas

Apenas as versoes mais recentes na branch ativa `master` recebem patches de seguranca regulares:

| Versao | Suporte a Patches de Seguranca |
|:---|:---|
| `v0.0.x` (Atual) | Sim (Versao ativa em desenvolvimento) |
| `< v0.0.1` | Nao (Atualizacao recomendada) |

---

## ðŸš¨ Como Reportar uma Vulnerabilidade

**Por favor, NAO abra Issues publicas no GitHub para reportar vulnerabilidades de seguranca.**

Se voce identificou uma falha de seguranca, vulnerabilidade no fluxo de autenticacao/biometria, vazamento de PII ou brecha em permissoes:

1. Envie um e-mail confidencial detalhado para:
   ðŸ“§ **`gabriel.desenvolvedor.97@gmail.com`**
2. No assunto do e-mail, utilize:
   `[SECURITY VULNERABILITY] CommonsAndroidNative - <Resumo Breve>`
3. No corpo da mensagem, inclua:
   - **Descricao detalhada:** Explicacao do vetor de ataque ou comportamento anomalo.
   - **Modulos afetados:** (ex: `feature:auth:authentication`, `feature:auth:biometric`, `feature:checkout:payment`).
   - **Passos para reproducao:** Codigo de exemplo ou PoC (Proof of Concept).
   - **Impacto potencial:** Avaliacao de severidade (ex: vazamento de tokens, bypass biometrico, crash de aplicacao).

---

## â±ï¸ Compromisso e SLA de Resposta

* **Confirmacao inicial de recebimento:** Em ate **24 a 48 horas**.
* **Avaliacao de severidade e triagem:** Em ate **72 horas**.
* **Lancamento de patch corretivo:** Prioridade maxima com publicacao de hotfix no GitHub Packages.