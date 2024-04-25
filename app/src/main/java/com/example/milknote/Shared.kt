package com.example.milknote

class Shared {
    companion object{
        var database: Database? = null
        var customers: ArrayList<Customer>? = null
        var records: Array<Pair<String, Double>>? = null
    }
}