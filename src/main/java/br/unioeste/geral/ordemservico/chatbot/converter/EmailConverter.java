package br.unioeste.geral.ordemservico.chatbot.converter;

import br.unioeste.geral.pessoa.bo.email.Email;

import java.util.List;
import java.util.stream.Collectors;

public class EmailConverter {
    public String converterEmailParaString(Email email){
        return email.getEndereco();
    }

    public String converterEmailsParaString(List<Email> emails){
        return emails.stream().map(this::converterEmailParaString).collect(Collectors.joining(", "));
    }
}
