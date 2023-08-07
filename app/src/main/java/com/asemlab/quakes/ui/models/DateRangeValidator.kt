package com.asemlab.quakes.ui.models

import android.os.Parcel
import android.os.Parcelable
import androidx.core.util.Pair
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.concurrent.TimeUnit


class DateRangeValidator(var numberOfDays: Int) : CalendarConstraints.DateValidator, Parcelable {

    private var rangePicker: MaterialDatePicker<Pair<Long, Long>>? = null

    fun setDatePicker(rangePicker: MaterialDatePicker<Pair<Long, Long>>?) {
        this.rangePicker = rangePicker
    }

    override fun isValid(date: Long): Boolean {
        val days: Long =
            (numberOfDays - 1) * TimeUnit.DAYS.toMillis(1)
        val selection = rangePicker?.selection as Pair<Long, Long>
        if (date > System.currentTimeMillis()) return false
        if (selection.first != null && selection.second != null) {
            if ((selection.second - selection.first) > numberOfDays)
                return true
        }

        selection.first?.let { startDate ->
            if (date > startDate + days) return false
        }
        return true
    }

    companion object CREATOR : Parcelable.Creator<DateRangeValidator> {
        override fun createFromParcel(parcel: Parcel): DateRangeValidator {
            return DateRangeValidator(parcel)
        }

        override fun newArray(size: Int): Array<DateRangeValidator?> {
            return arrayOfNulls(size)
        }
    }

    constructor(parcel: Parcel) : this(30)

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
    }

}