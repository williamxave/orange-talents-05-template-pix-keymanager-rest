package br.com.zup.william.registra

import br.com.zup.william.*
import br.com.zup.william.grpclient.GrpcClientFactory
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
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mock
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

    @Test
    fun `nao deve cadastra chave pix se usuario invalido`() {

        // cenario
        Mockito.`when`(grcpCliente.regista(
                RegistraChavePixRequest
                        .newBuilder()
                        .setIdDoCliente(CLIENTE_ID)
                        .setTipoDeConta(TipoDeConta.CONTA_CORRENTE)
                        .setValorDaChave("william@testando.com")
                        .setTipoDeChave(TipoDeChave.EMAIL)
                        .build()))
                .thenThrow(StatusRuntimeException(Status.NOT_FOUND))

        val novaChavePix = RegistraChaveRequest(
                TipoDeChaveRequest.EMAIL,
                "william@testando.com",
                TipoDeContaRequest.CONTA_CORRENTE
        )
        //acao
        val requestHttp = HttpRequest.POST("/api/v1/clientes/$CLIENTE_ID/registra", novaChavePix)
        val response = assertThrows<HttpClientResponseException> {
            client.toBlocking().exchange(requestHttp, RegistraChavePixResponse::class.java)
        }
        //validacao
        assertEquals(HttpStatus.NOT_FOUND, response.status)
    }

    @Nested
    inner class ChaveAleatoriaTest{

        @Test
        fun `deve ser valido quando o campo de chave aleatoria for nullo ou vazio`(){
            val tipoDeChave = TipoDeChaveRequest.CHAVE_ALEATORIA
            assertTrue(tipoDeChave.valida(null))
            assertTrue(tipoDeChave.valida(""))

        }

        @Test
        fun `nao deve ser valido quando o campo de chave aleatoria for preenchida pelo usuario`(){
            val tipoDeChave = TipoDeChaveRequest.CHAVE_ALEATORIA
            assertFalse(tipoDeChave.valida("nao pode ser preenchido"))
        }
    }

    @Nested
    inner class CpfTest(){

        @Test
        fun `deve ser valido se for cpf de valido`(){
            val cpf =  TipoDeChaveRequest.CPF
            assertTrue(cpf.valida("98846876091"))
        }

        @Test
        fun `nao deve ser valido se o cpf for invalido ou vazio`(){
            val cpf = TipoDeChaveRequest.CPF
            assertFalse(cpf.valida(""))
            assertFalse(cpf.valida(null))
            assertFalse(cpf.valida("9884687609"))
        }
    }

    @Nested
    inner class  EmailTest(){

        @Test
        fun `deve ser valido se o email tiver bem formado`(){
            val email = TipoDeChaveRequest.EMAIL
            assertTrue(email.valida("william@teste.com"))
        }

        @Test
        fun `nao deve ser valido se tiver o email vazio, nullo ou mal formado`(){
            val email = TipoDeChaveRequest.EMAIL
            assertFalse(email.valida(""))
            assertFalse(email.valida(null))
            assertFalse(email.valida("william.com"))
        }

    }

    @Nested
    inner class  TelefoneTest(){

        @Test
        fun `deve ser valido se o telefone tiver formato valido`(){
            val fone = TipoDeChaveRequest.TELEFONE_CELULAR
            assertTrue(fone.valida("+5551980528176"))
        }

        @Test
        fun `nao deve ser valido se o fone for vazio, nullo ou mal formado`(){
            val fone = TipoDeChaveRequest.TELEFONE_CELULAR
            assertFalse(fone.valida("+555198052817iii"))
            assertFalse(fone.valida(""))
            assertFalse(fone.valida(null))
        }
    }


    @Factory
    @Replaces(factory = GrpcClientFactory::class) // O micronaut vai entender que deve trocar as factorys
    internal class grpcFactory {
        @Singleton
        fun stubMock() = Mockito.mock(RegistraKeyManagerPixGRPCServiceGrpc
                .RegistraKeyManagerPixGRPCServiceBlockingStub::class.java)
    }
}