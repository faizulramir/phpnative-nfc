## fzlxtech/plugin-mobile-nfc

A NativePHP Mobile plugin

### Installation

```bash
composer require fzlxtech/plugin-mobile-nfc
```

### PHP Usage (Livewire/Blade)

Use the `MobileNfc` facade:

@verbatim
<code-snippet name="Using MobileNfc Facade" lang="php">
use Fzlxtech\MobileNfc\Facades\MobileNfc;

// Execute the plugin functionality
$result = MobileNfc::execute(['option1' => 'value']);

// Get the current status
$status = MobileNfc::getStatus();
</code-snippet>
@endverbatim

### Available Methods

- `MobileNfc::execute()`: Execute the plugin functionality
- `MobileNfc::getStatus()`: Get the current status

### Events

- `MobileNfcCompleted`: Listen with `#[OnNative(MobileNfcCompleted::class)]`

@verbatim
<code-snippet name="Listening for MobileNfc Events" lang="php">
use Native\Mobile\Attributes\OnNative;
use Fzlxtech\MobileNfc\Events\MobileNfcCompleted;

#[OnNative(MobileNfcCompleted::class)]
public function handleMobileNfcCompleted($result, $id = null)
{
    // Handle the event
}
</code-snippet>
@endverbatim

### JavaScript Usage (Vue/React/Inertia)

@verbatim
<code-snippet name="Using MobileNfc in JavaScript" lang="javascript">
import { mobileNfc } from '@fzlxtech/plugin-mobile-nfc';

// Execute the plugin functionality
const result = await mobileNfc.execute({ option1: 'value' });

// Get the current status
const status = await mobileNfc.getStatus();
</code-snippet>
@endverbatim