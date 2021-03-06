package app.tasknearby.yashcreations.com.tasknearby.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android.billingclient.api.Purchase;

import org.joda.time.DateTimeComparator;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import app.tasknearby.yashcreations.com.tasknearby.R;
import app.tasknearby.yashcreations.com.tasknearby.services.FusedLocationService;

/**
 * Contains utility functions used throughout the app.
 *
 * @author vermayash8
 */
public final class AppUtils {

    /**
     * Returns a formatted time string in 12-hour format.
     */
    public static String getReadableTime(LocalTime localTime) {
        int hourOfDay = localTime.getHourOfDay();
        int minute = localTime.getMinuteOfHour();
        String periodSuffix = "AM";
        if (hourOfDay > 12) {
            hourOfDay -= 12;
            periodSuffix = "PM";
        } else if (hourOfDay == 12) {
            periodSuffix = "PM";
        }
        return String.format(Locale.ENGLISH, "%02d:%02d %s", hourOfDay, minute, periodSuffix);
    }

    /**
     * Returns dates as "Today", "Fri, 12 Dec 17" or "Forever" (null) for the corresponding input
     * Date object. Later on, this can be modified to return "Yesterday", "Tomorrow" also.
     */
    public static String getReadableDate(@NonNull Context context, @Nullable Date date) {
        if (date == null) {
            return context.getString(R.string.detail_date_no_deadline);
        } else if (DateTimeComparator.getDateOnlyInstance().compare(date, new Date()) == 0) {
            return context.getString(R.string.detail_date_today);
        } else {
            SimpleDateFormat sdfReadable = new SimpleDateFormat("EEE, d MMM yy", Locale.ENGLISH);
            return sdfReadable.format(date);
        }
    }

    public static String getReadableLocalDate(Context context, LocalDate date) {
        if (date == null) {
            return context.getString(R.string.detail_date_no_deadline);
        } else if (date.compareTo(new LocalDate()) == 0) {
            return context.getString(R.string.detail_date_today);
        } else {
            return date.toString("EEE, d MMM YY");
        }

    }

    /**
     * Compares given dates. Returns -1 if date 1 is smaller than date 2. 0 if equal and 1 if
     * greater.
     */

    public static int compareDate(Date date1, Date date2) {
//        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//        String s1 = sdf.format(date1);
//        String s2 = sdf.format(date2);
//        try {
//            date1 = sdf.parse(s1);
//            date2 = sdf.parse(s2);
//        } catch (ParseException pe) {
//            pe.printStackTrace();
//            throw new IllegalArgumentException("Unable to parse date.");
//        }
//        return date1.compareTo(date2);
        LocalDate ld1 = LocalDate.fromDateFields(date1);
        LocalDate ld2 = LocalDate.fromDateFields(date2);
        return ld1.compareTo(ld2);
    }

    public static boolean isSnoozed(long lastSnoozedTime) {
        return (lastSnoozedTime != -1);
    }

    public static boolean isSnoozedTaskEligible(long lastSnoozedTime, long snoozeTime) {
        return (lastSnoozedTime + snoozeTime <= System.currentTimeMillis());
    }

    public static void sendFeedbackEmail(Context context) {
        Intent mailIntent = new Intent(Intent.ACTION_SENDTO);
        mailIntent.setType("text/plain");
        mailIntent.setData(Uri.parse("mailto:"));
        mailIntent.putExtra(Intent.EXTRA_EMAIL, context.getResources().getStringArray(R.array
                .email_ids));
        mailIntent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.email_subject));
        context.startActivity(mailIntent);
    }

    public static void rateApp(Context context) {
        String packageName = context.getPackageName();
        String appUrl = context.getString(R.string.play_store_base_url) + packageName;
        try {
            Intent rateIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R
                    .string.rating_base_url) +
                    packageName));
            context.startActivity(rateIntent);
        } catch (ActivityNotFoundException e) {
            Intent playStoreIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(appUrl));
            context.startActivity(playStoreIntent);
        }
    }

    public static boolean isPremiumUser(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(context.getString(R.string.pref_is_premium_user_key), false);
    }

    public static void setPremium(Context context, boolean value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = prefs.edit();
        ed.putBoolean(context.getString(R.string.pref_is_premium_user_key), value);
        ed.apply();
    }

    /**
     * Starts {@link FusedLocationService} service.
     */
    public static void startService(Context context) {
        if (Build.VERSION.SDK_INT >= 26) {
            context.startForegroundService(new Intent(context, FusedLocationService.class));
        } else {
            context.startService(new Intent(context, FusedLocationService.class));
        }
    }

    /**
     * Stops {@link FusedLocationService} service.
     */
    public static void stopService(Context context) {
        context.stopService(new Intent(context, FusedLocationService.class));
    }

    /**
     * Saves the order Id and purchase token to shared preferences.
     */
    public static void savePurchaseDetails(Context context, Purchase purchase) {
        SharedPreferences defaultPref = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor ed = defaultPref.edit();
        ed.putString(context.getString(R.string.pref_upgrade_order_id), purchase.getOrderId());
        ed.putString(context.getString(R.string.pref_upgrade_purchase_token), purchase
                .getPurchaseToken());
        ed.apply();
    }

    public static boolean hasUserSeenOnboarding(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(context.getString(R.string.pref_has_user_seen_onboarding), false);
    }
}
