package br.com.zup.william.grpclient

import br.com.zup.william.RegistraKeyManagerPixGRPCServiceGrpc
import br.com.zup.william.RemoveKeyManagerPixGRPCServiceGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

@Factory
class GrpcClientFactory(@GrpcChannel("pix") val channel: ManagedChannel) {

    @Singleton
    fun registraChave() = RegistraKeyManagerPixGRPCServiceGrpc.newBlockingStub(channel)

    @Singleton
    fun removeChave() = RemoveKeyManagerPixGRPCServiceGrpc.newBlockingStub(channel)

}