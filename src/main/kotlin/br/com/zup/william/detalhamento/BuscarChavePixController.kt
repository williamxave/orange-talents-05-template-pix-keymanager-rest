package br.com.zup.william.detalhamento

import br.com.zup.william.BuscarChavePixRequest
import br.com.zup.william.BuscarKeyManagerPixGRPCServiceGrpc
import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.PathVariable

@Controller("/api/v1/clientes/{clientId}")
class BuscarChavePixController(
        val grpcClient: BuscarKeyManagerPixGRPCServiceGrpc
        .BuscarKeyManagerPixGRPCServiceBlockingStub
) {
    @Get("/buscar/{pixId}")
    fun buscar(@PathVariable("clientId") clientId: String, @PathVariable("pixId") pixId: String):
            HttpResponse<Any> {
        val request = grpcClient.buscar(
                BuscarChavePixRequest.newBuilder()
                        .setPixId(
                                BuscarChavePixRequest.FiltroPorPixId.newBuilder()
                                        .setClientId(clientId)
                                        .setPixId(pixId)
                                        .build())
                        .build())
        return HttpResponse.ok(BuscarChavePixResponseRest(request))
    }
}