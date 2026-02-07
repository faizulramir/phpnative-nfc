import CoreNFC
import Foundation

class Reader {
    static func records(from message: NFCNDEFMessage) -> [NFCNDEFPayload] {
        return message.records
    }

    static func text(from payload: NFCNDEFPayload) -> String? {
        guard payload.typeNameFormat == .nfcWellKnown else { return nil }
        guard String(data: payload.type, encoding: .ascii) == "T" else { return nil }
        let data = payload.payload
        guard data.count > 1 else { return nil }
        let status = data[0]
        let isUtf8 = (status & 0x80) == 0
        let langLen = Int(status & 0x3F)
        if data.count < 1 + langLen { return nil }
        let textBytes = data.subdata(in: (1 + langLen)..<data.count)
        return String(data: textBytes, encoding: isUtf8 ? .utf8 : .utf16)
    }

    static func uri(from payload: NFCNDEFPayload) -> URL? {
        return payload.wellKnownTypeURIPayload()
    }
}
