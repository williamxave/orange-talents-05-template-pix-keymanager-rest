package br.com.zup.william.grpclient

import br.com.zup.william.RegistraKeyManagerPixGRPCServiceGrpc
import io.grpc.ManagedChannel
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import javax.inject.Singleton

@Factory
class GrpcClientFactory {
    @Singleton
    fun registraClientStub(@GrpcChannel("pix") channel: ManagedChannel):
            RegistraKeyManagerPixGRPCServiceGrpc
            .RegistraKeyManagerPixGRPCServiceBlockingStub {
        return RegistraKeyManagerPixGRPCServiceGrpc.newBlockingStub(channel)
    }
}