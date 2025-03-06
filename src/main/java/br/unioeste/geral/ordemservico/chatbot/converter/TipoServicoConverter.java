package br.unioeste.geral.ordemservico.chatbot.converter;

import br.unioeste.geral.ordemservico.bo.tiposervico.TipoServico;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class TipoServicoConverter {
    public String converterTipoServicoParaString(TipoServico tipoServico) {
        NumberFormat formatadorMoeda = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));

        String mensagem = """
        ID: %d
        Nome: %s
        Valor de referência: %s
        """;

        return String.format(
                mensagem,
                tipoServico.getId(),
                tipoServico.getNome(),
                formatadorMoeda.format(tipoServico.getValorReferencia())
        );
    }

    public String converterTipoServicosParaString(List<TipoServico> tipoServicos) {
        return tipoServicos.stream().map(this::converterTipoServicoParaString).collect(Collectors.joining("\n"));
    }
}
