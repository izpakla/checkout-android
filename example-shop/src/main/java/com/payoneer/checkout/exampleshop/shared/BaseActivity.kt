/*
 * Copyright (c) 2020 Payoneer Germany GmbH
 * https://www.payoneer.com
 *
 * This file is open source and available under the MIT license.
 * See the LICENSE file for more information.
 */
package com.payoneer.checkout.exampleshop.shared

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.test.espresso.IdlingResource
import com.payoneer.checkout.CheckoutActivityResult
import com.payoneer.checkout.CheckoutConfiguration
import com.payoneer.checkout.exampleshop.R
import com.payoneer.checkout.ui.dialog.PaymentDialogFactory
import com.payoneer.checkout.ui.dialog.PaymentDialogFragment.PaymentDialogListener
import com.payoneer.checkout.ui.page.idlingresource.SimpleIdlingResource

/**
 * Base Activity for Activities used in this shop, it stores and retrieves the listUrl value.
 */
abstract class BaseActivity : AppCompatActivity() {

    protected var checkoutConfiguration: CheckoutConfiguration? = null
    protected var activityResult: CheckoutActivityResult? = null
    private var resultHandledIdlingResource: SimpleIdlingResource? = null
    private var resultHandled = false

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            supportFinishAfterTransition()
            true
        } else {
            false
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = savedInstanceState ?: intent.extras
        bundle?.let {
            checkoutConfiguration = it.getParcelable(EXTRA_CHECKOUT_CONFIGURATION)
        }
    }

    public override fun onSaveInstanceState(savedInstanceState: Bundle) {
        super.onSaveInstanceState(savedInstanceState)
        savedInstanceState.putParcelable(EXTRA_CHECKOUT_CONFIGURATION, checkoutConfiguration)
    }

    public override fun onResume() {
        super.onResume()
        resultHandled = false
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PAYMENT_REQUEST_CODE || requestCode == EDIT_REQUEST_CODE) {
            activityResult = CheckoutActivityResult.fromActivityResult(requestCode, resultCode, data)
        }
    }

    /**
     * Show error dialog to the user, the payment dialog from the Checkout SDK is used
     * to display a material designed dialog.
     *
     * @param errorResId error resource string id
     */
    open fun showErrorDialog(errorResId: Int) {
        val listener: PaymentDialogListener = object : PaymentDialogListener {
            override fun onPositiveButtonClicked() {
                onErrorDialogClosed()
            }

            override fun onNegativeButtonClicked() {
                onErrorDialogClosed()
            }

            override fun onDismissed() {
                onErrorDialogClosed()
            }
        }

        val title = getString(R.string.dialog_error_title)
        val error = getString(errorResId)
        val tag = "dialog_exampleshop"
        val dialog = PaymentDialogFactory.createMessageDialog(title, error, tag, listener)
        dialog.showDialog(supportFragmentManager, null)
    }

    /**
     * Called when the error dialog has been closed by clicking a button or has been dismissed.
     * Activities extending from this BaseActivity should implement this method in order to receive this event.
     */
    open fun onErrorDialogClosed() {}

    /**
     * Only called from test, creates and returns a new paymentResult handled IdlingResource
     */
    @VisibleForTesting
    fun getResultHandledIdlingResource(): IdlingResource {
        if (resultHandledIdlingResource == null) {
            resultHandledIdlingResource =
                SimpleIdlingResource(javaClass.simpleName + "-resultHandledIdlingResource")
        }
        if (resultHandled) {
            resultHandledIdlingResource!!.setIdleState(true)
        } else {
            resultHandledIdlingResource!!.reset()
        }
        return resultHandledIdlingResource as SimpleIdlingResource
    }

    /**
     * Set the result handled idle state for the IdlingResource
     */
    protected fun setResultHandledIdleState() {
        resultHandled = true
        if (resultHandledIdlingResource != null) {
            resultHandledIdlingResource!!.setIdleState(true)
        }
    }

    companion object {
        const val EXTRA_CHECKOUT_CONFIGURATION = "checkoutconfiguration"
        const val PAYMENT_REQUEST_CODE = 1
        const val EDIT_REQUEST_CODE = 2
    }
}