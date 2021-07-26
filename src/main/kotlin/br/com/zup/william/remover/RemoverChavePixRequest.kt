package br.com.zup.william.remover

import br.com.zup.william.RemoveChavePixRequest

data class RemoverChavePixRequestRest(
        val clienteId: String,
        val pixId: String
){

    fun toModel() : RemoveChavePixRequest{
        return  RemoveChavePixRequest.newBuilder()
                .setIdDoCliente(clienteId)
                .setPixId(pixId)
                .build()
    }
}