package com.fzlxtech.plugins.mobile_nfc

import androidx.fragment.app.FragmentActivity
import android.content.Context
import android.nfc.NfcAdapter
import android.nfc.NdefMessage
import android.util.Base64
import com.nativephp.mobile.bridge.BridgeFunction
import com.nativephp.mobile.bridge.BridgeResponse

object MobileNfcFunctions {

    class Execute(private val activity: FragmentActivity) : BridgeFunction {
        override fun execute(parameters: Map<String, Any>): Map<String, Any> {
            // TODO: Implement your native functionality here
            val option1 = parameters["option1"] as? String ?: ""

            // Example: Return success with data
            return BridgeResponse.success(mapOf(
                "result" to "executed",
                "option1" to option1
            ))
        }
    }

    class GetStatus(private val context: Context) : BridgeFunction {
        override fun execute(parameters: Map<String, Any>): Map<String, Any> {
            // TODO: Return current status
            return BridgeResponse.success(mapOf(
                "status" to "ready",
                "version" to "1.0.0"
            ))
        }
    }

    class Status(private val activity: FragmentActivity) : BridgeFunction {
        override fun execute(parameters: Map<String, Any>): Map<String, Any> {
            val status = Checker.status(activity)
            return BridgeResponse.success(mapOf(
                "supported" to (status != NfcStatus.NotSupported),
                "enabled" to (status == NfcStatus.Enabled),
                "value" to status.name
            ))
        }
    }

    class IsSupported(private val activity: FragmentActivity) : BridgeFunction {
        override fun execute(parameters: Map<String, Any>): Map<String, Any> {
            return BridgeResponse.success(mapOf(
                "supported" to Checker.isSupported(activity)
            ))
        }
    }

    class IsEnabled(private val activity: FragmentActivity) : BridgeFunction {
        override fun execute(parameters: Map<String, Any>): Map<String, Any> {
            return BridgeResponse.success(mapOf(
                "enabled" to Checker.isEnabled(activity)
            ))
        }
    }

    class ReadNdef(private val activity: FragmentActivity) : BridgeFunction {
        override fun execute(parameters: Map<String, Any>): Map<String, Any> {
            val intent = activity.intent
            val tag = intent?.getParcelableExtra<android.nfc.Tag>(NfcAdapter.EXTRA_TAG)
                ?: return BridgeResponse.error("waiting", mapOf("message" to "No tag in intent"))

            val idBytes = tag.id ?: ByteArray(0)
            val idHex = idBytes.joinToString("") { "%02X".format(it) }

            val msgs = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES) as? Array<NdefMessage>
            if (msgs != null && msgs.isNotEmpty()) {
                val msgB64 = Base64.encodeToString(msgs[0].toByteArray(), Base64.NO_WRAP)
                return BridgeResponse.success(mapOf("id" to idHex, "message" to msgB64))
            }

            return BridgeResponse.error("error", mapOf("message" to "No NDEF message", "id" to idHex))
        }
    }

    class WriteNdef(private val activity: FragmentActivity) : BridgeFunction {
        override fun execute(parameters: Map<String, Any>): Map<String, Any> {
            val messageB64 = parameters["message"] as? String ?: return BridgeResponse.error("error", mapOf("message" to "Missing 'message' (base64)"))
            val msgBytes = try {
                Base64.decode(messageB64, Base64.DEFAULT)
            } catch (e: Exception) {
                return BridgeResponse.error("error", mapOf("message" to "Invalid base64"))
            }
            val intent = activity.intent
            val tag = intent?.getParcelableExtra<android.nfc.Tag>(NfcAdapter.EXTRA_TAG)
                ?: return BridgeResponse.error("waiting", mapOf("message" to "No tag in intent"))
            val message = try {
                NdefMessage(msgBytes)
            } catch (_: Exception) {
                return BridgeResponse.error("error", mapOf("message" to "Invalid NDEF message"))
            }
            val result = Writer.write(tag, message)
            return mapOf("status" to result.name)
        }
    }

    class Transceive(private val activity: FragmentActivity) : BridgeFunction {
        override fun execute(parameters: Map<String, Any>): Map<String, Any> {
            val cmdB64 = parameters["command"] as? String ?: return BridgeResponse.error("error", mapOf("message" to "Missing 'command'"))
            val command = try {
                Base64.decode(cmdB64, Base64.DEFAULT)
            } catch (e: Exception) {
                return BridgeResponse.error("error", mapOf("message" to "Invalid base64"))
            }
            val intent = activity.intent
            val tag = intent?.getParcelableExtra<android.nfc.Tag>(NfcAdapter.EXTRA_TAG)
                ?: return BridgeResponse.error("waiting", mapOf("message" to "No tag in intent"))
            val res = Transceive.transceive(tag, command)
            return when (res.status) {
                TransceiveStatus.Success -> BridgeResponse.success(mapOf(
                    "response" to Base64.encodeToString(res.response ?: ByteArray(0), Base64.NO_WRAP)
                ))
                TransceiveStatus.NotSupported -> BridgeResponse.error("NotSupported")
                TransceiveStatus.Failed -> BridgeResponse.error("Failed")
            }
        }
    }
}
