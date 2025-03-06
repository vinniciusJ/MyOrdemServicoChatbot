package br.unioeste.geral.ordemservico.chatbot.converter;

import br.unioeste.geral.ordemservico.bo.cliente.Cliente;
import br.unioeste.geral.ordemservico.bo.funcionario.Funcionario;

import java.util.List;
import java.util.stream.Collectors;

public class ClienteConverter {
    private final EnderecoEspecificoConverter enderecoEspecificoConverter;
    private final EmailConverter emailConverter;
    private final TelefoneConverter telefoneConverter;

    public ClienteConverter() {
        enderecoEspecificoConverter = new EnderecoEspecificoConverter();
        emailConverter = new EmailConverter();
        telefoneConverter = new TelefoneConverter();
    }

    public String converterClienteParaString(Cliente cliente){
        String mensagem = """
        ID: %d
        Primeiro nome: %s
        Nome do meio: %s
        Sobrenome: %s
        CPF: %s
        Endereco: %s
        Email(s): %s
        Telefone(s): %s
        """;

        return String.format(
                mensagem,
                cliente.getId(),
                cliente.getPrimeiroNome(),
                cliente.getNomeDoMeio(),
                cliente.getUltimoNome(),
                cliente.getCpf(),
                enderecoEspecificoConverter.converterEnderecoEspecificoParaString(cliente.getEndereco()),
                emailConverter.converterEmailsParaString(cliente.getEmails()),
                telefoneConverter.converterTelefonesParaString(cliente.getTelefones())
        );
    }

    public String converterClientesParaString(List<Cliente> clientes){
        return clientes.stream().map(this::converterClienteParaString).collect(Collectors.joining("\n"));
    }
}
