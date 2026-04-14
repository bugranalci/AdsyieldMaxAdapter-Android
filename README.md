# AdsYield MAX Mediation Adapter for Android

AdsYield MAX Mediation Adapter, AppLovin MAX mediation kullanan yayıncıların AdsYield demand'ini GAM/MCM altyapısı üzerinden sunmasını sağlar.

AdsYield MAX Mediation Adapter enables publishers using AppLovin MAX mediation to serve AdsYield demand through the underlying GAM/MCM inventory.

---

## 🇹🇷 Kurulum

### JitPack ile

`settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

App seviyesi `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.bugranalci:AdsyieldMaxAdapter-Android:1.0.1")
}
```

### Desteklenen Formatlar

- Banner (320x50) + Adaptive Banner (anchored & inline) + Leaderboard (728x90)
- MREC (300x250)
- Interstitial
- Rewarded

### MAX Dashboard Ayarı

| Alan | Değer |
|---|---|
| **Class Name (Android)** | `com.adsyield.mediation.max.ADSmaxCN` |
| **Placement ID** | AdsYield'ın verdiği GAM ad unit ID (örn: `ca-app-pub-XXXXX/YYYYYY`) |

Detaylı rehber: [docs/ENTEGRASYON_REHBERI.md](docs/ENTEGRASYON_REHBERI.md)

---

## 🇬🇧 Installation

### Via JitPack

`settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

App-level `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.github.bugranalci:AdsyieldMaxAdapter-Android:1.0.1")
}
```

### Supported Formats

- Banner (320x50) + Adaptive Banner (anchored & inline) + Leaderboard (728x90)
- MREC (300x250)
- Interstitial
- Rewarded

### MAX Dashboard Setup

| Field | Value |
|---|---|
| **Class Name (Android)** | `com.adsyield.mediation.max.ADSmaxCN` |
| **Placement ID** | GAM ad unit ID provided by AdsYield (e.g., `ca-app-pub-XXXXX/YYYYYY`) |

Full guide: [docs/INTEGRATION_GUIDE.md](docs/INTEGRATION_GUIDE.md)

---

## Requirements

- Android API 23+
- AppLovin MAX SDK 13.0.0+
- Google Mobile Ads SDK 24.7.0+ (transitively provided)

## License

Apache License 2.0
