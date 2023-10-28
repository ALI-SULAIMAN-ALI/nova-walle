package io.novafoundation.nova.feature_swap_impl.domain.validation

import io.novafoundation.nova.feature_account_api.data.model.Fee
import io.novafoundation.nova.feature_wallet_api.domain.validation.FeeChangeDetectedFailure
import io.novafoundation.nova.feature_wallet_api.domain.validation.NotEnoughToPayFeesError
import io.novafoundation.nova.runtime.multiNetwork.chain.model.Chain
import java.math.BigDecimal
import java.math.BigInteger

sealed class SwapValidationFailure {

    class FeeChangeDetected(override val payload: FeeChangeDetectedFailure.Payload) : SwapValidationFailure(), FeeChangeDetectedFailure

    object NonPositiveAmount : SwapValidationFailure()

    object InvalidSlippage : SwapValidationFailure()

    class NewRateExceededSlippage(
        val assetIn: Chain.Asset,
        val assetOut: Chain.Asset,
        val selectedRate: BigDecimal,
        val newRate: BigDecimal
    ) : SwapValidationFailure()

    object NotEnoughLiquidity : SwapValidationFailure()

    sealed class NotEnoughFunds : SwapValidationFailure() {

        object InUsedAsset : NotEnoughFunds()

        class InCommissionAsset(
            override val chainAsset: Chain.Asset,
            override val availableToPayFees: BigDecimal,
            override val fee: BigDecimal
        ) : NotEnoughFunds(), NotEnoughToPayFeesError
    }

    class AmountOutIsTooLowToStayAboveED(
        val asset: Chain.Asset,
        val amount: BigDecimal,
        val existentialDeposit: BigDecimal
    ) : SwapValidationFailure()

    sealed class InsufficientBalance : SwapValidationFailure() {

        class NoNeedsToBuyMainAssetED(
            val assetIn: Chain.Asset,
            val feeAsset: Chain.Asset,
            val maxSwapAmount: BigInteger,
            val fee: Fee
        ) : SwapValidationFailure()

        class NeedsToBuyMainAssetED(
            val feeAsset: Chain.Asset,
            val assetIn: Chain.Asset,
            val toBuyAmountToKeepEDInCommissionAsset: BigInteger,
            val toSellAmountToKeepEDUsingAssetIn: BigInteger,
            val maxSwapAmount: BigInteger,
            val fee: Fee
        ) : SwapValidationFailure()
    }

    sealed class TooSmallRemainingBalance : SwapValidationFailure() {

        class NoNeedsToBuyMainAssetED(
            val assetIn: Chain.Asset,
            val remainingBalance: BigInteger,
            val assetInExistentialDeposit: BigInteger
        ) : SwapValidationFailure()

        class NeedsToBuyMainAssetED(
            val feeAsset: Chain.Asset,
            val assetIn: Chain.Asset,
            val assetInExistentialDeposit: BigInteger,
            val toBuyAmountToKeepEDInCommissionAsset: BigInteger,
            val toSellAmountToKeepEDUsingAssetIn: BigInteger,
            val remainingBalance: BigInteger,
            val fee: Fee
        ) : SwapValidationFailure()
    }
}
