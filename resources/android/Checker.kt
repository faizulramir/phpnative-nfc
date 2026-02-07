package com.fzlxtech.plugins.mobile_nfc

import android.content.Context
import android.nfc.NfcAdapter

enum class NfcStatus {
    NotSupported,
    Disabled,
    Enabled
}

object Checker {
    fun isSupported(context: Context): Boolean {
        return NfcAdapter.getDefaultAdapter(context) != null
    }

    fun isEnabled(context: Context): Boolean {
        val adapter = NfcAdapter.getDefaultAdapter(context)
        return adapter?.isEnabled == true
    }

    fun status(context: Context): NfcStatus {
        val adapter = NfcAdapter.getDefaultAdapter(context)
        return when {
            adapter == null -> NfcStatus.NotSupported
            adapter.isEnabled -> NfcStatus.Enabled
            else -> NfcStatus.Disabled
        }
    }
}
