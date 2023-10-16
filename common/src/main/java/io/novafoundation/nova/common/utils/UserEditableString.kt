package io.novafoundation.nova.common.utils

data class UserEditableString(val value: String, val editedByUser: Boolean = false) {

    override fun toString(): String {
        return value
    }
}
