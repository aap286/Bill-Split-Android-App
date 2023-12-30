package app.cartcollaborate

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.ComponentActivity


class ActivityOne : ComponentActivity() {
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_1)

        val handler = Handler()
        // reference to the submit name button
        val submitNamesBtn = findViewById<Button>(R.id.SubmitNamesBtn)

        // action on clicking the button
        submitNamesBtn.setOnClickListener{

            // stores the user's input
            val userInput = findViewById<EditText>(R.id.userInput)

            // if input is not empty
            if(!userInput.text.isNullOrBlank()) {

                val payerNamesArray: Array<String> = userInput.text.toString().split(",").map { s ->
                    s.trim().capitalize()
                }.filter { it.isNotBlank() }.toTypedArray()


                val sendPayerNames = Intent(this, ActivityTwo::class.java).apply {
                    putExtra("userInput", payerNamesArray)
                }


                startActivity(sendPayerNames)

                // clearing input text
                handler.postDelayed({
                    userInput.setText("")
                }, 500)
            }

            // if input is empty
            else {
                showDialog("Missing Names", "Please enter names.")
            }
        }

    }

    private fun showDialog(title: String, msg: String) {
        // Inflate the custom layout
        val view = layoutInflater.inflate(R.layout.dialogue_box, null)

        // Set the title and message in the custom layout
        view.findViewById<TextView>(R.id.dialogueTitle).text = title
        view.findViewById<TextView>(R.id.dialogueMsg).text = msg

        // Create AlertDialog with the custom layout
        val builder = AlertDialog.Builder(this)
        builder.setView(view)

        // Create and show the AlertDialog
        val alertDialog = builder.create()
        alertDialog.show()

        // // Delay the dismissal of the dialog after 500 milliseconds
        Handler().postDelayed({ alertDialog.dismiss() }, 1700)
    }
}

