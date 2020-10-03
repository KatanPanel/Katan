package me.devnatan.katan.webserver.serializable

import com.fasterxml.jackson.annotation.JsonIgnore
import me.devnatan.katan.api.account.Account
import me.devnatan.katan.common.account.SecureAccount

class SerializableAccount(delegate: SecureAccount) : Account by delegate {

    @JsonIgnore
    var password = delegate.password

}