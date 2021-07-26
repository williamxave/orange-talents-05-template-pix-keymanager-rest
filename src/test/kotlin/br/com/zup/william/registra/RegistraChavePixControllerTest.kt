package br.com.zup.william.registra

import br.com.zup.william.RegistraChavePixResponse
import br.com.zup.william.RegistraKeyManagerPixGRPCServiceGrpc
import br.com.zup.william.grpclient.GrpcClientFactory
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class RegistraChavePixControllerTest(
        val grcpCliente: RegistraKeyManagerPixGRPCServiceGrpc.RegistraKeyManagerPixGRPCServiceBlockingStub
) {

    @field:Inject
    @field:Client("/")
    lateinit var client: HttpClient

    companion object {
        val CLIENTE_ID = UUID.randomUUID().toString()
        val PIX_ID = UUID.randomUUID().toString()
    }

    @Test
    fun `deve registrar uma chave pix `() {
        // cenario
        val resposta = RegistraChavePixResponse.newBuilder()
                .setIdDoCliente(CLIENTE_ID)
                .setPixId(PIX_ID)
                .build()

        Mockito.`when`(grcpCliente.regista(Mockito.any())).thenReturn(resposta)

        val novaChavePix = RegistraChaveRequest(
                TipoDeChaveRequest.EMAIL,
                "william@testando.com",
                TipoDeContaRequest.CONTA_CORRENTE
        )
        //acao
        val requestHttp = HttpRequest.POST("/api/v1/clientes/$CLIENTE_ID/registra", novaChavePix)
        val responseHttp = client.toBlocking().exchange(requestHttp, RegistraChavePixResponse::class.java)

        //validacao
        assertEquals(HttpStatus.CREATED, responseHttp.status)
        assertTrue(responseHttp.header("Location")!!.contains(PIX_ID))
        assertTrue(responseHttp.headers.contains("Location"))
    }

    @Factory
    @Replaces(factory = GrpcClientFactory::class) // O micronaut vai entender que deve trocar as factorys
    internal class grpcFactory {
        @Singleton
        fun stubMock() = Mockito.mock(RegistraKeyManagerPixGRPCServiceGrpc
                .RegistraKeyManagerPixGRPCServiceBlockingStub::class.java)
    }
}