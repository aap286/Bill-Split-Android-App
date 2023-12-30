package app.cartcollaborate

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat

class ActivityFour : ComponentActivity() {
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_4)

        val payers = intent.getStringArrayExtra("payers")
        val items = intent.getStringArrayExtra("items")
        val prices = intent.getDoubleArrayExtra("prices")
        val quantity = intent.getIntegerArrayListExtra("quantity")

        if( payers != null && items != null && prices != null && quantity != null){
            // store information how many times an item is split
            // default value 0 before being checked
            // index of elements is according to the items selected
            val itemSplitsArr = ArrayList<Int>(List(items.size) { 0 })
            Log.d("YourTag", "itemSplitsArr: ${itemSplitsArr.joinToString()}")

            // store what items which user is paying for
            val userPaymentSelection = mutableMapOf<String, MutableList<String>>()

            // store total price for eac user by Index
            val userTotalPrice = ArrayList<Double>(List(payers.size) { 0.0 })


            // pointer for the table of the view
            val table = findViewById<LinearLayout>(R.id.tbl)
            table.gravity = Gravity.CENTER

            // calling table header from the xml
            val itemName = findViewById<TextView>(R.id.tbl_header)
            itemName.addingItemName(this)

            table.removeView(itemName)
            table.addView(itemName)



            for (name in payers) {
                val payer = addPayer(name.toString(), this, itemName)
                table.addView(payer)
                userPaymentSelection[name] = mutableListOf()

            }


            // reference point
            val tblCell = findViewById<TextView>(R.id.tbl_cell)
            val tblRow = findViewById<LinearLayout>(R.id.tbl_row)
            val tblBodyLayout = findViewById<LinearLayout>(R.id.tbl_body_layout)
            val myCheckBox = findViewById<CheckBox>(R.id.myCheckBox)

            for (item in items) {

                // create a temporary text view to hold the name
                val nameOfItem = TextView(this)

                nameOfItem.text = formatItemName(item, 10)
                nameOfItem.setTextColor(ContextCompat.getColor(this, R.color.secondary))

                // setting layout params
                nameOfItem.textSize = 24f
                nameOfItem.layoutParams = tblCell.layoutParams

                // temporary linear layout
                val row = LinearLayout(this)
                row.layoutParams = tblRow.layoutParams

                row.addView(nameOfItem)

                // iteration per user to get n check boxes
                for (name in payers) {

                    // creating a temporary checkbox
                    val btn = CheckBox(this)

                    // setting params for checkbox
                    btn.isChecked = false
                    btn.layoutParams = myCheckBox.layoutParams
                    btn.buttonTintList = myCheckBox.buttonTintList
                    btn.textSize = 32f
                    btn.gravity = Gravity.CENTER
                    btn.setTextColor(ContextCompat.getColor(this, R.color.secondary))

                    // action when button is clicked
                    btn.setOnClickListener {

                        // when button is clicked
                        if (!btn.isChecked) {

                            val index = items.indexOf(item)
                            itemSplitsArr[index] -= 1
                            btn.isChecked = false
                            userPaymentSelection[name]?.remove(item)
                        }
                        // when button is unchecked
                        else {
                            // index of the item
                            val index = items.indexOf(item)
                            itemSplitsArr[index] += 1
                            btn.isChecked = true
                            userPaymentSelection[name]?.add(item)

                        }
                    }

                    // adding check box to the row
                    row.addView(btn)

                }
                tblBodyLayout.addView(row)
            }

            // button to next page to split bill
            val nextBtn = findViewById<Button>(R.id.splitBill)
            nextBtn.setOnClickListener {

                // iterating through each user
                for ((key, values) in userPaymentSelection) {


                    // find total price for each user
                    var totalPrice = 0.00
                    for (value in values) {
                        if (items.contains(value)
                        ) {
                            val index = items.indexOf(value)
                            totalPrice += prices[index] * quantity[index] / itemSplitsArr[index]
                            Log.d("Message", "$totalPrice")
                        }
                    }

                    val nameIndex = payers.indexOf(key)
                    userTotalPrice[nameIndex] = totalPrice

                }

                // goes to next activity
                val sendInfo = Intent(this, ActivityFive::class.java).apply {

                    putExtra("payers", payers)
                    putExtra("prices", userTotalPrice.toDoubleArray())

                }
                startActivity(sendInfo)

            }
        } else {
            Log.d("Error Copying", "Error sending data from previous page")
        }
    }

}

// formats the string of the name of the item
fun formatItemName(name:String,  n: Int): String {

    if (name.length > n) {
        val firstPart = name.substring(0, n - 3)
        val secondPart = name.substring(n - 3)
        return "$firstPart-\n$secondPart"
    }

    return name
}

// adding item name
@SuppressLint("SetTextI18n")
fun TextView.addingItemName(context: Context) {
    this.text = "Item Name"
    this.textSize = 24f
    this.setTextColor(ContextCompat.getColor(context, R.color.secondary))
}

// add payers name
fun addPayer(name:String, context: Context, layout: TextView): TextView {
    val payer = TextView(context)
    payer.text = formatItemName(name, 8)
    payer.textSize = 24f
    payer.layoutParams = layout.layoutParams
    payer.setTextColor(ContextCompat.getColor(context, R.color.secondary))
    payer.gravity = Gravity.CENTER

    return payer
}


