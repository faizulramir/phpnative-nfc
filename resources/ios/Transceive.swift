import CoreNFC
import Foundation

enum TransceiveStatus {
    case success
    case notSupported
    case failed
}

struct TransceiveResult {
    let status: TransceiveStatus
    let response: Data?
}

class Transceive {
    static func transceiveISO7816(tag: NFCISO7816Tag, apdu: NFCISO7816APDU, completion: @escaping (TransceiveResult) -> Void) {
        tag.sendCommand(apdu: apdu) { response, sw1, sw2, error in
            if let _ = error {
                completion(TransceiveResult(status: .failed, response: nil))
            } else {
                var result = Data(response)
                result.append(sw1)
                result.append(sw2)
                completion(TransceiveResult(status: .success, response: result))
            }
        }
    }

    static func transceiveMiFare(tag: NFCMiFareTag, command: Data, completion: @escaping (TransceiveResult) -> Void) {
        tag.sendMiFareCommand(command) { response, error in
            if let _ = error {
                completion(TransceiveResult(status: .failed, response: nil))
            } else {
                completion(TransceiveResult(status: .success, response: response))
            }
        }
    }

    static func transceiveFeliCa(tag: NFCFeliCaTag, commandPacket: Data, completion: @escaping (TransceiveResult) -> Void) {
        tag.sendFeliCaCommand(commandPacket) { statusFlag1, statusFlag2, data, error in
            if let _ = error {
                completion(TransceiveResult(status: .failed, response: nil))
            } else {
                var result = Data()
                result.append(statusFlag1)
                result.append(statusFlag2)
                result.append(data)
                completion(TransceiveResult(status: .success, response: result))
            }
        }
    }

    static func transceiveISO15693(tag: NFCISO15693Tag, requestFlags: NFCISO15693RequestFlag, commandCode: UInt8, data: Data, completion: @escaping (TransceiveResult) -> Void) {
        tag.customCommand(requestFlags: requestFlags, customCommandCode: commandCode, customRequestParameters: data) { response, error in
            if let _ = error {
                completion(TransceiveResult(status: .failed, response: nil))
            } else {
                completion(TransceiveResult(status: .success, response: response))
            }
        }
    }
}
