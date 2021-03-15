package namba.wallet.nambaone.common.exception

class ConnectionException(cause: Throwable) : Throwable("Error connecting to server", cause)
