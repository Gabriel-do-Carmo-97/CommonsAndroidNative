plugins {
    id("factory.android.bundle")
}

android {
    namespace = "br.com.wgc.factory.bundles.ecommerce.pro"
}

dependencies {
    api(project(":feature:storefront:catalog"))
    api(project(":feature:checkout:cart"))
    api(project(":feature:checkout:payment"))
    api(project(":feature:storefront:search"))
    api(project(":feature:storefront:promotions"))
    api(project(":feature:customer:reviews-store"))
    api(project(":feature:customer:loyalty"))
    api(project(":feature:storefront:stores"))
    api(project(":feature:platform:ai-assistant"))
}

