package br.com.zup.william.remover

import br.com.zup.william.RemoveKeyManagerPixGRPCServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import io.micronaut.validation.Validated
import javax.inject.Inject
import javax.validation.Valid

@Controller("/api/v1/clientes/{clienteId}")
@Validated
class RemoveChavePixController(
        @Inject val grcpCliente: RemoveKeyManagerPixGRPCServiceGrpc
        .RemoveKeyManagerPixGRPCServiceBlockingStub
) {
    @Delete("/deletar")
    fun delete(@Body @Valid chavePix: RemoverChavePixRequestRest): HttpResponse<Any> {
        val response = grcpCliente.remove(chavePix.toModel())
        return HttpResponse.ok()
    }
}