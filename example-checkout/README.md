This is an example application showing how to use the checkout SDK in your app.

In this module, there are two activities doing the same thing with one being in Java and the other Kotlin.
The default one is java as set in the manifest but follow the following steps if you wish to run the
kotlin app easily.

1. Click on the drop down arrow on the left of the device name tab in Android Studio and select `Edit Configurations`

2. Click on the `+` sign in the top left corner and select `Android App`

3. Give the configuration a name like `Example Checkout Kotlin`. You can choose another name if you wish

4. Select the module as `checkout-android.example-checkout`

5. Under `Launch Options`, change from `Default Activity` to `Specified Activity`

6. To select the activity, click on the three dots next to the `Activity` section below the above and choose `ExampleCheckoutKotlinActivity`. Android Studio should fill the qualified name of the activity automatically for you.

7. Select your new configuration and run.

An alternative way although not recommended is to change the manifest itself and set the `ExampleCheckoutKotlinActivity` as the launch activity although this method looses flexibility as you have to remember to change it every time.
