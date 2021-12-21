package at.fh.hgb.mc.bazramicroproject.interfaces

//Interface for returning data from network requests
interface VolleyCallback {
    fun onSuccess(result: IAPIResponse)
}