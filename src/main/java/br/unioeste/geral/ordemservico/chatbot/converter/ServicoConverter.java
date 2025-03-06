package br.unioeste.geral.ordemservico.chatbot.converter;

import br.unioeste.geral.ordemservico.bo.servico.Servico;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class ServicoConverter {
    public String converterServicoParaString(Servico servico){
        NumberFormat formatadorMoeda = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));

        String mensagem = """
        Tipo de serviço: %s
        Valor cobrado: %s
        """;

        return String.format(
                mensagem,
                servico.getTipoServico().getNome(),
                formatadorMoeda.format(servico.getValorCobrado())
        );
    }

    public String converterServicosParaString(List<Servico> servicos){
        return servicos.stream().map(this::converterServicoParaString).collect(Collectors.joining("\n"));
    }
}
