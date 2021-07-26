package br.com.zup.william.registra

import br.com.zup.william.RegistraChavePixRequest
import br.com.zup.william.TipoDeChave
import br.com.zup.william.TipoDeConta
import br.com.zup.william.annotacoes.ValidaTipoChavePix
import io.micronaut.core.annotation.Introspected
import org.hibernate.validator.internal.constraintvalidators.hv.EmailValidator
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ValidaTipoChavePix
@Introspected
data class RegistraChaveRequest(
        @field:NotNull val tipoDeChave: TipoDeChaveRequest?,
        @field:Size(max = 77) val valorDaChave: String?,
        @field:NotNull val tipoDeConta: TipoDeContaRequest?
) {
    fun toModel(clienteId: String?): RegistraChavePixRequest {

        return RegistraChavePixRequest.newBuilder()
                .setIdDoCliente(clienteId)
                .setTipoDeChave(tipoDeChave?.atributoGrpc ?: TipoDeChave.TIPO_DE_CHAVE_DESCONHECIDA)
                .setValorDaChave(valorDaChave ?: "")
                .setTipoDeConta(tipoDeConta?.atributoGrpc ?: TipoDeConta.CONTA_DESCONHECIDA)
                .build()
    }
}

enum class TipoDeChaveRequest(val atributoGrpc: TipoDeChave) {

    CPF(TipoDeChave.CPF) {
        override fun valida(valorDaChave: String?): Boolean {
            if (valorDaChave.isNullOrBlank()) {
                return false
            }
            if (!valorDaChave.matches("^[0-9]{11}$".toRegex())) {
                return false
            }
            return CPFValidator().run {
                initialize(null)
                isValid(valorDaChave, null)
            }
        }

    },
    TELEFONE_CELULAR(TipoDeChave.TELEFONE_CELULAR) {
        override fun valida(valorDaChave: String?): Boolean {
            if (valorDaChave.isNullOrBlank()) {
                return false
            }
            return valorDaChave.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())
        }
    },
    EMAIL(TipoDeChave.EMAIL) {
        override fun valida(valorDaChave: String?): Boolean {
            if (valorDaChave.isNullOrBlank()) {
                return false
            }
            return EmailValidator().run {
                initialize(null)
                isValid(valorDaChave, null)
            }
        }

    },
    CHAVE_ALEATORIA(TipoDeChave.CHAVE_ALEATORIA) {
        override fun valida(valorDaChave: String?): Boolean {
            return valorDaChave.isNullOrBlank()
        }
    };

    abstract fun valida(valorDaChave: String?): Boolean
}

enum class TipoDeContaRequest(val atributoGrpc: TipoDeConta) {
    CONTA_CORRENTE(TipoDeConta.CONTA_CORRENTE),
    CONTA_POUPANCA(TipoDeConta.CONTA_CORRENTE)
}
