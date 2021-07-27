package br.com.zup.william.detalhamento

import br.com.zup.william.BuscarChavePixResponse
import br.com.zup.william.BuscarKeyManagerPixGRPCServiceGrpc
import br.com.zup.william.TipoDeChave
import br.com.zup.william.TipoDeConta
import br.com.zup.william.grpclient.GrpcClientFactory
import com.google.protobuf.Timestamp
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton


@MicronautTest
internal class BuscarChavePixControllerTest(
        @Inject val grpcClient: BuscarKeyManagerPixGRPCServiceGrpc
        .BuscarKeyManagerPixGRPCServiceBlockingStub
) {
    companion object {
        val CLIENTE_ID = UUID.randomUUID().toString()
        val PIX_ID = UUID.randomUUID().toString()
        val CRIADA_EM = LocalDateTime.now()
    }

    @field:Inject
    @field:Client("/")
    lateinit var client: HttpClient

    @Test
    fun `deve buscar chave pix pelo pixId e clienteID`() {
        //cenario
        Mockito.`when`(grpcClient.buscar(Mockito.any())).thenReturn(
                BuscarChavePixResponse.newBuilder()
                        .setClienteId(CLIENTE_ID)
                        .setPixId(PIX_ID)
                        .setChave(
                                BuscarChavePixResponse.ChavePix.newBuilder()
                                        .setTipoDaChave(TipoDeChave.EMAIL)
                                        .setChave("william@teste.com")
                                        .setConta(
                                                BuscarChavePixResponse.ChavePix.ContaInfo.newBuilder()
                                                        .setTipo(TipoDeConta.CONTA_CORRENTE)
                                                        .setInstituicao("ITAU S.A.")
                                                        .setNomeDoTitular("william")
                                                        .setCpfDoTitular("34597563067")
                                                        .setAgencia("001")
                                                        .setNumeroDaConta("3306")
                                                        .build())
                                        .setCriadaEm(CRIADA_EM.let {
                                            val createdAt = it.atZone(ZoneId.of("UTC")).toInstant()
                                            Timestamp.newBuilder()
                                                    .setSeconds(createdAt.epochSecond)
                                                    .setNanos(createdAt.nano)
                                                    .build()
                                        })).build())

        //acao
        val requestHttp = HttpRequest.GET<Any>("/api/v1/clientes/$CLIENTE_ID/buscar/$PIX_ID")
        val responseHttp = client.toBlocking().exchange(requestHttp, Any::class.java)

        //validacao
        assertEquals(HttpStatus.OK, responseHttp.status)
        assertNotNull(responseHttp.body())
    }

    @Test
    fun `nao deve buscar se chave pix nao existir`() {
        //cenario
        Mockito.`when`(grpcClient.buscar(Mockito.any())).thenThrow(StatusRuntimeException(Status.NOT_FOUND))
        //acao
        val requestHttp = HttpRequest.GET<Any>("/api/v1/clientes/$CLIENTE_ID/buscar/$PIX_ID")
        val responseHttp = assertThrows<HttpClientResponseException>{
            client.toBlocking().exchange(requestHttp, Any::class.java)
        }
        //validacao
            assertEquals(HttpStatus.NOT_FOUND, responseHttp.status)
    }

    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class GrcpClientBuscar() {
        @Singleton
        fun mockStubBuscar() = Mockito.mock(BuscarKeyManagerPixGRPCServiceGrpc
                .BuscarKeyManagerPixGRPCServiceBlockingStub::class.java)
    }

}