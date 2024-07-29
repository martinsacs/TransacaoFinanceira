package app.service;

import app.model.ContaSaldo;
import app.model.Transacao;
import app.utils.Contantes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class TransacaoFinanceiraService {

    private List<ContaSaldo> tabelaSaldos;

    public TransacaoFinanceiraService() {
        tabelaSaldos = new ArrayList<>();
        tabelaSaldos.add(new ContaSaldo("938485762", BigDecimal.valueOf(180)));
        tabelaSaldos.add(new ContaSaldo("347586970", BigDecimal.valueOf(1200)));
        tabelaSaldos.add(new ContaSaldo("2147483649", BigDecimal.valueOf(0)));
        tabelaSaldos.add(new ContaSaldo("675869708", BigDecimal.valueOf(4900)));
        tabelaSaldos.add(new ContaSaldo("238596054", BigDecimal.valueOf(478)));
        tabelaSaldos.add(new ContaSaldo("573659065", BigDecimal.valueOf(787)));
        tabelaSaldos.add(new ContaSaldo("210385733", BigDecimal.valueOf(10)));
        tabelaSaldos.add(new ContaSaldo("674038564", BigDecimal.valueOf(400)));
        tabelaSaldos.add(new ContaSaldo("563856300", BigDecimal.valueOf(1200)));
    }

    public void iniciarTransferencias(List<Transacao> listaTransacoes) {
        List<String> datasDistintas = gerarListaDataHora(listaTransacoes);

        datasDistintas.forEach(data -> {
            dispararThreads(gerarGrupoTransacoesPorDataHora(listaTransacoes, data));
        });
    }

    public List<String> gerarListaDataHora(List<Transacao> transacoes) {
        ordenarPorData(transacoes);
        return transacoes.stream()
                .map(Transacao::getDateTime)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Transacao> gerarGrupoTransacoesPorDataHora(List<Transacao> todasTransacoes, String data) {
        List<Transacao> grupoTransacoesPorHora = new ArrayList<>();

        for(Transacao transacao : todasTransacoes) {
            if(transacao.getDateTime().equals(data)) {
                grupoTransacoesPorHora.add(transacao);
            }
        }
        return grupoTransacoesPorHora;
    }
    public void dispararThreads(List<Transacao> transacoesASeremFeitas) {
        ExecutorService executor = Executors.newFixedThreadPool(8);

        for (Transacao transacao : transacoesASeremFeitas) {
            executor.execute(() -> {
                System.out.println(transferir(transacao));
            });
        }

        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public String transferir(Transacao transacao) {
        if(transacao.getContaOrigem().equals(transacao.getContaDestino()))
            return Contantes.MENSAGEM_CONTAS_IGUAIS;

        if(contasExistem(transacao.getContaOrigem(), transacao.getContaDestino())) {
            BigDecimal valorTransacao = transacao.getValor();
            ContaSaldo contaSaldoOrigem = getContaSaldo(transacao.getContaOrigem());
            if (contaSaldoOrigem.getSaldo().compareTo(valorTransacao) == -1) {
                return "Transação número " + transacao.getCorrelationId() + " foi cancelada por falta de saldo.";
            } else {
                ContaSaldo contaSaldoDestino = getContaSaldo(transacao.getContaDestino());
                contaSaldoOrigem.diminuirSaldo(valorTransacao);
                contaSaldoDestino.aumentarSaldo(valorTransacao);
                if (atualizarSaldo(contaSaldoDestino) && atualizarSaldo(contaSaldoOrigem)) {
                    return "Transação número " + transacao.getCorrelationId() + " foi efetivada com sucesso! Novos saldos: Conta Origem: " + contaSaldoOrigem.getSaldo() + " | Conta Destino: " + contaSaldoDestino.getSaldo() + ".";
                } else {
                    // Caso não consiga atualizar a "base" de transações, a transação será desfeita
                    contaSaldoOrigem.aumentarSaldo(valorTransacao);
                    contaSaldoDestino.diminuirSaldo(valorTransacao);
                    return "Erro ao atualizar os saldos. Transferência cancelada.";
                }
            }
        } else {
            return "Erro ao executar transação " + transacao.getCorrelationId() + ". Pelo menos uma das contas não existe.";
        }
    }

    public boolean atualizarSaldo(ContaSaldo item)
    {
        try {
            tabelaSaldos.removeIf(x -> x.getConta().equals(item.getConta()));
            tabelaSaldos.add(item);
            return true;
        } catch (Exception e) {
            System.out.println("Ocorreu um erro ao tentar atualizar dados. " + e.getMessage());
            return false;
        }
    }

    public ContaSaldo getContaSaldo(String numeroContaOrigem) {
        return tabelaSaldos.stream().filter(x -> x.getConta().equals(numeroContaOrigem)).findFirst().orElse(null);
    }

    public boolean contasExistem(String contaOrigem, String contaDestino) {
        return (tabelaSaldos.stream().anyMatch(x -> x.getConta().equals(contaOrigem)) &&
                tabelaSaldos.stream().anyMatch(y -> y.getConta().equals(contaDestino)));
    }

    public void ordenarPorData(List<Transacao> listaTransacoes) {
        listaTransacoes.sort(Comparator.comparing(transacao -> LocalDateTime.parse(transacao.getDateTime(), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))));
    }
}
