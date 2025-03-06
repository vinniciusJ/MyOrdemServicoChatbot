package br.unioeste.geral.ordemservico.chatbot.converter;

import br.unioeste.geral.ordemservico.bo.cliente.Cliente;
import br.unioeste.geral.ordemservico.bo.funcionario.Funcionario;

import java.util.List;
import java.util.stream.Collectors;

public class FuncionarioConverter {
    private final EnderecoEspecificoConverter enderecoEspecificoConverter;
    private final EmailConverter emailConverter;
    private final TelefoneConverter telefoneConverter;

    public FuncionarioConverter() {
        enderecoEspecificoConverter = new EnderecoEspecificoConverter();
        emailConverter = new EmailConverter();
        telefoneConverter = new TelefoneConverter();
    }

    public String converterFuncionarioParaString(Funcionario funcionario){
        String mensagem = """
        O funcionário solicitado é o(a) %s, portador(a) do CPF %s. O endereço cadastrado para ele(a) é %s.
        
        E-mail(s) para contato: %s
        Telefone(s) para contato: %s
        """;

        return String.format(
                mensagem,
                funcionario.getNome(),
                funcionario.getCpf(),
                enderecoEspecificoConverter.converterEnderecoEspecificoParaString(funcionario.getEndereco()),
                emailConverter.converterEmailsParaString(funcionario.getEmails()),
                telefoneConverter.converterTelefonesParaString(funcionario.getTelefones())
        );
    }

    public String converterFuncionariosParaString(List<Funcionario> funcionarios){
        String funcionarioMensagem = """
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

        String funcionarioMensagens = funcionarios
                .stream()
                .map(funcionario ->
                        String.format(
                                funcionarioMensagem,
                                funcionario.getId(),
                                funcionario.getNome(),
                                funcionario.getCpf(),
                                enderecoEspecificoConverter.converterEnderecoEspecificoParaString(funcionario.getEndereco()),
                                emailConverter.converterEmailsParaString(funcionario.getEmails()),
                                telefoneConverter.converterTelefonesParaString(funcionario.getTelefones())
                        )
                )
                .collect(Collectors.joining("\n"));

        return String.format(mensagem, funcionarioMensagens);
    }
}
