@file:OptIn(ExperimentalForeignApi::class)

package com.bazaar.cli

import kotlinx.cinterop.ExperimentalForeignApi
import platform.posix.fprintf
import platform.posix.stderr

internal actual fun printStderr(message: String) {
    fprintf(stderr, "%s\n", message)
}
