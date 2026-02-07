package com.fzlxtech.plugins.mobile_nfc

import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.nfc.tech.NfcA
import android.nfc.tech.NfcB
import android.nfc.tech.NfcF
import android.nfc.tech.NfcV

enum class TransceiveStatus {
    Success,
    NotSupported,
    Failed
}

data class TransceiveResult(
    val status: TransceiveStatus,
    val response: ByteArray? = null
)

object Transceive {
    fun transceive(tag: Tag, command: ByteArray): TransceiveResult {
        return try {
            IsoDep.get(tag)?.let { tech ->
                tech.connect()
                val resp = tech.transceive(command)
                tech.close()
                return TransceiveResult(TransceiveStatus.Success, resp)
            }
            NfcA.get(tag)?.let { tech ->
                tech.connect()
                val resp = tech.transceive(command)
                tech.close()
                return TransceiveResult(TransceiveStatus.Success, resp)
            }
            NfcB.get(tag)?.let { tech ->
                tech.connect()
                val resp = tech.transceive(command)
                tech.close()
                return TransceiveResult(TransceiveStatus.Success, resp)
            }
            NfcF.get(tag)?.let { tech ->
                tech.connect()
                val resp = tech.transceive(command)
                tech.close()
                return TransceiveResult(TransceiveStatus.Success, resp)
            }
            NfcV.get(tag)?.let { tech ->
                tech.connect()
                val resp = tech.transceive(command)
                tech.close()
                return TransceiveResult(TransceiveStatus.Success, resp)
            }
            TransceiveResult(TransceiveStatus.NotSupported, null)
        } catch (_: Exception) {
            TransceiveResult(TransceiveStatus.Failed, null)
        }
    }

    fun transceiveFromIntent(intent: Intent, command: ByteArray): TransceiveResult {
        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG) ?: return TransceiveResult(TransceiveStatus.Failed, null)
        return transceive(tag, command)
    }
}
