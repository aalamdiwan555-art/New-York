package com.example.ridepricematcher.domain.model

sealed class AppError(
    val category: String,
    val userMessage: String,
    val technicalMessage: String
) : Exception(userMessage) {
    class Network(userMessage: String, technicalMessage: String) :
        AppError("NETWORK", userMessage, technicalMessage)

    class Auth(userMessage: String, technicalMessage: String) :
        AppError("AUTH", userMessage, technicalMessage)

    class Validation(userMessage: String, technicalMessage: String) :
        AppError("VALIDATION", userMessage, technicalMessage)

    class Server(userMessage: String, technicalMessage: String) :
        AppError("SERVER", userMessage, technicalMessage)

    class Unknown(userMessage: String, technicalMessage: String) :
        AppError("UNKNOWN", userMessage, technicalMessage)

    class Blocked(userMessage: String = "Your account has been blocked. Contact support.", technicalMessage: String = "User account blocked") :
        AppError("BLOCKED", userMessage, technicalMessage)
}
