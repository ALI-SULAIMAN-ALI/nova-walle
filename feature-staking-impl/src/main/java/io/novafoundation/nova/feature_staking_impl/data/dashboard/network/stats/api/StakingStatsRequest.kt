package io.novafoundation.nova.feature_staking_impl.data.dashboard.network.stats.api

import io.novafoundation.nova.common.data.network.subquery.SubQueryFilters
import io.novafoundation.nova.common.data.network.subquery.SubqueryExpressions.and
import io.novafoundation.nova.common.data.network.subquery.SubqueryExpressions.anyOf
import io.novafoundation.nova.feature_staking_api.domain.dashboard.model.StakingOptionId
import io.novafoundation.nova.runtime.ext.addressOf
import io.novafoundation.nova.runtime.ext.supportedStakingOptions
import io.novafoundation.nova.runtime.ext.utilityAsset
import io.novafoundation.nova.runtime.multiNetwork.chain.mappers.mapStakingTypeToStakingString
import io.novafoundation.nova.runtime.multiNetwork.chain.model.Chain
import io.novafoundation.nova.runtime.multiNetwork.chain.model.ChainId
import jp.co.soramitsu.fearless_utils.extensions.requireHexPrefix
import jp.co.soramitsu.fearless_utils.runtime.AccountId

class StakingStatsRequest(stakingAccounts: Map<StakingOptionId, AccountId?>, chains: List<Chain>) {

    @Transient
    private val chainAddressesFilter = constructChainAddressesFilter(stakingAccounts, chains)

    val query = """
    {
        activeStakers(
            filter: { $chainAddressesFilter }
        ) {
            nodes {
                networkId
                stakingType
                address
            }
        }
        
        stakingApies {
            nodes {
                networkId
                stakingType
                maxAPY
            }
        }
        
        accumulatedRewards(
            filter: { $chainAddressesFilter }
        ) {
            nodes {
                networkId
                stakingType
                amount
            }
        }
    }

    """.trimIndent()

    private fun constructChainAddressesFilter(
        stakingAccounts: Map<StakingOptionId, AccountId?>,
        chains: List<Chain>
    ): String = with(SubQueryFilters) {
        val perChain = chains.mapNotNull { chain ->
            val hasTypeAndAddressOptions = hasTypeAndAddressOptions(chain, stakingAccounts)

            if (hasTypeAndAddressOptions.isEmpty()) return@mapNotNull null

            hasNetwork(chain.id.requireHexPrefix()) and anyOf(hasTypeAndAddressOptions)
        }

        return anyOf(perChain)
    }

    private fun SubQueryFilters.Companion.hasTypeAndAddressOptions(
        chain: Chain,
        stakingAccounts: Map<StakingOptionId, AccountId?>
    ): List<String> {
        val utilityAsset = chain.utilityAsset

        return utilityAsset.supportedStakingOptions().mapNotNull { stakingType ->
            val stakingOptionId = StakingOptionId(chain.id, utilityAsset.id, stakingType)
            val accountId = stakingAccounts[stakingOptionId] ?: return@mapNotNull null
            val stakingTypeString = mapStakingTypeToStakingString(stakingType) ?: return@mapNotNull null

            hasAddress(chain.addressOf(accountId)) and hasStakingType(stakingTypeString)
        }
    }

    private fun SubQueryFilters.hasNetwork(chainId: ChainId): String {
        return "networkId" equalTo chainId
    }

    private fun SubQueryFilters.hasStakingType(stakingType: String): String {
        return "stakingType" equalTo stakingType
    }

    private fun SubQueryFilters.hasAddress(address: String): String {
        return "address" equalTo address
    }
}
