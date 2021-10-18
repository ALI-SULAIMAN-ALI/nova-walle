package jp.co.soramitsu.feature_account_api.domain.interfaces

import jp.co.soramitsu.common.utils.requireValue
import jp.co.soramitsu.core.model.Node
import jp.co.soramitsu.fearless_utils.extensions.toHexString
import jp.co.soramitsu.fearless_utils.runtime.AccountId

private val DEFAULT_NETWORK_TYPE = Node.NetworkType.KUSAMA

suspend fun AccountRepository.currentNetworkType() = runCatching { getSelectedAccount().network.type }
    .recover { DEFAULT_NETWORK_TYPE }
    .requireValue()

// suspend fun AccountRepository.signWithAccount(account: Account, message: ByteArray) = withContext(Dispatchers.Default) {
//    val securitySource = getSecuritySource(account.address)
//
//    val encryptionType = mapCryptoTypeToEncryption(account.cryptoType)
//
//    Signer.sign(encryptionType, message, securitySource.keypair, ).signature
// }

suspend fun AccountRepository.findMetaAccountOrThrow(accountId: AccountId) = findMetaAccount(accountId)
    ?: error("No meta account found for accountId: ${accountId.toHexString()}")
