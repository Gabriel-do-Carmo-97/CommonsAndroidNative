# Módulo `:order-tracking`

Módulo reutilizável para **Acompanhamento de Pedido em Tempo Real e Status de Entrega**.

---

## 🎯 Objetivo e Funcionalidades

- **Stepper Visual de Status**: *Pedido Recebido ➔ Em Preparo ➔ Saiu para Entrega ➔ Entregue*.
- **Estimativa Reativa**: Cronômetro regressivo com estimativa de tempo de entrega.
- **Contato Rápido**: Chamada direta ou mensagem via WhatsApp para o entregador ou loja.

---

## 📦 Como Usar

```kotlin
dependencies {
    implementation(project(":order-tracking"))
}
```
