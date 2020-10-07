import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class AlreadyInitializedPropertyException : IllegalStateException()

class InitOnceProperty<T> : ReadWriteProperty<Any, T> {

    private object EMPTY

    private var value: Any? = EMPTY

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        if (value === EMPTY)
            throw UnsupportedOperationException("Not yet initialized: ${property.name}")
        else
            return value as T
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        if (this.value != EMPTY)
            throw AlreadyInitializedPropertyException()
        this.value = value
    }

}