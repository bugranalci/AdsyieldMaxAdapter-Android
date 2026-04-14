# AdsYield MAX Android Adapter — Integration Guide

This guide walks you through integrating AdsYield into an Android app that already uses AppLovin MAX mediation.

## 1. Requirements

| Requirement | Minimum |
|---|---|
| Android API | 23+ |
| AppLovin MAX SDK | 13.0.0+ |
| Google Mobile Ads SDK | 24.7.0+ |

The AppLovin MAX SDK must already be installed in your app. If not, follow the [AppLovin MAX integration guide](https://support.axon.ai/en/max/android).

## 2. Gradle Setup

### 2.1 Add Repository

Project-level `settings.gradle.kts`:

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

### 2.2 Add Dependency

App-level `build.gradle.kts`:

```kotlin
dependencies {
    implementation("com.applovin:applovin-sdk:13.3.1")
    implementation("com.github.bugranalci:AdsyieldMaxAdapter-Android:1.0.0")
}
```

Google Mobile Ads SDK is brought in transitively.

## 3. AndroidManifest.xml

Declare the Google Mobile Ads Application ID (provided by AdsYield):

```xml
<meta-data
    android:name="com.google.android.gms.ads.APPLICATION_ID"
    android:value="ca-app-pub-XXXXX~YYYYY" />
```

## 4. MAX Dashboard — Create a Custom Network

### Step 1: Add a Network

1. MAX Dashboard → **Mediation > Networks**
2. Scroll to the bottom → **"Click here to add a Custom Network"**
3. Enter:

| Field | Value |
|---|---|
| **Network Type** | SDK |
| **Name** | AdsYield |
| **iOS Adapter Class Name** | `ADSmaxCN` |
| **Android Adapter Class Name** | `com.adsyield.mediation.max.ADSmaxCN` |

4. Save.

### Step 2: Enable on Ad Unit

1. **MAX > Ad Units** → pick the ad unit → **Edit Waterfall**
2. Toggle on the **AdsYield** custom network for Android
3. Configure:

| Field | Value |
|---|---|
| **App ID** | Leave empty |
| **Placement ID** | GAM ad unit ID provided by AdsYield (e.g., `ca-app-pub-XXXXX/YYYYYY`) |
| **CPM** | eCPM recommended by AdsYield |

> The **Placement ID** field is passed through to the adapter and used as the Google Mobile Ads SDK ad unit ID.

## 5. Supported Formats

| Format | Description |
|---|---|
| Banner | 320x50 |
| Adaptive Banner | Anchored or Inline (auto-forwarded from MAX) |
| Leaderboard | 728x90 |
| MREC | 300x250 |
| Interstitial | Full-screen interstitial |
| Rewarded | Rewarded video ad |

No changes are needed in your app code — the standard MAX ad loading APIs continue to work.

## 6. Testing

### Mediation Debugger

```kotlin
AppLovinSdk.getInstance(context).showMediationDebugger()
```

Verify adapter recognition:
- **Network SDK:** ADSmaxCN shows `Status: Initialized`
- **Ad Units** tab shows AdsYield as an active custom network

### Logcat

Filter by tag `ADSmaxCN`:

```
ADSmaxCN: Initializing AdsYield MAX adapter (ADSmaxCN)...
ADSmaxCN: Loading interstitial ad: ca-app-pub-XXX/YYY...
ADSmaxCN: Interstitial ad loaded: ca-app-pub-XXX/YYY
ADSmaxCN: Interstitial ad shown: ...
```

## 7. Troubleshooting

| Issue | Fix |
|---|---|
| "Adapter not found" | Check class name: `com.adsyield.mediation.max.ADSmaxCN` (case-sensitive) |
| "No fill" | Ensure AdsYield MCM ad unit is active and has demand |
| "Missing Placement ID" | Fill the Placement ID field in MAX dashboard for that ad unit |
| Banner invisible | Confirm the view is attached to its parent (MAX MaxAdView) |
| AdsYield absent from waterfall | Check Custom Network is enabled, CPM is set, country targeting matches |

## 8. Version Compatibility

| Adapter | AppLovin SDK | Google Mobile Ads SDK |
|---|---|---|
| 1.0.0 | 13.0.0+ | 24.7.0+ |

## 9. Support

- GitHub Issues: https://github.com/bugranalci/AdsyieldMaxAdapter-Android/issues
- Email: info@adsyield.com
