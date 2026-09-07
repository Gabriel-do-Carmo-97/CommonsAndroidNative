# Módulo `:cart`

Módulo reutilizável para **Carrinho de Compras, Cálculo de Frete/Taxa e Validações de Pedido**.

---

## 🎯 Objetivo e Funcionalidades

- **Carrinho Persistente**: Armazenamento local seguro do carrinho caso o app feche.
- **Cálculo de Totais**: Subtotal, taxa de entrega por bairro/distrito ou KM e troco.
- **Regras do Estabelecimento**: Validação de valor de pedido mínimo e verificação de horário de funcionamento (Aberto/Fechado).

---

## 📦 Como Usar

```kotlin
dependencies {
    implementation(project(":cart"))
}
```
