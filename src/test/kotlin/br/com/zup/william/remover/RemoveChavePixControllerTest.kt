package br.com.zup.william.remover

import br.com.zup.william.RemoveChavePixResponse
import br.com.zup.william.RemoveKeyManagerPixGRPCServiceGrpc
import br.com.zup.william.grpclient.GrpcClientFactory
import br.com.zup.william.handler.GlobalExceptionHandler
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.context.annotation.Replaces
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpStatus
import io.micronaut.http.client.HttpClient
import io.micronaut.http.client.annotation.Client
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest
internal class RemoveChavePixControllerTest(
        val grpcClient: RemoveKeyManagerPixGRPCServiceGrpc
        .RemoveKeyManagerPixGRPCServiceBlockingStub
) {

    val requestGenerica = HttpRequest.GET<Any>("/")

    @field:Inject
    @field:Client("/")
    lateinit var client: HttpClient

    companion object {
        val CLIENTE_ID = UUID.randomUUID().toString()
        val PIX_ID = UUID.randomUUID().toString()
    }

    @Test
    fun `deve remover uma chave pix`() {
        //cenario
        val resposta = RemoveChavePixResponse.newBuilder()
                .setIdDoCliente(CLIENTE_ID)
                .setPixId(PIX_ID)
                .setMensagem("Removido com sucesso")
                .build()

        Mockito.`when`(grpcClient.remove(Mockito.any())).thenReturn(resposta)

        val request = RemoverChavePixRequestRest(CLIENTE_ID, PIX_ID)
        //acao
        val requestHttp = HttpRequest.DELETE("/api/v1/clientes/$CLIENTE_ID/deletar", request)
        val responseHttp = client.toBlocking().exchange(requestHttp, Any::class.java)
        //validacao

        assertEquals(HttpStatus.OK, responseHttp.status)
    }

    @Nested
    inner class HandlerTest() {

        @Test
        fun `deve retornar status 404`() {
            //cenario
            val mensagem = "nao encontrado"
            val notFound = StatusRuntimeException(Status.NOT_FOUND.withDescription(mensagem))
            //acao
            val handle = GlobalExceptionHandler().handle(requestGenerica, notFound)
            //validacao
            with(handle) {
                assertEquals(HttpStatus.NOT_FOUND, handle.status)
                assertNotNull(handle.body())
            }
        }

        @Test
        fun `deve retornar status 422`() {
            //cenario
            val mesnsagem = "chave já existente"
            val jaExistente = StatusRuntimeException(Status.ALREADY_EXISTS)
            //acao
            val handle = GlobalExceptionHandler().handle(requestGenerica, jaExistente)
            //validacao
            with(handle) {
                assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, handle.status)
                assertNotNull(handle.body())
            }
        }


        @Test
        fun`deve retornar status 500`(){
            //cenario
            val mesnsagem = "erro inesperado"
            val jaExistente =  StatusRuntimeException(Status.INTERNAL)
            //acao
            val handle = GlobalExceptionHandler().handle(requestGenerica, jaExistente)
            //validacao
            with(handle){
                assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, handle.status)
                assertNotNull(handle.body())
            }
        }
        @Test
        fun`deve retornar status 4OO`(){
            //cenario
            val mesnsagem = "Dados da requisição estão inválidos"
            val jaExistente =  StatusRuntimeException(Status.INVALID_ARGUMENT)
            //acao
            val handle = GlobalExceptionHandler().handle(requestGenerica, jaExistente)
            //validacao
            with(handle){
                assertEquals(HttpStatus.BAD_REQUEST, handle.status)
                assertNotNull(handle.body())
            }

        }
    }

    @Factory
    @Replaces(factory = GrpcClientFactory::class)
    internal class grpcFactoryRemove {
        @Singleton
        fun mockStubRemove() = Mockito.mock(RemoveKeyManagerPixGRPCServiceGrpc
                .RemoveKeyManagerPixGRPCServiceBlockingStub::class.java)
    }

}
