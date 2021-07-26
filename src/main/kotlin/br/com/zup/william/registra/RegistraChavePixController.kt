package br.com.zup.william.registra

import br.com.zup.william.RegistraKeyManagerPixGRPCServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.PathVariable
import io.micronaut.http.annotation.Post
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.validation.Valid

@Validated
@Controller("/api/v1/clientes/{clienteId}")
class RegistraChavePixController(
        @Inject val grpcClient: RegistraKeyManagerPixGRPCServiceGrpc
        .RegistraKeyManagerPixGRPCServiceBlockingStub
) {

    @Post("/registra")
    fun registra(@PathVariable("clienteId") clienteId: String,
                 @Valid @Body request: RegistraChaveRequest): HttpResponse<Any> {

        val responseGrpc = grpcClient.regista(request.toModel(clienteId))
        return HttpResponse.created(location(clienteId, responseGrpc.pixId))
    }

    private fun location(clienteId: String, pixId: String) = HttpResponse
            .uri("api/v1/clientes/$clienteId/registra/${pixId}")
}