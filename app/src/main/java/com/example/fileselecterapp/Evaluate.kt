package com.example.fileselecterapp

// ms
//30KB = 1867               //30KB 2478
//100KB = 5647 4720 4633    //100KB 5600
//200KB = 8400 8401 7960    //200KB 10604
//500KB = 26219             //500KB 17477
//1MB = 46104               //1MB 36979
//2MB = 83013               //2MB 53620
//3MB = 115379              //3MB
//5MB = 216360              //5MB
class Evaluate {

    fun measureTimeMillis(block: () -> Unit): Long {
        val startTime = System.nanoTime()

        // Execute the function block
        block()

        val endTime = System.nanoTime()
        val elapsedTimeNano = endTime - startTime

        // Convert nanoseconds to milliseconds
        return elapsedTimeNano / 1_000_000
    }

}