package io.agora.api.example.common.server.api


internal class ResultException constructor(
    errCode: Int,
    msg: String
) : Exception(errCode.toString() + msg) {
    var errCode: Int? = errCode
}