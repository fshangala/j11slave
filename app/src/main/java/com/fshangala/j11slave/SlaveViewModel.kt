package com.fshangala.j11slave

import androidx.lifecycle.MutableLiveData

class SlaveViewModel : Y11ViewModel() {
    var jsResponse = MutableLiveData<String>("")
    var slaveStatus = MutableLiveData<String>("")
    var browserLoading = MutableLiveData<Boolean>(false)
}