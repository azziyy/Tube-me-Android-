package uz.tubeme.app.data.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

/**
 * Google Sheets GVIZ JSON endpoint:
 *   https://docs.google.com/spreadsheets/d/<SHEET_ID>/gviz/tq?tqx=out:json&gid=<GID>
 *
 * The response is a JSONP-like wrapper:
 *   /*O_o*/
 *   google.visualization.Query.setResponse({...JSON...});
 *
 * We fetch it as plain text and unwrap manually (see SheetsParser).
 */
interface SheetsApi {
    @GET("spreadsheets/d/{sheetId}/gviz/tq")
    suspend fun getSheet(
        @retrofit2.http.Path("sheetId") sheetId: String,
        @Query("tqx") tqx: String = "out:json",
        @Query("gid") gid: String
    ): String
}

object SheetsClient {
    const val SHEET_ID = "1K_dFI07bH2uS9lQ-BI6fGoEtOe_FKJT-wcFbFfKM6PA"
    const val GID_VIDEOS = "0"
    const val GID_COLLECTIONS = "1794713448"
    const val GID_NEWS = "1389725413"

    private val ok: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    val api: SheetsApi = Retrofit.Builder()
        .baseUrl("https://docs.google.com/")
        .client(ok)
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()
        .create(SheetsApi::class.java)
}
