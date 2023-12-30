package app.cartcollaborate

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity


class ActivityOne : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_1)

        // reference to the submit name button
        findViewById<Button>(R.id.SubmitNamesBtn)
            .setOnClickListener {

            // stores the user's input
            val userInput = findViewById<EditText>(R.id.userInput)

            // if input is not empty
            if (userInput.text.isNullOrBlank()) { "Missing Names".showDialog("Please enter names.")
            }

            else {
                val payerNamesArray: Array<String> = userInput.text.toString().formatInput()

                val sendPayerNames = Intent(this, ActivityTwo::class.java).apply {
                    putExtra("userInput", payerNamesArray)
                }

                startActivity(sendPayerNames)

                // clears input box
                Looper.myLooper()?.let {
                    Handler(it).postDelayed({
                        userInput.setText("")
                    }, 500)
                }
            }
        }
    }

    private fun String.showDialog(msg: String) {
        // Inflate the custom layout
        val view = layoutInflater.inflate(R.layout.dialogue_box, null)

        // Set the title and message in the custom layout
        view.findViewById<TextView>(R.id.dialogueTitle).text = this
        view.findViewById<TextView>(R.id.dialogueMsg).text = msg

        // Create AlertDialog with the custom layout
        val builder = AlertDialog.Builder(this@ActivityOne)
        builder.setView(view)

        // Create and show the AlertDialog
        val alertDialog = builder.create()
        alertDialog.show()

        // closes the alert box in 1700 ms
        Looper.myLooper()?.let {
            Handler(it).postDelayed({
                alertDialog.dismiss()
            }, 1700)
        }

    }

    // splits string into an array can capitalizes each element
    private fun String.formatInput(): Array<String> {
        return this.trim().lowercase().split(" ")
            .joinToString("") { it.replaceFirstChar(Char::uppercaseChar) }.split(",").toTypedArray()
    }
}

