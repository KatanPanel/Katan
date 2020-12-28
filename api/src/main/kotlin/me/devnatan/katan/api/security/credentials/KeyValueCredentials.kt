package me.devnatan.katan.api.security.credentials

class KeyValueCredentials<K, out V>(map: Map<K, V>) : Credentials, Map<K, V> by map