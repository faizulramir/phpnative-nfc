import CoreNFC
import Foundation

enum NfcWriteResult {
    case success
    case notSupported
    case notWritable
    case tooLarge
    case failed
}

class Writer {
    static func textRecord(_ text: String, languageCode: String = "en", encodeInUtf8: Bool = true) -> NFCNDEFPayload? {
        let lang = languageCode.data(using: .ascii) ?? Data()
        let encoding: String.Encoding = encodeInUtf8 ? .utf8 : .utf16
        guard let txt = text.data(using: encoding) else { return nil }
        let status: UInt8 = (encodeInUtf8 ? 0x00 : 0x80) | UInt8(lang.count & 0x3F)
        var payload = Data([status])
        payload.append(lang)
        payload.append(txt)
        guard let typeData = "T".data(using: .ascii) else { return nil }
        return NFCNDEFPayload(format: .nfcWellKnown, type: typeData, identifier: Data(), payload: payload)
    }

    static func uriRecord(_ uri: String) -> NFCNDEFPayload? {
        guard let url = URL(string: uri) else { return nil }
        return NFCNDEFPayload.wellKnownTypeURIPayload(url)
    }

    static func mimeRecord(_ mimeType: String, payload: Data) -> NFCNDEFPayload? {
        guard let typeData = mimeType.data(using: .utf8) else { return nil }
        return NFCNDEFPayload(format: .media, type: typeData, identifier: Data(), payload: payload)
    }

    static func message(_ records: [NFCNDEFPayload]) -> NFCNDEFMessage {
        return NFCNDEFMessage(records: records)
    }

    static func messageFromItems(texts: [String] = [], uris: [String] = [], mimes: [String: Data] = [:]) -> NFCNDEFMessage {
        var recs: [NFCNDEFPayload] = []
        texts.forEach { t in
            if let r = textRecord(t) { recs.append(r) }
        }
        uris.forEach { u in
            if let r = uriRecord(u) { recs.append(r) }
        }
        mimes.forEach { k, v in
            if let r = mimeRecord(k, payload: v) { recs.append(r) }
        }
        return NFCNDEFMessage(records: recs)
    }

    static func write(tag: NFCTag, message: NFCNDEFMessage, completion: @escaping (NfcWriteResult) -> Void) {
        switch tag {
        case .miFare(let t):
            t.queryNDEFStatus { status, capacity, error in
                if let _ = error { completion(.failed); return }
                switch status {
                case .notSupported:
                    completion(.notSupported)
                case .readOnly:
                    completion(.notWritable)
                case .readWrite:
                    let size = message.length
                    if size > capacity {
                        completion(.tooLarge)
                    } else {
                        t.writeNDEF(message) { err in
                            completion(err == nil ? .success : .failed)
                        }
                    }
                @unknown default:
                    completion(.failed)
                }
            }
        case .iso7816(let t):
            t.queryNDEFStatus { status, capacity, error in
                if let _ = error { completion(.failed); return }
                switch status {
                case .notSupported:
                    completion(.notSupported)
                case .readOnly:
                    completion(.notWritable)
                case .readWrite:
                    let size = message.length
                    if size > capacity {
                        completion(.tooLarge)
                    } else {
                        t.writeNDEF(message) { err in
                            completion(err == nil ? .success : .failed)
                        }
                    }
                @unknown default:
                    completion(.failed)
                }
            }
        case .iso15693(let t):
            t.queryNDEFStatus { status, capacity, error in
                if let _ = error { completion(.failed); return }
                switch status {
                case .notSupported:
                    completion(.notSupported)
                case .readOnly:
                    completion(.notWritable)
                case .readWrite:
                    let size = message.length
                    if size > capacity {
                        completion(.tooLarge)
                    } else {
                        t.writeNDEF(message) { err in
                            completion(err == nil ? .success : .failed)
                        }
                    }
                @unknown default:
                    completion(.failed)
                }
            }
        case .feliCa(let t):
            t.queryNDEFStatus { status, capacity, error in
                if let _ = error { completion(.failed); return }
                switch status {
                case .notSupported:
                    completion(.notSupported)
                case .readOnly:
                    completion(.notWritable)
                case .readWrite:
                    let size = message.length
                    if size > capacity {
                        completion(.tooLarge)
                    } else {
                        t.writeNDEF(message) { err in
                            completion(err == nil ? .success : .failed)
                        }
                    }
                @unknown default:
                    completion(.failed)
                }
            }
        @unknown default:
            completion(.failed)
        }
    }
}
