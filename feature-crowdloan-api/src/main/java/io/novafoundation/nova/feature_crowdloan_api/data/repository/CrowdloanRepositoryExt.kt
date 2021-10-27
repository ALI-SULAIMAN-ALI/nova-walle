package io.novafoundation.nova.feature_crowdloan_api.data.repository

import io.novafoundation.nova.feature_crowdloan_api.data.network.blockhain.binding.Contribution
import io.novafoundation.nova.feature_crowdloan_api.data.network.blockhain.binding.FundInfo
import io.novafoundation.nova.feature_crowdloan_api.data.network.blockhain.binding.ParaId
import io.novafoundation.nova.feature_crowdloan_api.data.network.blockhain.binding.TrieIndex
import io.novafoundation.nova.runtime.multiNetwork.chain.model.ChainId
import jp.co.soramitsu.fearless_utils.runtime.AccountId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

suspend fun CrowdloanRepository.getContributions(
    chainId: ChainId,
    accountId: AccountId,
    keys: Map<ParaId, TrieIndex>
): Map<ParaId, Contribution?> = withContext(Dispatchers.Default) {
    keys.map { (paraId, trieIndex) ->
        async { paraId to getContribution(chainId, accountId, paraId, trieIndex) }
    }
        .awaitAll()
        .toMap()
}

suspend fun CrowdloanRepository.hasWonAuction(chainId: ChainId, fundInfo: FundInfo): Boolean {
    val paraId = fundInfo.paraId

    return getWinnerInfo(chainId, mapOf(paraId to fundInfo)).getValue(paraId)
}
