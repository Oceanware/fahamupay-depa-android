package com.fahamutech.fahamupay.business.models

class Message(
    var reference: String? = null,
    var amount: Int? = null,
    var charge: Int? = null,
    var receipts: Array<String> = arrayOf(),
    var msisdn: String? = null,
)

class SendMessageRequest(
    var message: ArrayList<String> = arrayListOf()
)
