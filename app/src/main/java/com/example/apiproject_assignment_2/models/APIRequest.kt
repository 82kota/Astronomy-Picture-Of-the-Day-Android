package com.example.apiproject_assignment_2.models

import android.os.Parcel
import android.os.Parcelable

//APOD APIRequest model
class APIRequest (
    var copyright: String,
    var date: String,
    var explanation: String,
    var hdurl: String,
    var media_type: String,
    var service_version: String,
    var title: String,
    var url: String

    ) : Parcelable{ //class is parcelable for passing between activities
        constructor(parcel: Parcel) : this(
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: "",
            parcel.readString() ?: ""
        )
        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(copyright)
            parcel.writeString(date)
            parcel.writeString(explanation)
            parcel.writeString(hdurl)
            parcel.writeString(media_type)
            parcel.writeString(service_version)
            parcel.writeString(title)
            parcel.writeString(url)
        }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<APIRequest> {
        override fun createFromParcel(source: Parcel): APIRequest {
            return APIRequest(source)
        }

        override fun newArray(size: Int): Array<APIRequest?> {
            return arrayOfNulls(size)
        }
    }

    }