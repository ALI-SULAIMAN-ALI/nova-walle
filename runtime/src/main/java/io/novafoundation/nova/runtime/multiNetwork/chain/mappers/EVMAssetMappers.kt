package io.novafoundation.nova.runtime.multiNetwork.chain.mappers

import com.google.gson.Gson
import io.novafoundation.nova.common.utils.ethereumAddressToAccountId
import io.novafoundation.nova.core_db.model.chain.AssetSourceLocal
import io.novafoundation.nova.core_db.model.chain.ChainAssetLocal
import io.novafoundation.nova.runtime.multiNetwork.asset.remote.model.EVMAssetRemote
import io.novafoundation.nova.runtime.multiNetwork.chain.model.Chain

fun mapEVMAssetRemoteToLocalAssets(evmAssetRemote: EVMAssetRemote, gson: Gson): List<ChainAssetLocal> {
    return evmAssetRemote.instances.map {
        val assetId = it.contractAddress.ethereumAddressToAccountId()
            .contentHashCode()

        val domainType = Chain.Asset.Type.Evm(it.contractAddress)
        val (type, typeExtras) = mapChainAssetTypeToRaw(domainType)

        ChainAssetLocal(
            id = assetId,
            chainId = it.chainId,
            symbol = evmAssetRemote.symbol,
            name = evmAssetRemote.name,
            precision = evmAssetRemote.precision,
            priceId = evmAssetRemote.priceId,
            icon = evmAssetRemote.icon,
            staking = mapStakingTypeToLocal(Chain.Asset.StakingType.UNSUPPORTED),
            source = AssetSourceLocal.ERC20,
            type = type,
            buyProviders = null,
            typeExtras = gson.toJson(typeExtras),
            enabled = true
        )
    }
}
