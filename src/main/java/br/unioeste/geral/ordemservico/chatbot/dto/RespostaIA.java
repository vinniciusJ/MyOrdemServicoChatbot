package br.unioeste.geral.ordemservico.chatbot.dto;

import java.io.Serializable;

public record RespostaIA<T> (
    Intencao intencao,
    T dados,
    String mensagemPadrao
) implements Serializable {
}
