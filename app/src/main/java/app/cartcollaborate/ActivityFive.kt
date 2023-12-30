package app.cartcollaborate

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat

class ActivityFive : ComponentActivity() {
    @SuppressLint("MissingInflatedId", "SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_5)

        val payers = requireNotNull(intent.getStringArrayExtra("payers"))
        val prices = requireNotNull(intent.getDoubleArrayExtra("prices"))

        val n = prices.size // size of array
        val totalCost = String.format("%.2f", prices.sum()) // total cost of the bill

        // text view to display cost
        val totalCostView = findViewById<TextView>(R.id.totalPriceText)
        totalCostView.text = totalCost
        totalCostView.textSize = 24f
        totalCostView.setTextColor(ContextCompat.getColor(this, R.color.secondary))

        // texts view instance
        val nameView = findViewById<TextView>(R.id.nameOfPayer)
        val row = findViewById<LinearLayout>(R.id.paymentRow)
        val table = findViewById<LinearLayout>(R.id.costTable)
        val shareItbtn = findViewById<Button>(R.id.shareItBtn)

        // looping for each payer and corresponding price
        for(i in 0 until n){

            // creating a text view for the name of the payer
            val name = TextView(this)
            name.text = payers[i]
            name.textSize = 24f
            name.setTextColor(ContextCompat.getColor(this, R.color.secondary))
            name.layoutParams = nameView.layoutParams

            // creating a text view for the price for payer
            val price = TextView(this)
            price.text = String.format("%.2f", prices[i])
            price.textSize = 24f
            price.setTextColor(ContextCompat.getColor(this, R.color.secondary))
            price.layoutParams = nameView.layoutParams

            // text that is shared
            val text = "This is your bill ${payers[i]}: ${String.format("%.2f", prices[i])}"

            // share it button
            val btn = ImageView(this)
            btn.setImageResource(R.drawable.share_it_icon)
            btn.layoutParams = shareItbtn.layoutParams

            // when button is clicked
            btn.setOnClickListener {

                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, text)
                    type = "text/plain"
                    // Package name of WhatsApp to directly share to WhatsApp
                    // `package` = "com.whatsapp"
                }

                startActivity(Intent.createChooser(shareIntent, "Share with"))
            }

            // whatsapp button
            val whatsappBtn = ImageView(this)
            whatsappBtn.setImageResource(R.drawable.whatsapp)
            whatsappBtn.layoutParams = shareItbtn.layoutParams

            // when button is clicked
            whatsappBtn.setOnClickListener {

                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, text)
                    type = "text/plain"
                    // Package name of WhatsApp to directly share to WhatsApp
                    `package` = "com.whatsapp"
                }

                startActivity(Intent.createChooser(shareIntent, "Share with"))
            }




            // replicate row layout
            val rowTemp = LinearLayout(this)
            rowTemp.orientation = row.orientation
            rowTemp.layoutParams = row.layoutParams

            // adding layouts to view to display on screen
            rowTemp.addView(name)
            rowTemp.addView(price)
            rowTemp.addView(btn)
            rowTemp.addView(whatsappBtn)

            table.addView(rowTemp)

        }
    }
}

