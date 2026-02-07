<?php

namespace Fzlxtech\MobileNfc;

use Illuminate\Support\ServiceProvider;
use Fzlxtech\MobileNfc\Commands\CopyAssetsCommand;

class MobileNfcServiceProvider extends ServiceProvider
{
    public function register(): void
    {
        $this->app->singleton(MobileNfc::class, function () {
            return new MobileNfc();
        });
    }

    public function boot(): void
    {
        // Register plugin hook commands
        if ($this->app->runningInConsole()) {
            $this->commands([
                CopyAssetsCommand::class,
            ]);
        }
    }
}