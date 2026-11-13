// Copyright (c) 2026 Tipz Team
// SPDX-License-Identifier: Apache-2.0

package tipz.viola.webview.activity.components

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.suspendCancellableCoroutine
import tipz.viola.R
import tipz.viola.webview.WebPermissionsController
import kotlin.coroutines.resume

class WebPermissionsAlertDialogBuilder(
    context: Context, private val perm: String
) : MaterialAlertDialogBuilder(context) {
    private val webPermsCtl = WebPermissionsController(context)

    init {
        setTitle(R.string.dialog_permission_request_title)
        setMessage(context.resources.getString(
            R.string.dialog_permission_request_message,
            context.resources.getString(WebPermissionsController.run {
                permissionsRes[permissions.indexOf(perm)]})
        ))
    }

    suspend fun askForPermission(): Boolean = suspendCancellableCoroutine { continuation ->
        if (webPermsCtl.checkPerms(perm)[0]) {
            // Return directly if permission granted already
            continuation.resume(true)
            return@suspendCancellableCoroutine
        }

        setPositiveButton(R.string.text_allow) { _, _ ->
            webPermsCtl.allowPerms(perm)
            if (continuation.isActive) continuation.resume(true)
        }
        setNegativeButton(R.string.text_deny) { _, _ ->
            webPermsCtl.denyPerms(perm)
            if (continuation.isActive) continuation.resume(false)
        }
        setOnCancelListener {
            // Treat Back button / outside tap as a denial so the coroutine never hangs
            webPermsCtl.denyPerms(perm)
            if (continuation.isActive) continuation.resume(false)
        }

        val dialog = create()
        continuation.invokeOnCancellation {
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    override fun create(): AlertDialog {
        if (!webPermsCtl.isValidPerm(perm)) {
            // Invalid permission, return empty dialog
            return MaterialAlertDialogBuilder(context).create()
        }
        return super.create()
    }
}