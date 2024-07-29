package app.model;

import java.math.BigDecimal;

public class ContaSaldo {
    private String conta;
    private BigDecimal saldo;

    public ContaSaldo(String conta, BigDecimal saldo) {
        this.conta = conta;
        this.saldo = saldo;
    }

    public String getConta() {
        return conta;
    }

    public void setConta(String conta) {
        this.conta = conta;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void aumentarSaldo(BigDecimal valor) {
        saldo = saldo.add(valor);
    }

    public void diminuirSaldo(BigDecimal valor) {
        saldo = saldo.subtract(valor);
    }
}
