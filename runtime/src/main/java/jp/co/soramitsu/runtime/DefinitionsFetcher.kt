package jp.co.soramitsu.runtime

import retrofit2.http.GET
import retrofit2.http.Path

interface DefinitionsFetcher {

    @GET("https://raw.githubusercontent.com/valentunn/py-scale-codec/fearless_stable/scalecodec/type_registry/{fileName}")
    suspend fun getDefinitionsByFile(@Path("fileName") fileName: String): String
}

suspend fun DefinitionsFetcher.getDefinitionsByNetwork(networkName: String): String {
    return getDefinitionsByFile(fileName(networkName))
}

fun fileName(networkName: String) = "$networkName.json"
