<?php

namespace Fzlxtech\MobileNfc\Facades;

use Illuminate\Support\Facades\Facade;

/**
 * @method static mixed execute(array $options = [])
 * @method static object|null getStatus()
 *
 * @see \Fzlxtech\MobileNfc\MobileNfc
 */
class MobileNfc extends Facade
{
    protected static function getFacadeAccessor(): string
    {
        return \Fzlxtech\MobileNfc\MobileNfc::class;
    }
}