package br.com.zup.william.detalhamento

import br.com.zup.william.BuscarChavePixResponse
import br.com.zup.william.TipoDeConta
import io.micronaut.core.annotation.Introspected
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

@Introspected
class BuscarChavePixResponseRest(response: BuscarChavePixResponse) {
    val clientId = response.clienteId
    val pixId = response.pixId

    val tipoDaChave = response.chave.tipoDaChave
    val chave = response.chave.chave

    val criadaEm = response.chave.criadaEm.let {
        LocalDateTime.ofInstant(
                Instant.ofEpochSecond(it.seconds,
                        it.nanos.toLong()), ZoneOffset.UTC)
    }
    val tipoDaConta = when (response.chave.conta.tipo) {
        TipoDeConta.CONTA_CORRENTE -> "CONTA_CORRENTE"
        TipoDeConta.CONTA_POUPANCA -> "CONTA_POUPANCA"
        else -> "NAO_RECONHECIDA"
    }
    val conta = mapOf(Pair("tipo", tipoDaConta),
            Pair("instituicao", response.chave.conta.instituicao),
            Pair("nomeDoTitular", response.chave.conta.nomeDoTitular),
            Pair("cpfDoTitular", response.chave.conta.cpfDoTitular),
            Pair("agencia", response.chave.conta.agencia),
            Pair("numeroDaConta", response.chave.conta.numeroDaConta)
    )
}