plugins {
    id("com.android.library")
    id("maven-publish")
}

val adapterVersion = "1.0.0"

android {
    namespace = "com.adsyield.mediation.max"
    compileSdk = 35

    defaultConfig {
        minSdk = 23
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField("String", "VERSION_NAME", "\"$adapterVersion\"")
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
        }
    }
}

dependencies {
    // AppLovin MAX SDK provides the MediationAdapterBase and MAX adapter contracts.
    // compileOnly: the publisher's app already pulls this in at runtime.
    compileOnly("com.applovin:applovin-sdk:13.3.1")

    // Google Mobile Ads SDK loads the actual ad from AdsYield's GAM/MCM ad unit.
    api("com.google.android.gms:play-services-ads:24.7.0")
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                groupId = "com.adsyield"
                artifactId = "ADSmaxadapter"
                version = adapterVersion

                pom {
                    name.set("AdsYield MAX Mediation Adapter")
                    description.set("AdsYield MAX custom network adapter that loads ads from AdsYield's GAM/MCM inventory through the Google Mobile Ads SDK.")
                    url.set("https://github.com/bugranalci/AdsyieldMaxAdapter-Android")

                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                }
            }
        }
    }
}
