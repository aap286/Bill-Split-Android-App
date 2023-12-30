package app.cartcollaborate

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.view.Gravity
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat

class ActivityThree : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_3)

        val payers = intent.getStringArrayExtra("payers")
        val items = intent.getStringArrayExtra("items")
        val prices = intent.getDoubleArrayExtra("prices")

        // var quantities = Array<Int>(100) { 1 }
        val quantities = ArrayList<Int>(List(items?.size ?: 100) { 1 })

        // layout of the table
        val itemTable = findViewById<LinearLayout>(R.id.itemTable)

        if (items != null) {

            for (item in items) {


                // row headings
                val itemName = createItem(item)

                //  layout for increment, decrement and quantity
                val decBtn = ImageView(this)
                decBtn.setImageResource(R.drawable.down)

                // displaying current quantity of the item
                val qtyInput = createQuantityView()

                val incBtn = ImageView(this)
                incBtn.setImageResource(R.drawable.up)
                decBtn.alpha = 0.1f

                incBtn.setOnClickListener {
                    var num: Int? = qtyInput.text.toString().toIntOrNull()

                    if (num != null) {
                        num += 1
                        qtyInput.text = num.toString()

                        // get index of item name
                        val index = items.indexOf(item)

                        // quantities?.set(index, 1 + quantities[index]!!)
                        quantities[index] += 1

                        if (num > 1) {
                            decBtn.alpha = 1.0f
                        }
                    }
                }

                decBtn.setOnClickListener {
                    var num: Int? = qtyInput.text.toString().toIntOrNull()

                    if (num != null && num > 1) {
                        num -= 1
                        qtyInput.text = num.toString()
                        // get index of item name
                        val index = items.indexOf(item)
                        // quantities?.set(index, quantities[index]!! - 1)
                        quantities[index] -= 1
                    }


                    if (num == 1) {
                        decBtn.alpha = 0.1f
                    } else {
                        decBtn.alpha = 1.0f
                    }

                }


                // layout to store all these boxes
                val itemRow = LinearLayout(this)
                itemRow.orientation = LinearLayout.HORIZONTAL
                itemRow.setPadding(
                    16,
                    16,
                    0,
                    16
                )


                // Set layout parameters for the views
                val nameWeight = 0.5f
                val numberWeight = 0.167f

                val nameLayoutParams = LinearLayout.LayoutParams(
                    0, // Set width to 0dp for weight
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                nameLayoutParams.weight = nameWeight
                nameLayoutParams.gravity = Gravity.CENTER_VERTICAL
                itemRow.addView(itemName, nameLayoutParams)

                val numberLayoutParams = LinearLayout.LayoutParams(
                    0, // Set width to 0dp for weight
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                numberLayoutParams.weight = numberWeight
                numberLayoutParams.gravity = Gravity.CENTER_VERTICAL

                itemRow.addView(decBtn, numberLayoutParams)
                itemRow.addView(qtyInput, numberLayoutParams)
                itemRow.addView(incBtn, numberLayoutParams)

                itemTable.addView(itemRow)
            }
        }

        val nextPg = findViewById<Button>(R.id.nextPg)

        nextPg.setOnClickListener {
            val sendInfo = Intent(this, ActivityFour::class.java).apply {
                putIntegerArrayListExtra("quantity", quantities)
                putExtra("payers", payers)
                putExtra("items", items)
                putExtra("prices", prices)

            }
            startActivity(sendInfo)
        }

    }

    // creates text view layout to display item name
    private fun createItem(text: String): TextView {

        val itemName = TextView(this)
        itemName.textSize = 24f
        itemName.setPadding(16, 0, 0, 0)
        itemName.text = text
        itemName.setTextColor(ContextCompat.getColor(this, R.color.secondary))

        return itemName
    }

    // creates a view to display the quantity
    private fun createQuantityView(): TextView {
        val qtyInput = TextView(this)
        qtyInput.isFocusable = false
        qtyInput.isFocusableInTouchMode = false
        qtyInput.inputType = InputType.TYPE_NULL
        qtyInput.gravity = Gravity.CENTER
        qtyInput.textSize = 24f
        qtyInput.text = "1"
        qtyInput.setTextColor(ContextCompat.getColor(this, R.color.secondary))
        return qtyInput
    }


}

