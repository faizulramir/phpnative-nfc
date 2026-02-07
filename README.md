# MobileNfc Plugin for NativePHP Mobile

NFC utilities for NativePHP: status checks, NDEF read/write, and raw transceive.

## Installation

```bash
composer require fzlxtech/plugin-mobile-nfc
```

## Bridge Functions

- MobileNfc.Status
- MobileNfc.IsSupported
- MobileNfc.IsEnabled
- MobileNfc.ReadNdef
- MobileNfc.WriteNdef
- MobileNfc.Transceive

## PHP Usage

```php
use Fzlxtech\MobileNfc\MobileNfc;

$nfc = app(MobileNfc::class);

// Status and capability
$status = $nfc->status();        // { supported, enabled, value }
$supported = $nfc->isSupported(); // bool
$enabled = $nfc->isEnabled();     // bool

// Read NDEF (returns { id, message(base64) } or null)
$ndef = $nfc->readNdef();

// Write NDEF (base64-encoded NDEF message buffer)
$ok = $nfc->writeNdef($base64Message);

// Transceive raw command (base64-encoded) and get base64 response
$responseBase64 = $nfc->transceive($commandBase64);
```

## Frontend Usage

### React

```tsx
import { useState } from 'react';

export default function NfcPage() {
  const [info, setInfo] = useState<any>(null);
  const [ndef, setNdef] = useState<any>(null);
  const [writeStatus, setWriteStatus] = useState<string>('');

  async function getStatus() {
    const r = await fetch('/nfc/status', { method: 'POST' });
    setInfo(await r.json());
  }

  async function readNdef() {
    const r = await fetch('/nfc/read', { method: 'POST' });
    setNdef(await r.json());
  }

  async function writeNdef(base64Message: string) {
    const r = await fetch('/nfc/write', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ message: base64Message }),
    });
    const data = await r.json();
    setWriteStatus(data.status ?? '');
  }

  return (
    <div>
      <button onClick={getStatus}>Get NFC Status</button>
      <button onClick={readNdef}>Read NDEF</button>
      <button onClick={() => writeNdef('BASE64_NDEF_MESSAGE')}>Write NDEF</button>
      <pre>{JSON.stringify({ info, ndef, writeStatus }, null, 2)}</pre>
    </div>
  );
}
```

Server routes (example):

```php
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Route;
use Fzlxtech\MobileNfc\MobileNfc;

Route::post('/nfc/status', function (Request $r, MobileNfc $nfc) {
    return response()->json($nfc->status());
});

Route::post('/nfc/read', function (Request $r, MobileNfc $nfc) {
    return response()->json($nfc->readNdef());
});

Route::post('/nfc/write', function (Request $r, MobileNfc $nfc) {
    $ok = $nfc->writeNdef($r->string('message'));
    return response()->json(['status' => $ok ? 'Success' : 'Failed']);
});
```

### Livewire

```php
namespace App\Livewire;

use Livewire\Component;
use Fzlxtech\MobileNfc\MobileNfc;

class NfcPanel extends Component
{
    public $status;
    public $ndef;
    public $writeStatus;

    public function getStatus(MobileNfc $nfc)
    {
        $this->status = $nfc->status();
    }

    public function read(MobileNfc $nfc)
    {
        $this->ndef = $nfc->readNdef();
    }

    public function write(MobileNfc $nfc, string $message)
    {
        $ok = $nfc->writeNdef($message);
        $this->writeStatus = $ok ? 'Success' : 'Failed';
    }

    public function render()
    {
        return view('livewire.nfc-panel');
    }
}
```

Blade:

```blade
<div>
    <button wire:click="getStatus">Get NFC Status</button>
    <button wire:click="read">Read NDEF</button>
    <button wire:click="write('BASE64_NDEF_MESSAGE')">Write NDEF</button>
    <pre>{{ json_encode([$status, $ndef, $writeStatus], JSON_PRETTY_PRINT) }}</pre>
    @if($status && ($status['enabled'] ?? false) === false)
        <p>NFC disabled. Enable it in system settings.</p>
    @endif
    @if(!$status || ($status['supported'] ?? false) === false)
        <p>Device does not support NFC.</p>
    @endif
</div>
```

### Vue

```vue
<template>
  <div>
    <button @click="getStatus">Get NFC Status</button>
    <button @click="readNdef">Read NDEF</button>
    <button @click="writeNdef('BASE64_NDEF_MESSAGE')">Write NDEF</button>
    <pre>{{ dump }}</pre>
  </div>
</template>

<script setup>
import { ref } from 'vue';

const info = ref(null);
const ndef = ref(null);
const writeStatus = ref('');
const dump = computed(() => JSON.stringify({ info: info.value, ndef: ndef.value, writeStatus: writeStatus.value }, null, 2));

async function getStatus() {
  const r = await fetch('/nfc/status', { method: 'POST' });
  info.value = await r.json();
}

async function readNdef() {
  const r = await fetch('/nfc/read', { method: 'POST' });
  ndef.value = await r.json();
}

async function writeNdef(base64Message) {
  const r = await fetch('/nfc/write', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ message: base64Message }),
  });
  const data = await r.json();
  writeStatus.value = data.status ?? '';
}
</script>
```

## Notes

- Android reads the tag from the current Activity intent; ensure the app is in foreground when tapping a tag.
- iOS bridges for ReadNdef/WriteNdef/Transceive are stubbed; a session coordinator can be added to enable them.

## License

MIT
