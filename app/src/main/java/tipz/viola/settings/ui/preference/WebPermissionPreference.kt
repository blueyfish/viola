// Copyright (c) 2026 Tipz Team
// SPDX-License-Identifier: Apache-2.0

package tipz.viola.settings.ui.preference

import android.content.Context
import android.util.AttributeSet
import androidx.preference.Preference
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import tipz.viola.R
import tipz.viola.settings.SettingsSharedPreference
import tipz.viola.webview.WebPermissionsController

class WebPermissionPreference(
    context: Context,
    attrs: AttributeSet
) : Preference(context, attrs) {
    private val settingsPreference = SettingsSharedPreference(context)
    private val webPermsCtl = WebPermissionsController(context)

    init {
        setTitle(R.string.pref_web_permissions_title)
        setOnPreferenceClickListener {
            val permissions = WebPermissionsController.permissionsRes
                .map { resId -> context.resources.getString(resId) }
                .toTypedArray()
            val checkedItems = webPermsCtl.checkAllPerms()

            MaterialAlertDialogBuilder(context)
                .setTitle(R.string.pref_web_permissions_title)
                .setMultiChoiceItems(permissions, checkedItems) { _, which, isChecked ->
                    checkedItems[which] = isChecked
                }
                .setPositiveButton(android.R.string.ok) { _, _ ->
                    webPermsCtl.updatePerms(checkedItems)
                }
                .setNegativeButton(android.R.string.cancel, null)
                .create().show()
            true
        }
    }
}