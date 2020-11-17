package com.jj.haha.jrouter.api.bean

import android.os.IBinder
import android.os.Parcel
import android.os.Parcelable

class BinderBean(val binder: IBinder, val processName: String?) : Parcelable {

    constructor(`in`: Parcel):this(`in`.readStrongBinder(), `in`.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeStrongBinder(binder)
        parcel.writeString(processName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BinderBean> {
        override fun createFromParcel(parcel: Parcel): BinderBean {
            return BinderBean(parcel)
        }

        override fun newArray(size: Int): Array<BinderBean?> {
            return arrayOfNulls(size)
        }
    }

}