package at.fh.hgb.mc.bazramicroproject.interfaces

interface IAPIResponse {
    val success: Boolean
    val deck_id: String
    fun fromJSON(s: String):IAPIResponse
}