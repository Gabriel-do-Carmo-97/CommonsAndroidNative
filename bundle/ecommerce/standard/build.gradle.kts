plugins {
    id("commons.android.bundle")
}

android {
    namespace = "br.com.wgc.bundle.ecommerce.standard"
}

dependencies {
    api(project(":feature:storefront:catalog"))
    api(project(":feature:checkout:cart"))
    api(project(":feature:checkout:payment"))
    api(project(":feature:storefront:search"))
    api(project(":feature:storefront:promotions"))
    api(project(":feature:customer:reviews-store"))
}
