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
                funcionario.getId(),
                funcionario.getPrimeiroNome(),
                funcionario.getNomeDoMeio(),
                funcionario.getUltimoNome(),
                funcionario.getCpf(),
                enderecoEspecificoConverter.converterEnderecoEspecificoParaString(funcionario.getEndereco()),
                emailConverter.converterEmailsParaString(funcionario.getEmails()),
                telefoneConverter.converterTelefonesParaString(funcionario.getTelefones())
        );
    }

    public String converterFuncionariosParaString(List<Funcionario> funcionarios){
        return funcionarios.stream().map(this::converterFuncionarioParaString).collect(Collectors.joining("\n"));
    }
}
