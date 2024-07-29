package tests;

import app.model.Transacao;
import app.service.TransacaoFinanceiraService;
import app.utils.Contantes;
import org.junit.*;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;


public class TransacaoFinanceiraServiceTest {
    private static TransacaoFinanceiraService transacaoFinanceiraService = new TransacaoFinanceiraService();

    @Test
    public void deveRealizarTransferenciaComSucesso() {
        BigDecimal saldoDesejadoOrigem = transacaoFinanceiraService.getContaSaldo("675869708").getSaldo().subtract(BigDecimal.valueOf(1994.50));
        BigDecimal saldoDesejadoDestino = transacaoFinanceiraService.getContaSaldo("210385733").getSaldo().add(BigDecimal.valueOf(1994.50));
        Transacao transacao = new Transacao(1, "26/07/2024 17:07:00", "675869708", "210385733", BigDecimal.valueOf(1994.50));
        transacaoFinanceiraService.transferir(transacao);
        assertEquals(0, saldoDesejadoOrigem.compareTo(transacaoFinanceiraService.getContaSaldo("675869708").getSaldo()));
        assertEquals(0, saldoDesejadoDestino.compareTo(transacaoFinanceiraService.getContaSaldo("210385733").getSaldo()));
    }

    @Test
    public void deveNaoRealizarTransferenciaContasIguais() {
        Transacao transacao = new Transacao(1, "26/07/2024 17:07:00", "675869708", "675869708", BigDecimal.valueOf(1994.50));
        Assert.assertEquals(Contantes.MENSAGEM_CONTAS_IGUAIS, transacaoFinanceiraService.transferir(transacao));
    }


    @Test
    public void deveNaoRealizarTransferenciaSaldoInsuficiente() {
        Transacao transacao = new Transacao(1, "26/07/2024 17:07:00", "675869708", "210385733", BigDecimal.valueOf(31994.50));
        String retorno = transacaoFinanceiraService.transferir(transacao);
        assertThat(retorno, containsString("falta de saldo"));
    }

    @Test
    public void deveGerarListaDeDatasCorretamente() {
        List<String> datasDistintas = transacaoFinanceiraService.gerarListaDataHora(gerarListaTransacoesHorasRepetidas());
        Assert.assertTrue(datasDistintas.size() == 2);
        Assert.assertEquals("27/07/2024 15:56:00", datasDistintas.get(0));
        Assert.assertEquals( "27/07/2024 15:56:01", datasDistintas.get(1));
    }

    @Test
    public void deveOcorrerErroContaNaoExiste() {
        Transacao transacao = new Transacao(1, "26/07/2024 17:07:00", "758490", "210385733", BigDecimal.valueOf(1994.50));
        assertThat(transacaoFinanceiraService.transferir(transacao), containsString("não existe"));
    }

    @Test
    public void deveOrdenarListaPorDataHoraCorretamente() {
        List<String> datasDistintas = transacaoFinanceiraService.gerarListaDataHora(gerarListaTransacoesVariasHoras());
        Assert.assertEquals( "13/07/2022 12:56:00", datasDistintas.get(0));
        Assert.assertEquals( "13/07/2022 15:56:00", datasDistintas.get(1));
        Assert.assertEquals( "15/07/2022 15:56:01", datasDistintas.get(2));
        Assert.assertEquals( "18/07/2022 11:49:00", datasDistintas.get(3));
        Assert.assertEquals( "18/07/2022 11:50:00", datasDistintas.get(4));
        Assert.assertEquals( "27/07/2022 15:56:01", datasDistintas.get(5));
        Assert.assertEquals( "01/06/2023 10:36:01", datasDistintas.get(6));
    }
    private List<Transacao> gerarListaTransacoesHorasRepetidas() {
        return Arrays.asList(
            new Transacao(1, "27/07/2024 15:56:01", "1234", "4321", BigDecimal.valueOf(150)),
            new Transacao(2, "27/07/2024 15:56:01", "1201", "1234", BigDecimal.valueOf(149)),
            new Transacao(3, "27/07/2024 15:56:00", "1205", "4342", BigDecimal.valueOf(1100)),
            new Transacao(4, "27/07/2024 15:56:01", "4327", "9328", BigDecimal.valueOf(5300)),
            new Transacao(5, "27/07/2024 15:56:00", "4387", "1923", BigDecimal.valueOf(1489)),
            new Transacao(6, "27/07/2024 15:56:00", "4932", "0912", BigDecimal.valueOf(49)),
            new Transacao(7, "27/07/2024 15:56:01", "8129", "9832", BigDecimal.valueOf(44))
        );
    }

    private List<Transacao> gerarListaTransacoesVariasHoras() {
        return Arrays.asList(
            new Transacao(1, "01/06/2023 10:36:01", "1234", "4321", BigDecimal.valueOf(150)),
            new Transacao(2, "18/07/2022 11:50:00", "1201", "1234", BigDecimal.valueOf(149)),
            new Transacao(3, "18/07/2022 11:49:00", "1205", "4342", BigDecimal.valueOf(1100)),
            new Transacao(4, "15/07/2022 15:56:01", "4327", "9328", BigDecimal.valueOf(5300)),
            new Transacao(5, "13/07/2022 15:56:00", "4387", "1923", BigDecimal.valueOf(1489)),
            new Transacao(6, "13/07/2022 12:56:00", "4932", "0912", BigDecimal.valueOf(49)),
            new Transacao(7, "27/07/2022 15:56:01", "8129", "9832", BigDecimal.valueOf(44))
        );
    }
}
