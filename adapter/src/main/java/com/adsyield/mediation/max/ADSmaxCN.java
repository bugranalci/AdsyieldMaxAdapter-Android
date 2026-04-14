package com.adsyield.mediation.max;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.MaxReward;
import com.applovin.mediation.adapter.MaxAdViewAdapter;
import com.applovin.mediation.adapter.MaxAdapter;
import com.applovin.mediation.adapter.MaxAdapterError;
import com.applovin.mediation.adapter.MaxInterstitialAdapter;
import com.applovin.mediation.adapter.MaxRewardedAdapter;
import com.applovin.mediation.adapter.listeners.MaxAdViewAdapterListener;
import com.applovin.mediation.adapter.listeners.MaxInterstitialAdapterListener;
import com.applovin.mediation.adapter.listeners.MaxRewardedAdapterListener;
import com.applovin.mediation.adapter.parameters.MaxAdapterInitializationParameters;
import com.applovin.mediation.adapter.parameters.MaxAdapterParameters;
import com.applovin.mediation.adapter.parameters.MaxAdapterResponseParameters;
import com.applovin.mediation.adapters.MediationAdapterBase;
import com.applovin.sdk.AppLovinSdk;
import com.applovin.sdk.AppLovinSdkUtils;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * AdsYield MAX Custom Network adapter.
 *
 * Registered in the AppLovin MAX dashboard as a Custom Network with this
 * fully-qualified class name: {@code com.adsyield.mediation.max.ADSmaxCN}.
 *
 * The MAX dashboard "Placement ID" field receives the GAM/MCM ad unit ID that
 * AdsYield provides (e.g. {@code ca-app-pub-XXXXX/YYYYYY}). This adapter then
 * loads that ad unit through the Google Mobile Ads SDK and forwards the
 * lifecycle events back to MAX.
 */
@Keep
public class ADSmaxCN extends MediationAdapterBase
        implements MaxInterstitialAdapter, MaxRewardedAdapter, MaxAdViewAdapter {

    private static final String ADAPTIVE_BANNER_TYPE_INLINE = "inline";

    private static final AtomicBoolean initialized = new AtomicBoolean();

    private InterstitialAd interstitialAd;
    private RewardedAd     rewardedAd;
    private AdView         adView;

    public ADSmaxCN(final AppLovinSdk sdk) {
        super(sdk);
    }

    // region MaxAdapter lifecycle

    @Override
    public void initialize(final MaxAdapterInitializationParameters parameters,
                           @Nullable final Activity activity,
                           final MaxAdapter.OnCompletionListener onCompletionListener) {
        log("Initializing AdsYield MAX adapter (ADSmaxCN)...");

        if (initialized.compareAndSet(false, true)) {
            MobileAds.initialize(getContext(activity));
        }

        onCompletionListener.onCompletion(MaxAdapter.InitializationStatus.DOES_NOT_APPLY, null);
    }

    @Override
    public String getSdkVersion() {
        return String.valueOf(MobileAds.getVersion());
    }

    @Override
    public String getAdapterVersion() {
        return BuildConfig.VERSION_NAME;
    }

    @Override
    public void onDestroy() {
        log("Destroy called for adapter " + this);

        if (interstitialAd != null) {
            interstitialAd.setFullScreenContentCallback(null);
            interstitialAd = null;
        }
        if (rewardedAd != null) {
            rewardedAd.setFullScreenContentCallback(null);
            rewardedAd = null;
        }
        if (adView != null) {
            adView.destroy();
            adView = null;
        }
    }

    // endregion

    // region MaxInterstitialAdapter

    @Override
    public void loadInterstitialAd(final MaxAdapterResponseParameters parameters,
                                   @Nullable final Activity activity,
                                   final MaxInterstitialAdapterListener listener) {
        final String placementId = parameters.getThirdPartyAdPlacementId();
        log("Loading interstitial ad: " + placementId + "...");

        if (TextUtils.isEmpty(placementId)) {
            listener.onInterstitialAdLoadFailed(ADSmaxErrorMapper.missingAdUnitId());
            return;
        }

        final Context context = getContext(activity);
        InterstitialAd.load(context, placementId, new AdRequest.Builder().build(),
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull final InterstitialAd ad) {
                        log("Interstitial ad loaded: " + placementId);
                        interstitialAd = ad;
                        interstitialAd.setFullScreenContentCallback(
                                new InterstitialAdListener(placementId, listener));
                        listener.onInterstitialAdLoaded();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull final LoadAdError error) {
                        final MaxAdapterError adapterError = ADSmaxErrorMapper.toMaxError(error);
                        log("Interstitial (" + placementId + ") failed to load: " + adapterError);
                        listener.onInterstitialAdLoadFailed(adapterError);
                    }
                });
    }

    @Override
    public void showInterstitialAd(final MaxAdapterResponseParameters parameters,
                                   @Nullable final Activity activity,
                                   final MaxInterstitialAdapterListener listener) {
        final String placementId = parameters.getThirdPartyAdPlacementId();
        log("Showing interstitial ad: " + placementId + "...");

        if (interstitialAd != null) {
            interstitialAd.show(activity);
        } else {
            log("Interstitial ad not ready: " + placementId);
            listener.onInterstitialAdDisplayFailed(ADSmaxErrorMapper.adNotReady());
        }
    }

    private class InterstitialAdListener extends FullScreenContentCallback {
        private final String placementId;
        private final MaxInterstitialAdapterListener listener;

        InterstitialAdListener(final String placementId, final MaxInterstitialAdapterListener listener) {
            this.placementId = placementId;
            this.listener = listener;
        }

        @Override
        public void onAdShowedFullScreenContent() {
            log("Interstitial ad shown: " + placementId);
        }

        @Override
        public void onAdImpression() {
            log("Interstitial ad impression: " + placementId);
            listener.onInterstitialAdDisplayed();
        }

        @Override
        public void onAdClicked() {
            log("Interstitial ad clicked: " + placementId);
            listener.onInterstitialAdClicked();
        }

        @Override
        public void onAdDismissedFullScreenContent() {
            log("Interstitial ad hidden: " + placementId);
            listener.onInterstitialAdHidden();
        }

        @Override
        public void onAdFailedToShowFullScreenContent(@NonNull final AdError adError) {
            final MaxAdapterError adapterError = new MaxAdapterError(
                    MaxAdapterError.AD_DISPLAY_FAILED,
                    adError.getCode(),
                    adError.getMessage() != null ? adError.getMessage() : "");
            log("Interstitial (" + placementId + ") failed to show: " + adapterError);
            listener.onInterstitialAdDisplayFailed(adapterError);
        }
    }

    // endregion

    // region MaxRewardedAdapter

    @Override
    public void loadRewardedAd(final MaxAdapterResponseParameters parameters,
                               @Nullable final Activity activity,
                               final MaxRewardedAdapterListener listener) {
        final String placementId = parameters.getThirdPartyAdPlacementId();
        log("Loading rewarded ad: " + placementId + "...");

        if (TextUtils.isEmpty(placementId)) {
            listener.onRewardedAdLoadFailed(ADSmaxErrorMapper.missingAdUnitId());
            return;
        }

        final Context context = getContext(activity);
        RewardedAd.load(context, placementId, new AdRequest.Builder().build(),
                new RewardedAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull final RewardedAd ad) {
                        log("Rewarded ad loaded: " + placementId);
                        rewardedAd = ad;
                        rewardedAd.setFullScreenContentCallback(
                                new RewardedAdListener(placementId, listener));
                        listener.onRewardedAdLoaded();
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull final LoadAdError error) {
                        final MaxAdapterError adapterError = ADSmaxErrorMapper.toMaxError(error);
                        log("Rewarded (" + placementId + ") failed to load: " + adapterError);
                        listener.onRewardedAdLoadFailed(adapterError);
                    }
                });
    }

    @Override
    public void showRewardedAd(final MaxAdapterResponseParameters parameters,
                               @Nullable final Activity activity,
                               final MaxRewardedAdapterListener listener) {
        final String placementId = parameters.getThirdPartyAdPlacementId();
        log("Showing rewarded ad: " + placementId + "...");

        if (rewardedAd != null) {
            configureReward(parameters);
            rewardedAd.show(activity, rewardItem -> {
                log("User earned reward: " + placementId);
                final MaxReward reward = getReward();
                listener.onUserRewarded(reward);
            });
        } else {
            log("Rewarded ad not ready: " + placementId);
            listener.onRewardedAdDisplayFailed(ADSmaxErrorMapper.adNotReady());
        }
    }

    private class RewardedAdListener extends FullScreenContentCallback {
        private final String placementId;
        private final MaxRewardedAdapterListener listener;

        RewardedAdListener(final String placementId, final MaxRewardedAdapterListener listener) {
            this.placementId = placementId;
            this.listener = listener;
        }

        @Override
        public void onAdShowedFullScreenContent() {
            log("Rewarded ad shown: " + placementId);
        }

        @Override
        public void onAdImpression() {
            log("Rewarded ad impression: " + placementId);
            listener.onRewardedAdDisplayed();
        }

        @Override
        public void onAdClicked() {
            log("Rewarded ad clicked: " + placementId);
            listener.onRewardedAdClicked();
        }

        @Override
        public void onAdDismissedFullScreenContent() {
            log("Rewarded ad hidden: " + placementId);
            listener.onRewardedAdHidden();
        }

        @Override
        public void onAdFailedToShowFullScreenContent(@NonNull final AdError adError) {
            final MaxAdapterError adapterError = new MaxAdapterError(
                    MaxAdapterError.AD_DISPLAY_FAILED,
                    adError.getCode(),
                    adError.getMessage() != null ? adError.getMessage() : "");
            log("Rewarded (" + placementId + ") failed to show: " + adapterError);
            listener.onRewardedAdDisplayFailed(adapterError);
        }
    }

    // endregion

    // region MaxAdViewAdapter (Banner / MREC / Leader)

    @Override
    public void loadAdViewAd(final MaxAdapterResponseParameters parameters,
                             final MaxAdFormat adFormat,
                             @Nullable final Activity activity,
                             final MaxAdViewAdapterListener listener) {
        final String placementId = parameters.getThirdPartyAdPlacementId();
        log("Loading " + adFormat.getLabel() + " ad: " + placementId + "...");

        if (TextUtils.isEmpty(placementId)) {
            listener.onAdViewAdLoadFailed(ADSmaxErrorMapper.missingAdUnitId());
            return;
        }

        final Context context = getContext(activity);
        final boolean isAdaptiveBanner =
                parameters.getServerParameters().getBoolean("adaptive_banner", false);

        adView = new AdView(context);
        adView.setAdUnitId(placementId);
        adView.setAdSize(toAdSize(adFormat, isAdaptiveBanner, parameters, context));
        adView.setAdListener(new AdViewListener(placementId, adFormat, listener));
        adView.loadAd(new AdRequest.Builder().build());
    }

    private class AdViewListener extends AdListener {
        private final String placementId;
        private final MaxAdFormat adFormat;
        private final MaxAdViewAdapterListener listener;

        AdViewListener(final String placementId,
                       final MaxAdFormat adFormat,
                       final MaxAdViewAdapterListener listener) {
            this.placementId = placementId;
            this.adFormat = adFormat;
            this.listener = listener;
        }

        @Override
        public void onAdLoaded() {
            log(adFormat.getLabel() + " ad loaded: " + placementId);
            listener.onAdViewAdLoaded(adView);
        }

        @Override
        public void onAdFailedToLoad(@NonNull final LoadAdError error) {
            final MaxAdapterError adapterError = ADSmaxErrorMapper.toMaxError(error);
            log(adFormat.getLabel() + " (" + placementId + ") failed to load: " + adapterError);
            listener.onAdViewAdLoadFailed(adapterError);
        }

        @Override
        public void onAdImpression() {
            log(adFormat.getLabel() + " ad impression: " + placementId);
            listener.onAdViewAdDisplayed();
        }

        @Override
        public void onAdClicked() {
            log(adFormat.getLabel() + " ad clicked: " + placementId);
            listener.onAdViewAdClicked();
        }

        @Override
        public void onAdOpened() {
            log(adFormat.getLabel() + " ad expanded: " + placementId);
            listener.onAdViewAdExpanded();
        }

        @Override
        public void onAdClosed() {
            log(adFormat.getLabel() + " ad collapsed: " + placementId);
            listener.onAdViewAdCollapsed();
        }
    }

    // endregion

    // region Helpers

    private Context getContext(@Nullable final Activity activity) {
        return (activity != null) ? activity.getApplicationContext() : getApplicationContext();
    }

    private AdSize toAdSize(final MaxAdFormat adFormat,
                            final boolean isAdaptiveBanner,
                            final MaxAdapterParameters parameters,
                            final Context context) {
        if (isAdaptiveBanner && isAdaptiveAdFormat(adFormat, parameters)) {
            return getAdaptiveAdSize(parameters, context);
        }
        if (adFormat == MaxAdFormat.BANNER) return AdSize.BANNER;
        if (adFormat == MaxAdFormat.LEADER) return AdSize.LEADERBOARD;
        if (adFormat == MaxAdFormat.MREC)   return AdSize.MEDIUM_RECTANGLE;
        throw new IllegalArgumentException("Unsupported ad format: " + adFormat);
    }

    private boolean isAdaptiveAdFormat(final MaxAdFormat adFormat,
                                       final MaxAdapterParameters parameters) {
        final boolean inlineAdaptiveMrec =
                adFormat == MaxAdFormat.MREC && isInlineAdaptiveBanner(parameters);
        return inlineAdaptiveMrec
                || adFormat == MaxAdFormat.BANNER
                || adFormat == MaxAdFormat.LEADER;
    }

    private AdSize getAdaptiveAdSize(final MaxAdapterParameters parameters, final Context context) {
        final int bannerWidth = getAdaptiveBannerWidth(parameters, context);

        if (isInlineAdaptiveBanner(parameters)) {
            final int inlineMaxHeight = getInlineAdaptiveBannerMaxHeight(parameters);
            if (inlineMaxHeight > 0) {
                return AdSize.getInlineAdaptiveBannerAdSize(bannerWidth, inlineMaxHeight);
            }
            return AdSize.getCurrentOrientationInlineAdaptiveBannerAdSize(context, bannerWidth);
        }
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(context, bannerWidth);
    }

    private boolean isInlineAdaptiveBanner(final MaxAdapterParameters parameters) {
        final Map<String, Object> extras = parameters.getLocalExtraParameters();
        final Object type = extras != null ? extras.get("adaptive_banner_type") : null;
        return (type instanceof String)
                && ADAPTIVE_BANNER_TYPE_INLINE.equalsIgnoreCase((String) type);
    }

    private int getInlineAdaptiveBannerMaxHeight(final MaxAdapterParameters parameters) {
        final Map<String, Object> extras = parameters.getLocalExtraParameters();
        final Object value = extras != null ? extras.get("inline_adaptive_banner_max_height") : null;
        return (value instanceof Integer) ? (int) value : 0;
    }

    private int getAdaptiveBannerWidth(final MaxAdapterParameters parameters, final Context context) {
        final Map<String, Object> extras = parameters.getLocalExtraParameters();
        final Object width = extras != null ? extras.get("adaptive_banner_width") : null;
        if (width instanceof Integer) {
            return (int) width;
        }
        return AppLovinSdkUtils.pxToDp(context, getApplicationWindowWidth(context));
    }

    private static int getApplicationWindowWidth(final Context context) {
        final WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        final Display display = wm.getDefaultDisplay();
        final DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        return metrics.widthPixels;
    }

    // endregion
}
