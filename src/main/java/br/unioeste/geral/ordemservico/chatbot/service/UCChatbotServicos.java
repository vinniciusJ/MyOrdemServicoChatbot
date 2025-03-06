package br.unioeste.geral.ordemservico.chatbot.service;

import br.unioeste.geral.ordemservico.bo.cliente.Cliente;
import br.unioeste.geral.ordemservico.bo.funcionario.Funcionario;
import br.unioeste.geral.ordemservico.bo.ordemservico.OrdemServico;
import br.unioeste.geral.ordemservico.bo.tiposervico.TipoServico;
import br.unioeste.geral.ordemservico.chatbot.converter.ClienteConverter;
import br.unioeste.geral.ordemservico.chatbot.converter.FuncionarioConverter;
import br.unioeste.geral.ordemservico.chatbot.converter.OrdemServicoConverter;
import br.unioeste.geral.ordemservico.chatbot.converter.TipoServicoConverter;
import br.unioeste.geral.ordemservico.servico.service.cliente.UCClienteServicos;
import br.unioeste.geral.ordemservico.servico.service.funcionario.UCFuncionarioServicos;
import br.unioeste.geral.ordemservico.servico.service.ordemservico.UCOrdemServicoServicos;
import br.unioeste.geral.ordemservico.servico.service.tiposervico.UCTipoServicoServicos;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class UCChatbotServicos {
    private final String geminiApiUrl;
    private final String geminiApiKey;

    private final String instrucaoInicial;
    private final String respostaPadrao;

    private final ObjectMapper objectMapper;

    private final UCClienteServicos clienteServicos;
    private final UCFuncionarioServicos funcionarioServicos;

    private final UCTipoServicoServicos tipoServicoServicos;
    private final UCOrdemServicoServicos ordemServicoServicos;

    private final ClienteConverter clienteConverter;
    private final FuncionarioConverter funcionarioConverter;
    private final TipoServicoConverter tipoServicoConverter;
    private final OrdemServicoConverter ordemServicoConverter;

    public UCChatbotServicos() {
        geminiApiUrl = System.getenv("GEMINI_API_URL");
        geminiApiKey = System.getenv("GEMINI_API_KEY");

        instrucaoInicial = """
        Você é o assistence virtual do sistema de gerenciamentos de ordens de serviço de uma empresa. Existem vários tipos de serviço que essa empresa presta
        desde manutenções elétricas à troca de peças de ar condicionado. Dessa forma, só responda perguntas relacionadas a isso e em português. Não assuma nada!
        Quando te perguntarem algo que você não pode responde, peça desculpas e diga o motivo de não poder responder. As coisas que você pode fazer são: mostrar
        os dados de uma ordem de serviço, mostrar todas as ordens de serviço, mostrar os dados de um tipo de serviço, mostrar todos os tipos de serviço, mostrar
        os serviços realizados de um tipo de serviço, mostrar os dados de um cliente, mostrar todos os clientes, mostrar os dados de um funcionario, mostrar todos
        os funcionarios.
        """;

        respostaPadrao = """
        Sinto muito, não entendi o que você deseja saber sobre o sistema de gerenciamento de ordens de serviço
        """;

        objectMapper = new ObjectMapper();

        clienteServicos = new UCClienteServicos();
        funcionarioServicos = new UCFuncionarioServicos();
        tipoServicoServicos = new UCTipoServicoServicos();
        ordemServicoServicos = new UCOrdemServicoServicos();

        clienteConverter = new ClienteConverter();
        funcionarioConverter = new FuncionarioConverter();
        tipoServicoConverter = new TipoServicoConverter();
        ordemServicoConverter = new OrdemServicoConverter();
    }

    public String enviarMensagem(String mensagem) throws Exception {
        return processarRespostaIA(enviarRequisicao(mensagem));
    }

    private String enviarRequisicao(String mensagem) throws IOException, InterruptedException {
        Map<String, Object> conteudoRequisicao = criarConteudoRequisicao(mensagem);
        
        ObjectMapper objectMapper = new ObjectMapper();
        String conteudoRequisicaoJSON = objectMapper.writeValueAsString(conteudoRequisicao);

        try(HttpClient clienteHttp = HttpClient.newHttpClient()) {
            HttpRequest requisicao = HttpRequest.newBuilder()
                    .uri(URI.create(geminiApiUrl + geminiApiKey))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(conteudoRequisicaoJSON))
                    .build();

            HttpResponse<String> resposta = clienteHttp.send(requisicao, HttpResponse.BodyHandlers.ofString());

            return resposta.body();
        }
    }

    private Map<String, Object> criarConteudoRequisicao(String messagem){
        Map<String, Object> obterClientePorID = Map.of(
                "name", "obterClientePorID",
                "description", "Retorna todas as informações de um cliente baseado (id/número/código) fornecido. Sempre que o usuário falar de um cliente e passar um número (identificador), essa função deve ser chamada.",
                "parameters", Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "idCliente", Map.of(
                                        "type", "integer",
                                        "description", "O identificador (id/número/código) do cliente para o qual você deseja obter os detalhes (informações)"
                                )
                        ),
                        "required", List.of("idCliente")
                )
        );

        Map<String, Object> obterFuncionarioPorID = Map.of(
                "name", "obterFuncionarioPorID",
                "description", "Retorna todas as informações de um funcionário baseado (id/número/código) fornecido. Sempre que o usuário falar de um funcionário e passar um número (identificador), essa função deve ser chamada.",
                "parameters", Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "idFuncionario", Map.of(
                                        "type", "integer",
                                        "description", "O identificador (id/número/código) do funcionário para o qual você deseja obter os detalhes (informações)"
                                )
                        ),
                        "required", List.of("idFuncionario")
                )
        );

        Map<String, Object> obterTipoServicoPorID = Map.of(
                "name", "obterTipoServicoPorID",
                "description", "Retorna todas as informações de um tipo de serviço baseado (id/número/código) fornecido. Sempre que o usuário falar de um tipo de serviço e passar um número (identificador), essa função deve ser chamada.",
                "parameters", Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "idTipoServico", Map.of(
                                        "type", "integer",
                                        "description", "O identificador (id/número/código) do tipo de serviço para o qual você deseja obter os detalhes (informações)"
                                )
                        ),
                        "required", List.of("idTipoServico")
                )
        );

        Map<String, Object> obterOrdemServicoPorNumero = Map.of(
                "name", "obterOrdemServicoPorNumero",
                "description", "Retorna todas as informações de um ordem de serviço baseado (id/número/código) fornecido. Sempre que o usuário falar de uma ordem de serviço e passar um número (identificador), essa função deve ser chamada.",
                "parameters", Map.of(
                        "type", "object",
                        "properties", Map.of(
                                "numeroOrdemServico", Map.of(
                                        "type", "string",
                                        "description", "O identificador (id/número/código) do tipo de serviço para o qual você deseja obter os detalhes (informações)"
                                )
                        ),
                        "required", List.of("numeroOrdemServico")
                )
        );

        Map<String, Object> obterClientes = Map.of(
                "name", "obterClientes",
                "description", "Retorna todos os clientes cadastrados no sistema, mostrando um resumo das informações de cada um"
        );

        Map<String, Object> obterFuncionarios = Map.of(
                "name", "obterFuncionarios",
                "description", "Retorna todos os funcionários cadastrados no sistema, mostrando um resumo das informações de cada um"
        );

        Map<String, Object> obterTipoServicos = Map.of(
                "name", "obterTipoServicos",
                "description", "Retorna todos os tipos de serviços cadastrados no sistema, mostrando um resumo das informações de cada um"
        );

        Map<String, Object> obterOrdensServicos = Map.of(
                "name", "obterOrdemServicos",
                "description", "Retorna todos as ordens de serviços cadastrados no sistema, mostrando um resumo das informações de cada um"
        );

        return Map.of(
                "contents", List.of(
                        Map.of(
                                "parts", List.of(
                                        Map.of("text", instrucaoInicial),
                                        Map.of("text", messagem)
                                )
                        )
                ),
                "tools", Map.of(
                        "function_declarations", List.of(
                                obterClientePorID,
                                obterFuncionarioPorID,
                                obterTipoServicoPorID,
                                obterOrdemServicoPorNumero,
                                obterClientes,
                                obterFuncionarios,
                                obterTipoServicos,
                                obterOrdensServicos
                        )
                )
        );
    }

    private String processarRespostaIA(String resposta) throws Exception {
        JsonNode noRaiz = objectMapper.readTree(resposta);

        if (noRaiz.has("candidates")) {
            return lidarRespostasCandidatas(noRaiz.path("candidates"));
        }

        return resposta;
    }

    private String lidarRespostasCandidatas(JsonNode nosCandidatos) throws Exception{
        if(nosCandidatos.isArray() && !nosCandidatos.isEmpty()){
            JsonNode noConteudo = nosCandidatos.get(0).path("content");

            if(noConteudo.has("parts") && noConteudo.path("parts").isArray()){
                return lidarPartesResposta(noConteudo.path("parts"));
            }
        }

        return respostaPadrao;
    }

    private String lidarPartesResposta(JsonNode partesResposta) throws Exception {
        if(!partesResposta.isEmpty()){
            JsonNode primeiraParte = partesResposta.get(0);

            if (primeiraParte.has("functionCall")) {
                JsonNode functionCallNode = primeiraParte.path("functionCall");
                return realizarChamadaServico(functionCallNode);
            } else if (primeiraParte.has("text")) {
                return primeiraParte.path("text").asText();
            }
        }

        return respostaPadrao;
    }

    private String realizarChamadaServico(JsonNode noMetodoServico) throws Exception {
        String nomeMetodo = noMetodoServico.path("name").asText();
        JsonNode argumentos = noMetodoServico.path("args");

        System.out.println(nomeMetodo);

        if(nomeMetodo.equals("obterClientePorID")){
            return obterClientePorID(argumentos);
        }

        if(nomeMetodo.equals("obterFuncionarioPorID")){
            return obterFuncionarioPorID(argumentos);
        }

        if(nomeMetodo.equals("obterTipoServicoPorID")){
            return obterTipoServicoPorID(argumentos);
        }

        if(nomeMetodo.equals("obterOrdemServicoPorNumero")){
            return obterOrdemServicoPorNumero(argumentos);
        }

        if(nomeMetodo.equals("obterClientes")){
            return obterClientes();
        }

        if(nomeMetodo.equals("obterFuncionarios")){
            return obterFuncionarios();
        }

        if(nomeMetodo.equals("obterTipoServicos")){
            return obterTipoServicos();
        }

        if(nomeMetodo.equals("obterOrdemServicos")){
            return obterOrdemServicos();
        }

        return respostaPadrao;
    }

    private String obterClientePorID(JsonNode argumentos) throws Exception{
        long idCliente = argumentos.path("idCliente").asLong();
        Cliente cliente = clienteServicos.obterClientePorID(idCliente);

        return clienteConverter.converterClienteParaString(cliente);
    }

    private String obterFuncionarioPorID(JsonNode argumentos) throws Exception{
        long idFuncionario = argumentos.path("idFuncionario").asLong();
        Funcionario funcionario = funcionarioServicos.obterFuncionarioPorID(idFuncionario);

        return funcionarioConverter.converterFuncionarioParaString(funcionario);
    }

    private String obterTipoServicoPorID(JsonNode argumentos) throws Exception {
        long idTipoServico = argumentos.path("idTipoServico").asLong();
        TipoServico tipoServico = tipoServicoServicos.obterTipoServicoPorID(idTipoServico);

        return tipoServicoConverter.converterTipoServicoParaString(tipoServico);
    }

    private String obterOrdemServicoPorNumero(JsonNode argumentos) throws Exception {
        String numeroOrdemServico = argumentos.path("numeroOrdemServico").asText();
        OrdemServico ordemServico = ordemServicoServicos.obterOrdemServicoPorNumero(numeroOrdemServico);

        return ordemServicoConverter.converterOrdemServicoParaString(ordemServico);
    }

    private String obterClientes() throws Exception {
        List<Cliente> clientes = clienteServicos.obterClientes();

        return clienteConverter.converterClientesParaString(clientes);
    }

    private String obterFuncionarios() throws Exception {
        List<Funcionario> funcionarios = funcionarioServicos.obterFuncionarios();

        return funcionarioConverter.converterFuncionariosParaString(funcionarios);
    }

    private String obterTipoServicos() throws Exception {
        List<TipoServico> tipoServicos = tipoServicoServicos.obterTiposServicos();

        return tipoServicoConverter.converterTipoServicosParaString(tipoServicos);
    }

    private String obterOrdemServicos() throws Exception {
        List<OrdemServico> ordemServicos = ordemServicoServicos.obterOrdemServicos();

        return ordemServicoConverter.converterOrdemServicosParaString(ordemServicos);
    }
}
