package br.com.zup.william.remover

import br.com.zup.william.RemoveKeyManagerPixGRPCServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Delete
import javax.inject.Inject

@Controller("/api/v1/clientes/{clienteId}")
class RemoveChavePixController(
        @Inject val grcpCliente: RemoveKeyManagerPixGRPCServiceGrpc
        .RemoveKeyManagerPixGRPCServiceBlockingStub
) {
    @Delete("/deletar")
    fun delete(chavePix: RemoverChavePixRequestRest): HttpResponse<Any> {
        val response = grcpCliente.remove(chavePix.toModel())
        return HttpResponse.ok()
    }
}