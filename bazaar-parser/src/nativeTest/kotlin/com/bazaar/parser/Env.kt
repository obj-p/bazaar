@file:OptIn(ExperimentalForeignApi::class)

package com.bazaar.parser

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.toKString
import platform.posix.getenv

internal actual fun getEnv(name: String): String? = getenv(name)?.toKString()
