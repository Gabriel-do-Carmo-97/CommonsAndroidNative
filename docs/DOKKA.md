# 📚 Documentação de API com Dokka

O repositório **CommonsAndroidNative** está configurado com o plugin **Dokka** (V2) para geração automática de documentação HTML e Markdown a partir das anotações e KDocs do código-fonte.

---

## 🚀 Como Gerar Localmente

### Gerar documentação de todos os submódulos:
```bash
./gradlew dokkaHtmlMultiModule
```

### Gerar documentação de um módulo específico:
```bash
./gradlew :bundles:ecommerce:basic:dokkaHtml
# ou
./gradlew :feature:storefront:catalog:dokkaHtml
```

Os arquivos HTML gerados estarão disponíveis em:
- Visão agregada: `build/dokka/htmlMultiModule/`
- Por módulo: `<modulo>/build/dokka/html/`

---

## 🌐 Publicação Contínua no GitHub Pages
O pipeline do GitHub Actions (`.github/workflows/android.yaml`) executa automaticamente a tarefa de compilação do Dokka em cada push para `master` e publica a documentação diretamente no **GitHub Pages** da organização.
