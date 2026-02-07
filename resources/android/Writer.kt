package com.fzlxtech.plugins.mobile_nfc

import android.content.Intent
import android.net.Uri
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.nfc.tech.NdefFormatable

enum class NfcWriteResult {
    Success,
    NotSupported,
    NotWritable,
    TooLarge,
    Failed
}

object Writer {
    fun textRecord(text: String, languageCode: String = "en", encodeInUtf8: Boolean = true): NdefRecord {
        val lang = languageCode.toByteArray(Charsets.US_ASCII)
        val txt = text.toByteArray(if (encodeInUtf8) Charsets.UTF_8 else Charsets.UTF_16)
        val status = ((if (encodeInUtf8) 0 else 0x80) + lang.size).toByte()
        val payload = ByteArray(1 + lang.size + txt.size)
        payload[0] = status
        System.arraycopy(lang, 0, payload, 1, lang.size)
        System.arraycopy(txt, 0, payload, 1 + lang.size, txt.size)
        return NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, ByteArray(0), payload)
    }

    fun uriRecord(uri: String): NdefRecord {
        return NdefRecord.createUri(Uri.parse(uri))
    }

    fun mimeRecord(mimeType: String, payload: ByteArray): NdefRecord {
        return NdefRecord.createMime(mimeType, payload)
    }

    fun message(vararg records: NdefRecord): NdefMessage {
        return NdefMessage(records)
    }

    fun messageFromItems(
        texts: List<String> = emptyList(),
        uris: List<String> = emptyList(),
        mimes: Map<String, ByteArray> = emptyMap()
    ): NdefMessage {
        val recs = ArrayList<NdefRecord>()
        texts.forEach { recs.add(textRecord(it)) }
        uris.forEach { recs.add(uriRecord(it)) }
        mimes.forEach { (type, data) -> recs.add(mimeRecord(type, data)) }
        return NdefMessage(recs.toTypedArray())
    }

    fun write(tag: Tag, message: NdefMessage): NfcWriteResult {
        return try {
            val ndef = Ndef.get(tag)
            if (ndef != null) {
                ndef.connect()
                if (!ndef.isWritable) {
                    ndef.close()
                    NfcWriteResult.NotWritable
                } else {
                    val size = message.toByteArray().size
                    if (size > ndef.maxSize) {
                        ndef.close()
                        NfcWriteResult.TooLarge
                    } else {
                        ndef.writeNdefMessage(message)
                        ndef.close()
                        NfcWriteResult.Success
                    }
                }
            } else {
                val formatable = NdefFormatable.get(tag)
                if (formatable != null) {
                    formatable.connect()
                    formatable.format(message)
                    formatable.close()
                    NfcWriteResult.Success
                } else {
                    NfcWriteResult.NotSupported
                }
            }
        } catch (_: Exception) {
            NfcWriteResult.Failed
        }
    }

    fun writeFromIntent(intent: Intent, message: NdefMessage): NfcWriteResult {
        val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG) ?: return NfcWriteResult.Failed
        return write(tag, message)
    }

    fun writeText(intent: Intent, text: String, languageCode: String = "en", encodeInUtf8: Boolean = true): NfcWriteResult {
        val record = textRecord(text, languageCode, encodeInUtf8)
        return writeFromIntent(intent, message(record))
    }

    fun writeUri(intent: Intent, uri: String): NfcWriteResult {
        val record = uriRecord(uri)
        return writeFromIntent(intent, message(record))
    }
}
