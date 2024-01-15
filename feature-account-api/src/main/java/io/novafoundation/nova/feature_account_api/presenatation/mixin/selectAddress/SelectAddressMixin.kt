package io.novafoundation.nova.feature_account_api.presenatation.mixin.selectAddress

import androidx.core.view.isInvisible
import io.novafoundation.nova.common.base.BaseFragment
import io.novafoundation.nova.common.utils.Filter
import io.novafoundation.nova.common.view.YourWalletsView
import io.novafoundation.nova.feature_account_api.domain.model.MetaAccount
import io.novafoundation.nova.runtime.multiNetwork.chain.model.Chain
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

interface SelectAddressMixin {

    class Payload(val chain: Chain, val filter: Filter<MetaAccount>)

    interface Factory {

        fun create(
            coroutineScope: CoroutineScope,
            payloadFlow: Flow<SelectAddressMixin.Payload>,
            onAddressSelect: (String) -> Unit
        ): SelectAddressMixin
    }

    val isSelectAddressAvailableFlow: Flow<Boolean>

    suspend fun openSelectAddress(selectedAddress: String?)
}

fun BaseFragment<*>.setupYourWalletsBtn(view: YourWalletsView, selectAddressMixin: SelectAddressMixin) {
    selectAddressMixin.isSelectAddressAvailableFlow.observe {
        view.isInvisible = !it
    }
}
