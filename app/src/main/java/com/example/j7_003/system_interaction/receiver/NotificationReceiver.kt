package com.example.j7_003.system_interaction.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.j7_003.R
import com.example.j7_003.data.sleepreminder.SleepReminder
import com.example.j7_003.system_interaction.handler.NotificationHandler
import com.example.j7_003.data.birthdaylist.Birthday
import com.example.j7_003.data.birthdaylist.BirthdayList
import com.example.j7_003.system_interaction.handler.Logger
import org.threeten.bp.LocalDate
import kotlin.collections.ArrayList


class NotificationReceiver : BroadcastReceiver() {
    private lateinit var context: Context
    private val localDate = LocalDate.now()

    override fun onReceive(context: Context, intent: Intent) {
        this.context = context
        val logger = Logger(context)
        logger.log("BroadcastReceiver", "Notification broadcast received")

        logger.log("BroadcastReceiver", "Received content: ${intent.extras?.get("Notification")}")
        when (intent.extras?.get("Notification")) {
            "Birthday" -> birthdayNotifications()
            "SReminder" -> checkSleepNotification(intent)
        }
    }

    private fun checkSleepNotification(intent: Intent) {
        SleepReminder().reminder[intent.extras?.get("weekday")]?.updateAlarm(
            intent.extras?.getInt("requestCode")!!
        )
        sRNotification()
    }

    private fun sRNotification() {
        NotificationHandler.createNotification(
            "Sleep Reminder",
            "Sleep Reminder Notification",
            200,
            "Sleep Time",
            "It's time to go to bed, sleep well!",
            R.drawable.ic_action_sleepreminder,
            "SReminder",
            context
        )
    }

    private fun birthdayNotifications() {
        val birthdayList = BirthdayList()

        if (birthdayList.isEmpty()) {
            return
        }

        val notifiableUpcomingBirthdays = getUpcomingBirthdays(birthdayList)
        val notifiableCurrentBirthdays = getCurrentBirthdays(birthdayList)

        if (notifiableCurrentBirthdays.size > 1) {
            notifyCurrentBirthdays(notifiableCurrentBirthdays.size)
        } else if (notifiableCurrentBirthdays.size == 1) {
            notifyBirthdayNow(notifiableCurrentBirthdays[0])
        }

        if (notifiableUpcomingBirthdays.size > 1) {
            notifyUpcomingBirthdays(notifiableUpcomingBirthdays.size)
        } else if (notifiableUpcomingBirthdays.size == 1) {
            notifyUpcomingBirthday(notifiableUpcomingBirthdays[0])
        }
    }

    private fun getUpcomingBirthdays(birthdayList: BirthdayList): ArrayList<Birthday> {
        val upcomingBirthdays = ArrayList<Birthday>()
        birthdayList.forEach { n ->
            if (n.month == localDate.monthValue && (n.day - n.daysToRemind) ==
                localDate.dayOfMonth && n.daysToRemind > 0)
            {
                upcomingBirthdays.add(n)
            }
        }
        return upcomingBirthdays
    }

    private fun getCurrentBirthdays(birthdayList: BirthdayList): ArrayList<Birthday> {
        val currentBirthdays = ArrayList<Birthday>()
        birthdayList.forEach { n ->
            if (n.month == localDate.monthValue &&
                n.day == localDate.dayOfMonth &&
                n.daysToRemind == 0
            ) {
                currentBirthdays.add(n)
            }
        }
        return currentBirthdays
    }

    private fun notifyBirthdayNow(birthday: Birthday) {
        NotificationHandler.createNotification(
            "Birthday Notification",
            "Birthdays",
            100,
            "Birthday",
            "It's ${birthday.name}s birthday!",
            R.drawable.ic_action_birthday,
            "birthdays",
            context
        )
    }

    private fun notifyCurrentBirthdays(currentBirthdays: Int) {
        NotificationHandler.createNotification(
            "Birthday Notification",
            "Birthdays",
            102,
            "Birthdays",
            "There are $currentBirthdays birthdays today!",
            R.drawable.ic_action_birthday,
            "birthdays",
            context
        )
    }

    private fun notifyUpcomingBirthday(birthday: Birthday) {
        NotificationHandler.createNotification(
            "Birthday Notification",
            "Upcoming Birthdays",
            101,
            "Upcoming Birthday",
            "${birthday.name}s birthday is coming up in ${birthday.daysToRemind} ${if(birthday.daysToRemind ==1 ) {"day"} else {"days"}}!",
            R.drawable.ic_action_birthday,
            "birthdays",
            context
        )
    }

    private fun notifyUpcomingBirthdays(upcomingBirthdays: Int) {
        NotificationHandler.createNotification(
            "Birthday Notification",
            "Upcoming Birthdays",
            103,
            "Upcoming Birthdays",
            "$upcomingBirthdays birthdays are coming up!",
            R.drawable.ic_action_birthday,
            "birthdays",
            context
        )
    }


}