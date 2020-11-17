package com.jj.haha.jrouter.api.lib

import android.os.IBinder
import android.os.Parcel
import android.os.Parcelable

class BinderWrapper : Parcelable {
    val binder: IBinder

    constructor(binder: IBinder) {
        this.binder = binder
    }

    constructor(`in`: Parcel) {
        binder = `in`.readStrongBinder()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeStrongBinder(binder)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BinderWrapper> {
        override fun createFromParcel(parcel: Parcel): BinderWrapper {
            return BinderWrapper(parcel)
        }

        override fun newArray(size: Int): Array<BinderWrapper?> {
            return arrayOfNulls(size)
        }
    }

}