package io.rightpad.daxplorer.utils

class MutableOptional<T>(var value: T? = null) {
    fun get() = this.value

    fun set(value: T?): MutableOptional<T> {
        this.value = value
        return this
    }
}