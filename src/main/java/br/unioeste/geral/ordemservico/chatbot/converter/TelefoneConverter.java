package br.unioeste.geral.ordemservico.chatbot.converter;

import br.unioeste.geral.pessoa.bo.ddd.DDD;
import br.unioeste.geral.pessoa.bo.ddi.DDI;
import br.unioeste.geral.pessoa.bo.email.Email;
import br.unioeste.geral.pessoa.bo.telefone.Telefone;

import java.util.List;
import java.util.stream.Collectors;

public class TelefoneConverter {
    public String converterTelefoneParaString(Telefone telefone){
        DDD ddd = telefone.getDdd();
        DDI ddi = telefone.getDdi();

        return String.format("(%s) %s %s", ddi.getNumero(), ddd.getNumero(), telefone.getNumero());
    }

    public String converterTelefonesParaString(List<Telefone> telefones){
        return telefones.stream().map(this::converterTelefoneParaString).collect(Collectors.joining(", "));
    }
}
