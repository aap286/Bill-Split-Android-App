package app.cartcollaborate

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class ActivityTwo : ComponentActivity() {

    private val db =
        Firebase.firestore.collection("Items") // connects to the Items collection directly
    private val groceryDict = mutableMapOf<String, MutableList<String>>() //  empty grocery dictionary

    // selected grocery items
    private val selectedItems = mutableListOf<String>()
    private val selectedPrice = mutableListOf<Double>()


    // categories for all the items
    private val categories = arrayOf(
        "Fresh Produce",
        "Dairy",
        "Grains and Breads",
        "Meat and Proteins",
        "Canned Goods",
        "Baking",
        "Snacks",
        "Spices",
        "Condiments",
        "Frozen Foods",
        "Drinks and Alcohol",
        "Household",
        "Other"
    )
    private val itemTypeColor = arrayOf(
        "#5E7153",   // Produce - Olive Green
        "#776B5D",   // Dairy - Brown
        "#8B5A2B",   // Bread - Warm Brown
        "#A85143",   // Protein - Rusty Red
        "#D4BF4D",   // Canned Goods - Mustard Yellow
        "#F2CDC8",   // Baking - Soft Pink
        "#A7B8C1",   // Snacks - Dusty Blue
        "#C0745D",   // Spices - Terra Cotta
        "#D4B48D",   // Condiments - Muted Mustard
        "#AED8E6",   // Frozen Foods - Icy Blue
        "#65815E",   // Drinks - Vintage Green
        "#AFAFAF",   // Household - Antique Gray
        "#CAB8E6"    // Other - Faded Lavende
    )


    private val numOfItems = categories.size
    private val categoryColors = HashMap<String, String>()


    //    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_2)

        // Array of all payers
        val userInputArray = intent.getStringArrayExtra("userInput")

        // dropdown menu for category
        categoryDropDownMenu()

        // colors for each type category
        categoryColors.categoryCol()

        // add new items
        val addItemBtn = findViewById<Button>(R.id.addItemBtn)

        // update items
        val updateBtn = findViewById<Button>(R.id.updateItemBtn)

        // adding items
        addItemBtn.setOnClickListener {

            val nameOfItemBeforeFilter = findViewById<EditText>(R.id.nameOfItem)
            val priceOfItemBeforeFilter = findViewById<EditText>(R.id.priceOfItem)
            val categoryOfItem: Int = findViewById<Spinner>(R.id.categoryInput).selectedItemPosition

            val nameOfItem = nameOfItemBeforeFilter.text.toString().lowercase().split(" ")
                .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
            val priceOfItem = priceOfItemBeforeFilter.text.toString()


            //   only submit if both fields filled
            if (nameOfItem.isNotBlank() && priceOfItem.isNotBlank()) {

                // store new data to database
                val newItem = hashMapOf(
                    "Item" to nameOfItem,
                    "Price" to priceOfItem.formatToTwoDecimals(),
                    "Category" to categories[categoryOfItem]
                )

                val docRef = db.document(nameOfItem)

                docRef.get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            showAlertDialog(
                                "Duplicate Item",
                                "This item already exists in the list."
                            )

                        } else {
                            // Document does not exist, add it
                            showAlertDialog("Success!", "Your item has been added.")
                            docRef.set(newItem)
                                .addOnSuccessListener {
                                    updateGroceryList()
                                    nameOfItemBeforeFilter.setText("")
                                    priceOfItemBeforeFilter.setText("")
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error adding document", e)
                                }
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error checking document existence", e)
                    }
            } else {
                checkAndShowAlert(this, nameOfItem, priceOfItem)
            }
        }

        // updating item
        updateBtn.setOnClickListener {

            val nameOfItemBeforeFilter = findViewById<EditText>(R.id.nameOfItem)
            val priceOfItemBeforeFilter = findViewById<EditText>(R.id.priceOfItem)

            val nameOfItem = nameOfItemBeforeFilter.text.toString().lowercase().split(" ")
                .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
            val priceOfItem = priceOfItemBeforeFilter.text.toString()

            //   only submit if both fields filled
            if (nameOfItem.isNotBlank() && priceOfItem.isNotBlank()) {

                val docRef = db.document(nameOfItem)

                docRef.get()
                    .addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot.exists()) {
                            db.document(nameOfItem)
                                .update("Price", priceOfItem.formatToTwoDecimals())
                                .addOnSuccessListener {
                                    updateGroceryList()
                                    Log.d(TAG, "DocumentSnapshot updated successfully")
                                    nameOfItemBeforeFilter.setText("")
                                    priceOfItemBeforeFilter.setText("")
                                    showAlertDialog(
                                        "Item Updated   ",
                                        "Your item has been successfully updated."
                                    )
                                }
                                .addOnFailureListener { e ->
                                    Log.w(TAG, "Error updating document", e)
                                }
                        } else {
                            //     when item doesn't exists
                            showAlertDialog(
                                "Item Not Found",
                                "Item not in list. Check name or add as new."
                            )
                        }
                    }
            } else {
                checkAndShowAlert(this, nameOfItem, priceOfItem)
            }
        }

        // update grocery button on start
        updateGroceryList()

        //     next activity
        val selectItemBtn = findViewById<Button>(R.id.submitItems)

        selectItemBtn.setOnClickListener {
            if (selectedItems.isNotEmpty()) {

                val sendPayerItems = Intent(this, ActivityThree::class.java).apply {
                    putExtra("payers", userInputArray)
                    putExtra("items", selectedItems.toTypedArray())
                    putExtra("prices", selectedPrice.toDoubleArray())
                }
                startActivity(sendPayerItems)
            } else {
                showAlertDialog("Missing Selection", "Please select items before proceeding.")
            }
        }


    }

    private fun HashMap<String, String>.categoryCol(): HashMap<String, String> {
        for (i in 0 until numOfItems) {
            this[categories[i]] = itemTypeColor[i]
        }

        return this
    }

    // alert message if user forget to add details
    private fun showAlertForMissingDetails(
        context: Context,
        isNameMissing: Boolean,
        isPriceMissing: Boolean
    ) {
        val title = "Incomplete Information"

        val message = when {
            isNameMissing && isPriceMissing -> "Please enter the name and price of the item."
            isNameMissing -> "Please enter the name of the item."
            isPriceMissing -> "Please enter the price of the item."
            else -> "Great! You've provided all the necessary details. Thanks a bunch!"
        }

        showAlertDialog(title, message)
    }

    private fun checkAndShowAlert(context: Context, nameOfItem: String, priceOfItem: String) {
        when {
            nameOfItem.isBlank() && priceOfItem.isBlank() -> showAlertForMissingDetails(
                context,
                true,
                true
            )

            nameOfItem.isBlank() -> showAlertForMissingDetails(context, true, false)
            priceOfItem.isBlank() -> showAlertForMissingDetails(context, false, true)
            else -> {
                // Both name and price are provided, no alert needed
            }
        }
    }

    // updates grocery list and buttons
    private fun updateGroceryList() {

        db.get()
            .addOnSuccessListener { result ->
                for (doc in result) {
                    val item = mutableListOf(
                        doc.data["Item"] as String,
                        doc.data["Category"] as String,
                        doc.data["Price"].toString()
                    )

                    groceryDict[doc.id] = item
                }

                //  layout for storing the buttons
                val groceryBtnLayout = findViewById<GridLayout>(R.id.groceryBtnLayout)

                // Clear existing views from the GridLayout
                groceryBtnLayout.removeAllViews()

                // setting text inside the button
                for ((_, values) in groceryDict) {

                    // string for the button
                    val formattedString = formatButtonString(values[0], values[2], 8)

                    // creates button
                    val itemBtn = createItemBtn(this, formattedString, values, groceryBtnLayout)

                    // add button to the layout
                    groceryBtnLayout.addView(itemBtn)

                }
            }

            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents.", exception)
            }
    }


    // initiates the category dropdown menu
    private fun categoryDropDownMenu() {
        // setting up dropdown menu for categories
        val spinner: Spinner = findViewById(R.id.categoryInput)
        val adapter = ArrayAdapter(this, R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

    }


    // alert message display
    private fun showAlertDialog(title: String, msg: String) {
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

        // Delay the dismissal of the dialog after 500 milliseconds
        Handler().postDelayed({ alertDialog.dismiss() }, 1700)
    }

    // creating button to display item name and price
    private fun createItemBtn(
        context: Context,
        text: String,
        values: MutableList<String>,
        layout: GridLayout
    ): Button {

        val itemBtn = Button(context)
        itemBtn.text = text
        itemBtn.isAllCaps = false
        itemBtn.textSize = 16f
        itemBtn.setTextColor(ContextCompat.getColor(context, R.color.primary))
        itemBtn.gravity = Gravity.CENTER
        itemBtn.background.setColorFilter(
            Color.parseColor(categoryColors[values[1]]),
            android.graphics.PorterDuff.Mode.SRC
        )
        itemBtn.layoutParams = LinearLayout.LayoutParams(
            310,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Set onClickListener
        itemBtn.setOnClickListener {
            values[0].let { item ->
                if (item !in selectedItems) {
                    selectedItems.add(item)
                    selectedPrice.add(values[2].toDouble())
                    itemBtn.alpha = 0.5f
                } else {
                    val index = selectedItems.indexOf(item)
                    selectedItems.removeAt(index)
                    selectedPrice.removeAt(index)
                    itemBtn.alpha = 1.0f
                }
            }
        }

        // set onLongClick
        itemBtn.setOnLongClickListener {

            db.document(values[0]).delete()
                .addOnSuccessListener {
                    layout.removeView(itemBtn)
                }
                .addOnFailureListener { e ->
                    println("Error deleting document: $e")
                }

            true
        }
        return itemBtn
    }
}


// rounds double to 2 decimal places
fun String.formatToTwoDecimals(): Double {
    return String.format("%.2f", toDouble()).toDouble()
}

// formats the string for the button
fun formatButtonString(name: String, price: String, n: Int): String {

    val newString = if (name.length > n) {
        val firstPart = name.substring(0, n - 3)
        val secondPart = name.substring(n - 3, name.length)
        "$firstPart\n$secondPart"
    } else {
        name
    }

    return newString + "\n" + price

}





