package br.com.zup.william.listagem

import br.com.zup.william.ListarChavePixResponse
import br.com.zup.william.ListarrKeyManagerPixGRPCServiceGrpc
import br.com.zup.william.TipoDeChave
import br.com.zup.william.TipoDeConta
import br.com.zup.william.grpclient.GrpcClientFactory
import com.google.protobuf.Timestamp
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class ListandoChavesPixControllerTest(
        val grpcClient: ListarrKeyManagerPixGRPCServiceGrpc
        .ListarrKeyManagerPixGRPCServiceBlockingStub
) {
    @field:Inject
    @field:Client("/")
    lateinit var client: HttpClient

    companion object {
        val ID_CLIENTE = UUID.randomUUID().toString()
        val CRIADA_EM = LocalDateTime.now()
    }

    @Test
    fun `deve listar as chaves de um cliente, pelo seu id`() {

        val chaveEmail = listaDeChaves("william@teste.com", TipoDeChave.EMAIL)
        val chaveCpf = listaDeChaves("34597563067", TipoDeChave.CPF)

        val responseGrcp = ListarChavePixResponse.newBuilder()
                .setClientId(ID_CLIENTE)
                .addAllChaves(listOf(chaveEmail, chaveCpf))
                .build()

        Mockito.`when`(grpcClient.listar(Mockito.any())).thenReturn(responseGrcp)

        val requestHttp = HttpRequest.GET<Any>("/api/v1/clientes/${ID_CLIENTE}/listar")
        val response = client.toBlocking().exchange(requestHttp, Any::class.java)

        Assertions.assertEquals(HttpStatus.OK, response.status)
        Assertions.assertNotNull(response.body())

    }
    private fun listaDeChaves(chave: String, tipoDeChave: TipoDeChave): ListarChavePixResponse.Listar {
        return ListarChavePixResponse.Listar.newBuilder()
                .setPixId(UUID.randomUUID().toString())
                .setValorDaChave(chave)
                .setTipoDeChave(tipoDeChave)
                .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                .setCriadaEm(
                        CRIADA_EM.let {
                            val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                            Timestamp.newBuilder()
                                    .setSeconds(createdAt.epochSecond)
                                    .setNanos(createdAt.nano)
                                    .build()
                        }
                ).build()

    }

    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class MockListagem() {
        @Singleton
        fun mockStubListagem() = Mockito.mock(ListarrKeyManagerPixGRPCServiceGrpc
                .ListarrKeyManagerPixGRPCServiceBlockingStub::class.java)

    }

}