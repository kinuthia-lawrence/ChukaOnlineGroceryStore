package com.example.chukaonlinegrocerystore.utils

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import kotlin.random.Random as KotlinRandom

/**
 * Standalone utility to generate dummy sales data for testing.
 * Can be executed directly from IDE or via command line.
 */
class DataGenerator {
    companion object {
        private const val SELLER_ID = "M9D26AAUz9fcWDX6XYUxFGNebX52"

        // Main method for direct execution
        @JvmStatic
        fun main(args: Array<String>) {
            println("Starting sales data generation...")
            insertDummySalesData()
            println("Data generation process initiated!")

            // Keep process alive until Firestore operations complete
            Thread.sleep(5000)
        }

        fun insertDummySalesData() {
            val firestore = FirebaseFirestore.getInstance()
            val salesCollection = firestore.collection("sellers").document(SELLER_ID).collection("sales")

            // Sample product data
            val products = listOf(
                Triple("Apples", 85.0, "Product1"),
                Triple("Bananas", 65.0, "Product2"),
                Triple("Oranges", 120.0, "Product3"),
                Triple("Milk", 150.0, "Product4"),
                Triple("Bread", 95.0, "Product5"),
                Triple("Tomatoes", 75.0, "Product6"),
                Triple("Potatoes", 150.0, "Product7"),
                Triple("Spinach", 45.0, "Product8")
            )

            // Phone numbers for buyers
            val phoneNumbers = listOf("0712345678", "0723456789", "0734567890", "0745678901")

            // Generate sales over the past 60 days
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -60) // Start 60 days ago

            val batch = firestore.batch()
            var count = 0

            // Create 100 random sales spread over 60 days
            repeat(100) {
                // Random product
                val (productName, price, productId) = products.random()

                // Random date within the past 60 days
                calendar.add(Calendar.DAY_OF_YEAR, KotlinRandom.nextInt(0, 3))
                calendar.add(Calendar.HOUR_OF_DAY, KotlinRandom.nextInt(8, 20))
                calendar.add(Calendar.MINUTE, KotlinRandom.nextInt(0, 60))

                val timestamp = Timestamp(calendar.time)
                val quantity = KotlinRandom.nextInt(1, 6)
                val totalAmount = price * quantity
                val buyerPhone = phoneNumbers.random()

                val sale = hashMapOf(
                    "productId" to productId,
                    "productName" to productName,
                    "quantity" to quantity,
                    "price" to price,
                    "totalAmount" to totalAmount,
                    "buyerPhone" to buyerPhone,
                    "timestamp" to timestamp,
                    "sellerId" to SELLER_ID
                )

                val document = salesCollection.document()
                batch.set(document, sale)

                count++

                // Commit in batches of 20
                if (count % 20 == 0) {
                    batch.commit()
                }
            }

            // Commit any remaining documents
            batch.commit()

            println("Generated 100 sample sales records.")
        }
    }
}