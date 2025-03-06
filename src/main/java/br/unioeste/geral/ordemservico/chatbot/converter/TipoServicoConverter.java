package br.unioeste.geral.ordemservico.chatbot.converter;

import br.unioeste.geral.ordemservico.bo.tiposervico.TipoServico;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class TipoServicoConverter {
    private final NumberFormat formatadorMoeda;

    public TipoServicoConverter() {
        formatadorMoeda = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));
    }

    public String converterTipoServicoParaString(TipoServico tipoServico) {
        String mensagem = "O tipo de serviço solicitado é %s, com valor de referência de %s ";

        return String.format(
                mensagem,
                tipoServico.getNome(),
                formatadorMoeda.format(tipoServico.getValorReferencia())
        );
    }

    public String converterTipoServicosParaString(List<TipoServico> tipoServicos) {
        String tipoServicoMensagem = """
            ID: %d
            Nome: %s
            Valor de referência: %s
        """;

        String mensagem = """
        Abaixo estão listados todos os tipos de serviços cadastrados no sistema
        
        %s
        """;

        String tipoServicosMensagem = tipoServicos
                .stream()
                .map(tipoServico -> String.format(
                        tipoServicoMensagem,
                        tipoServico.getId(),
                        tipoServico.getNome(),
                        formatadorMoeda.format(tipoServico.getValorReferencia())
                ))
                .collect(Collectors.joining("\n"));

        return String.format(mensagem, tipoServicosMensagem);
    }
}
