import CoreNFC
import Foundation

enum NfcStatus {
    case notSupported
    case enabled
}

class Checker {
    static func isSupported() -> Bool {
        return NFCNDEFReaderSession.readingAvailable
    }

    static func isEnabled() -> Bool {
        return NFCNDEFReaderSession.readingAvailable
    }

    static func status() -> NfcStatus {
        return NFCNDEFReaderSession.readingAvailable ? .enabled : .notSupported
    }
}
