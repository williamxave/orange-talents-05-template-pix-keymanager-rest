package br.com.zup.william.listagem

import br.com.zup.william.ListarChavePixResponse
import io.micronaut.core.annotation.Introspected
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Introspected
class ListaResponseRest(chavePix: ListarChavePixResponse.Listar) {
    val pixId = chavePix.pixId
    val clienteId = chavePix.idDoCliente
    val chave = chavePix.valorDaChave
    val tipoDeChave = chavePix.tipoDeChave
    val tipoDeConta = chavePix.tipoDeConta
    val criadaEm = chavePix.criadaEm.let {
        LocalDateTime.ofInstant(Instant.ofEpochSecond(it.seconds, it.nanos.toLong()), ZoneOffset.UTC)
    }
}