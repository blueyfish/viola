// Copyright (c) 2026 Tipz Team
// SPDX-License-Identifier: Apache-2.0

package tipz.viola.settings

@RequiresOptIn(
    message = "This setting key is deprecated and should only be accessed by migration tools.",
    level = RequiresOptIn.Level.WARNING
)
@Retention(AnnotationRetention.BINARY)
annotation class DeprecatedSettingKey
