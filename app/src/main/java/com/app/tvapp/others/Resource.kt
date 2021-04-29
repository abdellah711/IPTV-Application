package com.app.tvapp.others

sealed class Resource<T>(data: T?,error: String?) {
    class Loading<T>(): Resource<T>(null,null)
    class Success<T>(val data: T): Resource<T>(data,null)
    class Error<T>(val error: String): Resource<T>(null,error)
}