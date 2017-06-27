package br.com.caelum.leilao.infra.dao;

import br.com.caelum.leilao.dominio.Leilao;

public interface Carteiro {

	public void envia(Leilao leilao);
}
