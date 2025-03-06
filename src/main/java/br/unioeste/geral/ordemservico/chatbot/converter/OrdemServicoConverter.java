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
    private final NumberFormat formatadorMoeda;
    private final DateTimeFormatter formatadorData;

    public OrdemServicoConverter() {
        servicoConverter = new ServicoConverter();

        formatadorMoeda = NumberFormat.getCurrencyInstance(Locale.of("pt", "BR"));
        formatadorData = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    }

    public String converterOrdemServicoParaString(OrdemServico ordemServico) {
        String mensagem = """
        A ordem de serviço solicitada n° %s, emitada em %s com o valor total de %s, tem o seguinte problema: %s.
        
        Cliente: %s
        Funcionário responsável: %s
        
        Serviços realizados:
        %s
        """;

        return String.format(
                mensagem,
                ordemServico.getNumero(),
                ordemServico.getDataEmissao().format(formatadorData),
                formatadorMoeda.format(ordemServico.getValorTotal()),
                ordemServico.getDescricao(),
                ordemServico.getCliente().getNome(),
                ordemServico.getFuncionarioResponsavel().getNome(),
                servicoConverter.converterServicosParaString(ordemServico.getServicosRealizados())
        );
    }

    public String converterOrdemServicosParaString(List<OrdemServico> ordemServicos) {
        String ordemServicoMensagem = """
        Número: %s
        Data de emissão: %s
        Descrição: %s
        Cliente: %s
        Funcionário Responsável: %s
        Serviços realizados:
        %s
        Valor: %s
        """;

        String ordemServicosMensagem = ordemServicos
                .stream()
                .map(ordemServico ->
                        String.format(
                                ordemServicoMensagem,
                                ordemServico.getNumero(),
                                ordemServico.getDataEmissao().format(formatadorData),
                                ordemServico.getDescricao(),
                                ordemServico.getCliente().getNome(),
                                ordemServico.getFuncionarioResponsavel().getNome(),
                                servicoConverter.converterServicosParaString(ordemServico.getServicosRealizados()),
                                formatadorMoeda.format(ordemServico.getValorTotal())
                        )
                )
                .collect(Collectors.joining("\n"));

        String mensagem = """
        Abaixo estão listadas todas as ordens de serviço cadastradas no sistema:
        
        %s
        """;

        return String.format(mensagem, ordemServicosMensagem);
    }
}
