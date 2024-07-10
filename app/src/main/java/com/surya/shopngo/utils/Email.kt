package com.surya.shopngo.utils

import android.util.Log
import com.surya.shopngo.activity.CheckoutActivity
import java.util.Properties
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.Multipart
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart

object Email {
    private val TAG = Email::class.java.simpleName

    lateinit var fromAddress: String
    lateinit var password: String
    lateinit var props: Properties
    lateinit var session: Session
    lateinit var message: Message
    lateinit var messageBodyPart: MimeBodyPart
    lateinit var multipart: Multipart

    init {
        props = Properties()
        fromAddress = "ssuurryyaa101@gmail.com"
        password = "kassxprauoxzqqsc"
    }

    fun sendEmail(toAddress: String) {

        try {
            session = Session.getInstance(props,
                object : Authenticator() {
                    override fun getPasswordAuthentication(): PasswordAuthentication {
                        return PasswordAuthentication(fromAddress, password)
                    }
                })

            message = MimeMessage(session)

            message.setFrom(InternetAddress(fromAddress))
            message.setRecipients(
                Message.RecipientType.TO,
                InternetAddress.parse(toAddress)
            )
            message.setSubject("Order Confirmation")
            message.setText(
                "Thank you for your order!"
            )

            messageBodyPart = MimeBodyPart()
            multipart = MimeMultipart()

            multipart.addBodyPart(messageBodyPart)
            message.setContent(multipart)
            Transport.send(message)

            Log.i(TAG, "Email sent successfully!");
        }catch (e: Exception) {
            Log.i(TAG, "Error in sendEmail!");
        }

    }
}
