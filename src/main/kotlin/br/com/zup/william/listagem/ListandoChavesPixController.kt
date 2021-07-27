package br.com.zup.william.listagem

import br.com.zup.william.ListarChavePixRequest
import br.com.zup.william.ListarChavePixResponse
import br.com.zup.william.ListarrKeyManagerPixGRPCServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable

@Controller("/api/v1/clientes/{clienteId}")
class ListandoChavesPixController(
        val grpcClient: ListarrKeyManagerPixGRPCServiceGrpc
        .ListarrKeyManagerPixGRPCServiceBlockingStub
) {
    @Get("listar")
    fun lista(@PathVariable("clienteId") clienteId: String):
            HttpResponse<Any> {

        val request = grpcClient.listar(
                ListarChavePixRequest
                        .newBuilder()
                        .setIdDoCliente(clienteId)
                        .build())

        val listaChaves = request.chavesList.map { ListaResponseRest(it) }
        return HttpResponse.ok(listaChaves)
    }
}