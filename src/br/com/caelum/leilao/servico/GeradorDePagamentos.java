package br.com.caelum.leilao.servico;

import java.util.Calendar;
import java.util.List;

import br.com.caelum.infra.Relogio;
import br.com.caelum.infra.RelogioDoSistema;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.dominio.Pagamento;
import br.com.caelum.leilao.infra.dao.RepositorioDeLeiloes;
import br.com.caelum.leilao.infra.dao.RepositorioDePagamentos;

public class GeradorDePagamentos {

	private Avaliador avaliador;
	private RepositorioDePagamentos pagamentos;
	private RepositorioDeLeiloes leiloes;
	private Relogio relogio;

	public GeradorDePagamentos(RepositorioDeLeiloes leiloes,
			RepositorioDePagamentos pagamentos,
			Avaliador avaliador){
		this.leiloes = leiloes;
		this.pagamentos = pagamentos;
		this.avaliador = avaliador;
		this.relogio = new RelogioDoSistema();
	}

	public GeradorDePagamentos(RepositorioDeLeiloes leiloes, 
			RepositorioDePagamentos pagamentos, 
			Avaliador avaliador, 
			Relogio relogio) {
		this.leiloes = leiloes;
		this.pagamentos = pagamentos;
		this.avaliador = avaliador;
		this.relogio = relogio;
	}
	public void gera(){
		List<Leilao> leiloesEncerrados = leiloes.encerrados();
		for(Leilao leilao : leiloesEncerrados){
			avaliador.avalia(leilao);
			Pagamento novoPagamento = new Pagamento(avaliador.getMaiorLance(), primeiroDiaUtil());
			pagamentos.salvar(novoPagamento);
		}

	}

	private Calendar primeiroDiaUtil() {

		Calendar data = relogio.hoje();
		int diaDaSemana = data.get(Calendar.DAY_OF_WEEK);

		if(diaDaSemana == Calendar.SATURDAY) data.add(Calendar.DAY_OF_MONTH, 2); 
		else if(diaDaSemana == Calendar.SUNDAY) data.add(Calendar.DAY_OF_MONTH, 1); 

		return data;
	}

}
