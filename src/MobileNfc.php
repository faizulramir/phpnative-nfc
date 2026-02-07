<?php

namespace Fzlxtech\MobileNfc;

class MobileNfc
{
    /**
     * Execute the plugin functionality
     */
    public function execute(array $options = []): mixed
    {
        if (function_exists('nativephp_call')) {
            $result = nativephp_call('MobileNfc.Execute', json_encode($options));

            if ($result) {
                $decoded = json_decode($result);
                return $decoded->data ?? null;
            }
        }

        return null;
    }

    /**
     * Get the current status
     */
    public function getStatus(): ?object
    {
        if (function_exists('nativephp_call')) {
            $result = nativephp_call('MobileNfc.GetStatus', '{}');

            if ($result) {
                $decoded = json_decode($result);
                return $decoded->data ?? null;
            }
        }

        return null;
    }

    public function status(): ?object
    {
        if (function_exists('nativephp_call')) {
            $result = nativephp_call('MobileNfc.Status', '{}');
            if ($result) {
                $decoded = json_decode($result);
                return $decoded->data ?? null;
            }
        }
        return null;
    }

    public function isSupported(): bool
    {
        if (function_exists('nativephp_call')) {
            $result = nativephp_call('MobileNfc.IsSupported', '{}');
            if ($result) {
                $decoded = json_decode($result);
                return (bool) (($decoded->data->supported ?? false));
            }
        }
        return false;
    }

    public function isEnabled(): bool
    {
        if (function_exists('nativephp_call')) {
            $result = nativephp_call('MobileNfc.IsEnabled', '{}');
            if ($result) {
                $decoded = json_decode($result);
                return (bool) (($decoded->data->enabled ?? false));
            }
        }
        return false;
    }

    public function readNdef(): ?object
    {
        if (function_exists('nativephp_call')) {
            $result = nativephp_call('MobileNfc.ReadNdef', '{}');
            if ($result) {
                $decoded = json_decode($result);
                return $decoded->data ?? null;
            }
        }
        return null;
    }

    public function writeNdef(string $base64Message): bool
    {
        if (function_exists('nativephp_call')) {
            $result = nativephp_call('MobileNfc.WriteNdef', json_encode(['message' => $base64Message]));
            if ($result) {
                $decoded = json_decode($result, true);
                $status = $decoded['status'] ?? null;
                return $status === 'Success';
            }
        }
        return false;
    }

    public function transceive(string $base64Command): ?string
    {
        if (function_exists('nativephp_call')) {
            $result = nativephp_call('MobileNfc.Transceive', json_encode(['command' => $base64Command]));
            if ($result) {
                $decoded = json_decode($result);
                return $decoded->data->response ?? null;
            }
        }
        return null;
    }
}
