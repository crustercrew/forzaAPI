package com.api.forzaapi.utils.errorhandler

sealed class DomainException(message: String) : RuntimeException(message)