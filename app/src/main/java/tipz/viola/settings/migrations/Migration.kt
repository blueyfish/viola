// Copyright (c) 2026 Tipz Team
// SPDX-License-Identifier: Apache-2.0

package tipz.viola.settings.migrations

import android.util.Log
import tipz.viola.settings.SettingsKeys
import tipz.viola.settings.SettingsSharedPreference

abstract class Migration(private val minLevel: Int) {
    abstract val keyPairsUpdate: Array<Pair<String, Any>>
    abstract val keysRemoval: Array<String>
    abstract fun process(pref: SettingsSharedPreference)

    companion object {
        const val LOG_TAG = "MigrationHelper"

        fun migrationProcess(pref: SettingsSharedPreference, migration: Migration) {
            val currentProto = pref.getInt(SettingsKeys.protocolVersion)
            val currentProtoFinal = pref.getIntBool(SettingsKeys.protocolVersionFinal)

            if (currentProto > migration.minLevel + 1
                || (currentProto == migration.minLevel + 1 && currentProtoFinal))
                return

            migration.minLevel.let {
                Log.d(LOG_TAG, "Performing preference migration ($it -> ${it + 1})")
            }

            migration.keyPairsUpdate.forEach {
                if (pref.contains(it.first))
                    return@forEach

                if (it.second is String)
                    pref.setString(it.first, it.second as String)
                else if (it.second is Int)
                    pref.setInt(it.first, it.second as Int)
                else
                    Log.w(LOG_TAG, "Invalid key pair format")
            }

            migration.process(pref)
            migration.keysRemoval.forEach {
                pref.remove(it)
            }
        }
    }
}