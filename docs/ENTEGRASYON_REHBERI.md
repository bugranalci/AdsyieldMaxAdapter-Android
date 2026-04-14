# AdsYield MAX Android Adapter — Entegrasyon Rehberi

Bu rehber, AppLovin MAX mediation kullanan Android uygulamalarına AdsYield'in entegre edilmesini anlatır.

## 1. Gereksinimler

| Gereksinim | Minimum |
|---|---|
| Android API | 23+ |
| AppLovin MAX SDK | 13.0.0+ |
| Google Mobile Ads SDK | 24.7.0+ |

AppLovin MAX SDK uygulamanızda zaten kurulu olmalıdır. Değilse [AppLovin MAX entegrasyon rehberi](https://support.axon.ai/en/max/android) ile kurun.

## 2. Gradle Kurulumu

### 2.1 Repository Ekleme

Proje seviyesi `settings.gradle.kts`:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

### 2.2 Dependency Ekleme

App seviyesi `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.applovin:applovin-sdk:13.3.1")
    implementation("com.github.bugranalci:AdsyieldMaxAdapter-Android:1.0.1")
}
```

Google Mobile Ads SDK otomatik transitive olarak gelir.

## 3. AndroidManifest.xml

Google Mobile Ads için Application ID tanımlayın (AdsYield tarafından sağlanacak):

```xml
<meta-data
    android:name="com.google.android.gms.ads.APPLICATION_ID"
    android:value="ca-app-pub-XXXXX~YYYYY" />
```

## 4. MAX Dashboard — Custom Network Oluşturma

### Adım 1: Network Ekle

1. MAX Dashboard → **Mediation > Networks**
2. Sayfanın en altında **"Click here to add a Custom Network"**
3. Değerleri girin:

| Alan | Değer |
|---|---|
| **Network Type** | SDK |
| **Name** | AdsYield |
| **iOS Adapter Class Name** | `ADSmaxCN` |
| **Android Adapter Class Name** | `com.adsyield.mediation.max.ADSmaxCN` |

4. Kaydedin.

### Adım 2: Ad Unit'e Ekle

1. **MAX > Ad Units** → ilgili ad unit → **Edit Waterfall**
2. **AdsYield** custom network'ünü aç (Android)
3. Alanları doldurun:

| Alan | Değer |
|---|---|
| **App ID** | Boş bırakılabilir |
| **Placement ID** | AdsYield'ın verdiği GAM ad unit ID (örn: `ca-app-pub-XXXXX/YYYYYY`) |
| **CPM** | AdsYield'ın tavsiye ettiği eCPM |

> **Placement ID** alanı adapter'a iletilir ve Google Mobile Ads SDK'ya doğrudan ad unit ID olarak geçirilir.

## 5. Desteklenen Formatlar

| Format | Açıklama |
|---|---|
| Banner | 320x50 |
| Adaptive Banner | Anchored veya Inline (MAX'ten otomatik gelir) |
| Leaderboard | 728x90 |
| MREC | 300x250 |
| Interstitial | Tam ekran geçiş reklamı |
| Rewarded | Ödüllü video reklam |

Uygulama kodunuzda değişiklik yapmanıza gerek yoktur — standart MAX reklam yükleme kodunuz aynen çalışır.

## 6. Test Etme

### Mediation Debugger

```kotlin
AppLovinSdk.getInstance(context).showMediationDebugger()
```

Adapter'ın tanındığını şu şekilde doğrulayın:
- **Network SDK:** ADSmaxCN sekmesinde `Status: Initialized` görünmelidir
- **Ad Units** sekmesinde AdsYield custom network'ü görünmelidir

### Logcat

`ADSmaxCN` tag'ini filtreleyin:

```
ADSmaxCN: Initializing AdsYield MAX adapter (ADSmaxCN)...
ADSmaxCN: Loading interstitial ad: ca-app-pub-XXX/YYY...
ADSmaxCN: Interstitial ad loaded: ca-app-pub-XXX/YYY
ADSmaxCN: Interstitial ad shown: ...
```

## 7. Sorun Giderme

| Sorun | Çözüm |
|---|---|
| "Adapter not found" | Class name'i kontrol edin: `com.adsyield.mediation.max.ADSmaxCN` (büyük/küçük harf) |
| "No fill" | AdsYield MCM ad unit'inin aktif olduğundan ve demand bulunduğundan emin olun |
| "Missing Placement ID" | MAX dashboard'da ad unit ayarında Placement ID alanını doldurun |
| Banner görünmüyor | `AppLovinMaxConfiguration`'da view'ın parent'a eklendiğinden emin olun |
| Waterfall'da AdsYield görünmüyor | Custom Network aktif mi, CPM set edildi mi, ülke seçimi doğru mu kontrol edin |

## 8. Versiyon Uyumluluğu

| Adapter | AppLovin SDK | Google Mobile Ads SDK |
|---|---|---|
| 1.0.1 | 13.0.0+ | 24.7.0+ |

## 9. Destek

- GitHub Issues: https://github.com/bugranalci/AdsyieldMaxAdapter-Android/issues
- E-posta: info@adsyield.com
