package com.adsyield.mediation.max;

import androidx.annotation.NonNull;

import com.applovin.mediation.adapter.MaxAdapterError;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;

/**
 * Maps Google Mobile Ads SDK errors to AppLovin MAX adapter errors.
 */
final class ADSmaxErrorMapper {

    private ADSmaxErrorMapper() {}

    @NonNull
    static MaxAdapterError toMaxError(@NonNull final AdError gmaError) {
        final int code = gmaError.getCode();
        MaxAdapterError adapterError = MaxAdapterError.UNSPECIFIED;

        switch (code) {
            case AdRequest.ERROR_CODE_NO_FILL:
            case AdRequest.ERROR_CODE_MEDIATION_NO_FILL:
                adapterError = MaxAdapterError.NO_FILL;
                break;
            case AdRequest.ERROR_CODE_NETWORK_ERROR:
                adapterError = MaxAdapterError.NO_CONNECTION;
                break;
            case AdRequest.ERROR_CODE_INTERNAL_ERROR:
                adapterError = MaxAdapterError.INTERNAL_ERROR;
                break;
            case AdRequest.ERROR_CODE_INVALID_REQUEST:
            case AdRequest.ERROR_CODE_REQUEST_ID_MISMATCH:
                adapterError = MaxAdapterError.BAD_REQUEST;
                break;
            case AdRequest.ERROR_CODE_APP_ID_MISSING:
            case AdRequest.ERROR_CODE_INVALID_AD_STRING:
                adapterError = MaxAdapterError.INVALID_CONFIGURATION;
                break;
            default:
                break;
        }

        final String message = gmaError.getMessage() != null ? gmaError.getMessage() : "";
        return new MaxAdapterError(adapterError, code, message);
    }

    @NonNull
    static MaxAdapterError missingAdUnitId() {
        return new MaxAdapterError(
                MaxAdapterError.INVALID_CONFIGURATION,
                0,
                "Missing Placement ID (GAM ad unit) in MAX custom network configuration."
        );
    }

    @NonNull
    static MaxAdapterError adNotReady() {
        return new MaxAdapterError(
                MaxAdapterError.AD_DISPLAY_FAILED,
                MaxAdapterError.AD_NOT_READY.getCode(),
                MaxAdapterError.AD_NOT_READY.getMessage()
        );
    }
}
