package app;

import app.model.Transacao;
import app.service.TransacaoFinanceiraService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

public class TransacaoFinanceiraApplication {

    private static TransacaoFinanceiraService transacaoFinanceiraService = new TransacaoFinanceiraService();

    public static void main(String[] args) {
        List<Transacao> listaTransacoes = Arrays.asList(
                new Transacao(1, "09/09/2023 14:15:00", "938485762", "2147483649", BigDecimal.valueOf(150)),
                new Transacao(2, "09/09/2023 14:15:05", "2147483649", "210385733", BigDecimal.valueOf(149)),
                new Transacao(3, "09/09/2023 14:15:29", "347586970", "238596054", BigDecimal.valueOf(1100)),
                new Transacao(4, "09/09/2023 14:17:00", "675869708", "210385733", BigDecimal.valueOf(5300)),
                new Transacao(5, "09/09/2023 14:18:00", "238596054", "674038564", BigDecimal.valueOf(1489)),
                new Transacao(6, "09/09/2023 14:18:20", "573659065", "563856300", BigDecimal.valueOf(49)),
                new Transacao(7, "09/09/2023 14:19:00", "938485762", "2147483649", BigDecimal.valueOf(44)),
                new Transacao(8, "09/09/2023 14:19:01", "573659065", "675869708", BigDecimal.valueOf(150))
        );
        transacaoFinanceiraService.iniciarTransferencias(listaTransacoes);
    }
}