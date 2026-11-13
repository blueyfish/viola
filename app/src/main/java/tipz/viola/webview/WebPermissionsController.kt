// Copyright (c) 2026 Tipz Team
// SPDX-License-Identifier: Apache-2.0

package tipz.viola.webview

import android.content.Context
import android.util.Log
import tipz.viola.R
import tipz.viola.settings.SettingsKeys
import tipz.viola.settings.SettingsSharedPreference

class WebPermissionsController(context: Context) {
    private val LOG_TAG = "WebPermsCtl"

    private val settingsPreference = SettingsSharedPreference(context)
    private fun getCurrentPerms(): MutableSet<String> =
        settingsPreference.getString(SettingsKeys.webPermissions).split(",").toMutableSet()
    private fun setCurrentPerms(set: MutableSet<String>) =
        settingsPreference.setString(SettingsKeys.webPermissions, set.joinToString(","))
    fun isValidPerm(perm: String): Boolean = perm in permissions

    /* Check permissions tools */
    fun checkPerms(vararg perms: String): BooleanArray {
        perms.firstOrNull { it !in permissions }?.let {
            Log.e(LOG_TAG, "Invalid permission: $it")
            return booleanArrayOf()
        }
        return getCurrentPerms().run {
            BooleanArray(perms.size) {
                Log.d(LOG_TAG, "Checking permission: ${perms[it]}")
                perms[it] in this
            }
        }
    }

    fun checkAllPerms(): BooleanArray {
        val permsSet = getCurrentPerms()
        return BooleanArray(permissions.size) { permissions[it] in permsSet }
    }

    /* Modify permissions tools */
    fun allowPerms(vararg perms: String) {
        val (validPerms, invalidPerms) = perms.partition { it in permissions }
        invalidPerms.forEach { Log.e(LOG_TAG, "Invalid permission: $it") }
        setCurrentPerms(getCurrentPerms().apply { addAll(validPerms) })
    }

    fun denyPerms(vararg perms: String) {
        val (validPerms, invalidPerms) = perms.partition { it in permissions }
        invalidPerms.forEach { Log.e(LOG_TAG, "Invalid permission: $it") }
        setCurrentPerms(getCurrentPerms().apply { removeAll(validPerms) })
    }

    fun updatePerms(perms: BooleanArray) {
        setCurrentPerms(getCurrentPerms().apply {
            permissions.forEachIndexed { i, item ->
                if (perms[i]) add(item) else remove(item)
            }
        })
    }

    companion object {
        const val PERMISSION_CAMERA = "camera"
        const val PERMISSION_DRM_CONTENT = "drm_content"
        const val PERMISSION_LOCATION = "location"
        const val PERMISSION_MICROPHONE = "microphone"
        const val PERMISSION_MIDI_SYSEX = "midi_sysex"

        // The big list of permissions
        val permissions = arrayOf(
            PERMISSION_CAMERA,
            PERMISSION_DRM_CONTENT,
            PERMISSION_LOCATION,
            PERMISSION_MICROPHONE,
            PERMISSION_MIDI_SYSEX
        )
        val permissionsRes = arrayOf(
            R.string.permission_camera,
            R.string.permission_drm_content,
            R.string.permission_location,
            R.string.permission_microphone,
            R.string.permission_midi_sysex
        )
    }
}