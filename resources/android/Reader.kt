package com.fzlxtech.plugins.mobile_nfc

import android.content.Intent
import android.net.Uri
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter

object Reader {
    fun recordsFromIntent(intent: Intent): List<NdefRecord> {
        val msgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES) as? Array<NdefMessage>
            ?: return emptyList()
        val list = ArrayList<NdefRecord>()
        msgs.forEach { msg ->
            msg.records.forEach { list.add(it) }
        }
        return list
    }

    fun textFrom(record: NdefRecord): String? {
        if (record.tnf != NdefRecord.TNF_WELL_KNOWN || !record.type.contentEquals(NdefRecord.RTD_TEXT)) {
            return null
        }
        val payload = record.payload ?: return null
        if (payload.isEmpty()) return null
        val status = payload[0].toInt()
        val isUtf8 = (status and 0x80) == 0
        val langLen = status and 0x3F
        val textBytes = payload.copyOfRange(1 + langLen, payload.size)
        return try {
            val charset = if (isUtf8) Charsets.UTF_8 else Charsets.UTF_16
            String(textBytes, charset)
        } catch (_: Exception) {
            null
        }
    }

    fun uriFrom(record: NdefRecord): Uri? {
        return try {
            record.toUri()
        } catch (_: Exception) {
            null
        }
    }

    fun readText(intent: Intent): List<String> {
        return recordsFromIntent(intent).mapNotNull { textFrom(it) }
    }

    fun readUris(intent: Intent): List<String> {
        return recordsFromIntent(intent).mapNotNull { uriFrom(it)?.toString() }
    }
}
