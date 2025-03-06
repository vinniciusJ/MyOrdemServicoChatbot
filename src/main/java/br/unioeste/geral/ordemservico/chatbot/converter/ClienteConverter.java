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
        O cliente solicitado é o(a) %s, portador(a) do CPF %s. O endereço cadastrado para ele(a) é %s.
        
        E-mail(s) para contato: %s
        Telefone(s) para contato: %s
        """;

        return String.format(
                mensagem,
                cliente.getNome(),
                cliente.getCpf(),
                enderecoEspecificoConverter.converterEnderecoEspecificoParaString(cliente.getEndereco()),
                emailConverter.converterEmailsParaString(cliente.getEmails()),
                telefoneConverter.converterTelefonesParaString(cliente.getTelefones())
        );
    }

    public String converterClientesParaString(List<Cliente> clientes){
        String clienteMensagem = """
            ID: %d
            Nome: %s
            CPF: %s
            Endereco: %s
            Email(s): %s
            Telefone(s): %s
        """;

        String mensagem = """
        Abaixo estão listados todos os clientes cadastrados no sistema:
        
        %s
        """;

        String clientesMensagem = clientes
                .stream()
                .map(cliente ->
                        String.format(
                                clienteMensagem,
                                cliente.getId(),
                                cliente.getNome(),
                                cliente.getCpf(),
                                enderecoEspecificoConverter.converterEnderecoEspecificoParaString(cliente.getEndereco()),
                                emailConverter.converterEmailsParaString(cliente.getEmails()),
                                telefoneConverter.converterTelefonesParaString(cliente.getTelefones())
                        )
                )
                .collect(Collectors.joining("\n"));

        return String.format(mensagem, clientesMensagem);
    }
}

