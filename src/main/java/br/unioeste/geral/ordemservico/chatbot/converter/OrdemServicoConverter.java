package br.unioeste.geral.ordemservico.chatbot.converter;

import br.unioeste.geral.ordemservico.bo.ordemservico.OrdemServico;
import br.unioeste.geral.ordemservico.bo.tiposervico.TipoServico;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class OrdemServicoConverter {
    private final ServicoConverter servicoConverter;

    public OrdemServicoConverter() {
        servicoConverter = new ServicoConverter();
    }

    public String converterOrdemServicoParaString(OrdemServico ordemServico) {
        NumberFormat formatadorMoeda = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));
        DateTimeFormatter formatadorData = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        String mensagem = """
        Número: %s
        Data de emissão: %s
        Descrição: %s
        Cliente: %s
        Funcionário Responsável: %s
        Serviços realizados:
            %s
        Valor: %s
        """;

        return String.format(
                mensagem,
                ordemServico.getNumero(),
                ordemServico.getDataEmissao().format(formatadorData),
                ordemServico.getDescricao(),
                ordemServico.getCliente().getNome(),
                ordemServico.getFuncionarioResponsavel().getNome(),
                servicoConverter.converterServicosParaString(ordemServico.getServicosRealizados()),
                formatadorMoeda.format(0.0)
        );
    }

    public String converterOrdemServicosParaString(List<OrdemServico> ordemServicos) {
        return ordemServicos.stream().map(this::converterOrdemServicoParaString).collect(Collectors.joining("\n"));
    }
}
