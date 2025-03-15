package br.unioeste.geral.ordemservico.chatbot.service;

import br.unioeste.geral.ordemservico.chatbot.dto.Intencao;
import br.unioeste.geral.ordemservico.chatbot.dto.RespostaIA;
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
    private final RespostaIA<Void> respostaPadrao;

    private final ObjectMapper objectMapper;

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

        respostaPadrao = new RespostaIA<>(
                null,
                null,
                "Sinto muito, não entendi o que você deseja saber sobre o sistema de gerenciamento de ordens de serviço"
        );

        objectMapper = new ObjectMapper();
    }

    public RespostaIA<?> obterResposta(String mensagem) throws Exception {
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
                "name", Intencao.OBTER_CLIENTE_POR_ID,
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
                "name", Intencao.OBTER_FUNCIONARIO_POR_ID,
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
                "name", Intencao.OBTER_TIPO_SERVICO_POR_ID,
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
                "name", Intencao.OBTER_ORDEM_SERVICO_POR_NUMERO,
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
                "name", Intencao.OBTER_CLIENTES,
                "description", "Retorna todos os clientes cadastrados no sistema, mostrando um resumo das informações de cada um"
        );

        Map<String, Object> obterFuncionarios = Map.of(
                "name", Intencao.OBTER_FUNCIONARIOS,
                "description", "Retorna todos os funcionários cadastrados no sistema, mostrando um resumo das informações de cada um"
        );

        Map<String, Object> obterTipoServicos = Map.of(
                "name", Intencao.OBTER_TIPOS_SERVICO,
                "description", "Retorna todos os tipos de serviços cadastrados no sistema, mostrando um resumo das informações de cada um"
        );

        Map<String, Object> obterOrdensServicos = Map.of(
                "name", Intencao.OBTER_ORDEM_SERVICOS,
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

    private RespostaIA<?> processarRespostaIA(String resposta) throws Exception {
        JsonNode noRaiz = objectMapper.readTree(resposta);

        if (noRaiz.has("candidates")) {
            return lidarRespostasCandidatas(noRaiz.path("candidates"));
        }

        return new RespostaIA<>(null, null, resposta);
    }

    private RespostaIA<?> lidarRespostasCandidatas(JsonNode nosCandidatos) throws Exception{
        if(nosCandidatos.isArray() && !nosCandidatos.isEmpty()){
            JsonNode noConteudo = nosCandidatos.get(0).path("content");

            if(noConteudo.has("parts") && noConteudo.path("parts").isArray()){
                return lidarPartesResposta(noConteudo.path("parts"));
            }
        }

        return respostaPadrao;
    }

    private RespostaIA<?> lidarPartesResposta(JsonNode partesResposta) throws Exception {
        if(!partesResposta.isEmpty()){
            JsonNode primeiraParte = partesResposta.get(0);

            if (primeiraParte.has("functionCall")) {
                JsonNode functionCallNode = primeiraParte.path("functionCall");
                return realizarChamadaServico(functionCallNode);
            } else if (primeiraParte.has("text")) {
                String resposta = primeiraParte.path("text").asText();

                return new RespostaIA<>(null, null, resposta);
            }
        }

        return respostaPadrao;
    }

    private RespostaIA<?> realizarChamadaServico(JsonNode noMetodoServico) throws Exception {
        String servico = noMetodoServico.path("name").asText();
        JsonNode argumentos = noMetodoServico.path("args");

        if(servico.equals(Intencao.OBTER_CLIENTE_POR_ID.toString())){
            return criarRespostaParaIntencaoObterClientePorID(argumentos);
        }

        if(servico.equals(Intencao.OBTER_FUNCIONARIO_POR_ID.toString())){
            return criarRespostaParaIntencaoObterFuncionarioPorID(argumentos);
        }

        if(servico.equals(Intencao.OBTER_TIPO_SERVICO_POR_ID.toString())){
            return criarRespostaParaIntencaoObterTipoServicoPorID(argumentos);
        }

        if(servico.equals(Intencao.OBTER_ORDEM_SERVICO_POR_NUMERO.toString())){
            return criarRespostaParaIntencaoObterOrdemServicoPorNumero(argumentos);
        }

        if(servico.equals(Intencao.OBTER_CLIENTES.toString())){
            return criarRespostaParaIntencaoObterClientes();
        }

        if(servico.equals(Intencao.OBTER_FUNCIONARIOS.toString())){
            return criarRespostaParaIntencaoObterFuncionarios();
        }

        if(servico.equals(Intencao.OBTER_TIPOS_SERVICO.toString())){
            return criarRespostaParaIntencaoObterTiposServicos();
        }

        if(servico.equals(Intencao.OBTER_ORDEM_SERVICOS.toString())){
            return criarRespostaParaIntencaoObterOrdemServicos();
        }

        return respostaPadrao;
    }

    private RespostaIA<Void> criarRespostaParaIntencaoObterClientes(){
        return new RespostaIA<>(Intencao.OBTER_CLIENTES, null, null);
    }

    private RespostaIA<Void> criarRespostaParaIntencaoObterFuncionarios(){
        return new RespostaIA<>(Intencao.OBTER_FUNCIONARIOS, null, null);
    }

    private RespostaIA<Void> criarRespostaParaIntencaoObterTiposServicos(){
        return new RespostaIA<>(Intencao.OBTER_TIPOS_SERVICO, null, null);
    }

    private RespostaIA<Void> criarRespostaParaIntencaoObterOrdemServicos(){
        return new RespostaIA<>(Intencao.OBTER_ORDEM_SERVICOS, null, null);
    }

    private RespostaIA<Long> criarRespostaParaIntencaoObterClientePorID(JsonNode argumentos){
        long idCliente = argumentos.path("idCliente").asLong();

        return new RespostaIA<>(Intencao.OBTER_ORDEM_SERVICOS, idCliente, null);
    }

    private RespostaIA<Long> criarRespostaParaIntencaoObterFuncionarioPorID(JsonNode argumentos){
        long idFuncionario = argumentos.path("idFuncionario").asLong();

        return new RespostaIA<>(Intencao.OBTER_ORDEM_SERVICOS, idFuncionario, null);
    }

    private RespostaIA<Long> criarRespostaParaIntencaoObterTipoServicoPorID(JsonNode argumentos){
        long idTipoServico = argumentos.path("idTipoServico").asLong();

        return new RespostaIA<>(Intencao.OBTER_ORDEM_SERVICOS, idTipoServico, null);
    }

    private RespostaIA<String> criarRespostaParaIntencaoObterOrdemServicoPorNumero(JsonNode argumentos){
        String numeroOrdemServico = argumentos.path("numeroOrdemServico").asText();

        return new RespostaIA<>(Intencao.OBTER_ORDEM_SERVICOS, numeroOrdemServico, null);
    }
}
