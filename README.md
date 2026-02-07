# MobileNfc Plugin for NativePHP Mobile

A NativePHP Mobile plugin

## Installation

```bash
composer require fzlxtech/plugin-mobile-nfc
```

## Usage

```php
use Fzlxtech\MobileNfc\Facades\MobileNfc;

// Execute functionality
$result = MobileNfc::execute(['option1' => 'value']);

// Get status
$status = MobileNfc::getStatus();
```

## Listening for Events

```php
use Livewire\Attributes\On;

#[On('native:Fzlxtech\MobileNfc\Events\MobileNfcCompleted')]
public function handleMobileNfcCompleted($result, $id = null)
{
    // Handle the event
}
```

## License

MIT