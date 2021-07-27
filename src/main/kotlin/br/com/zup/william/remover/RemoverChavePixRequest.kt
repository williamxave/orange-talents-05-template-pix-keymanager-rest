package br.com.zup.william.remover

import br.com.zup.william.RemoveChavePixRequest
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank

@Introspected
data class RemoverChavePixRequestRest(
        @field:NotBlank val clienteId: String,
        @field:NotBlank val pixId: String
){

    fun toModel() : RemoveChavePixRequest{
        return  RemoveChavePixRequest.newBuilder()
                .setIdDoCliente(clienteId)
                .setPixId(pixId)
                .build()
    }
}