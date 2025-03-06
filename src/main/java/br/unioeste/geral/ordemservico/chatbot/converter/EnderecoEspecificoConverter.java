package br.unioeste.geral.ordemservico.chatbot.converter;

import br.unioeste.geral.endereco.bo.cidade.Cidade;
import br.unioeste.geral.endereco.bo.endereco.Endereco;
import br.unioeste.geral.endereco.bo.enderecoespecifico.EnderecoEspecifico;
import br.unioeste.geral.endereco.bo.logradouro.Logradouro;

public class EnderecoEspecificoConverter {
    public String converterEnderecoEspecificoParaString(EnderecoEspecifico enderecoEspecifico){
        Endereco endereco = enderecoEspecifico.getEndereco();
        Logradouro logradouro = endereco.getLogradouro();
        Cidade cidade = endereco.getCidade();

        String mensagem = "%s %s, %s, %s - %s, %s - %s, %s";

        return String.format(
                mensagem,
                logradouro.getTipoLogradouro().getSigla(),
                logradouro.getNome(),
                enderecoEspecifico.getNumeroEndereco(),
                enderecoEspecifico.getComplementoEndereco(),
                endereco.getBairro().getNome(),
                cidade.getNome(),
                cidade.getUnidadeFederativa().getSigla(),
                endereco.getCep()
        );
    }
}
