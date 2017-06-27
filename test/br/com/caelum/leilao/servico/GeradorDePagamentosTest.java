package br.com.caelum.leilao.servico;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Calendar;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import br.com.caelum.infra.Relogio;
import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.dominio.Pagamento;
import br.com.caelum.leilao.dominio.Usuario;
import br.com.caelum.leilao.infra.dao.RepositorioDeLeiloes;
import br.com.caelum.leilao.infra.dao.RepositorioDePagamentos;

public class GeradorDePagamentosTest {

	@Test
	public void deveGerarPagamentoParaUmLeilaoEncerrado() {
		
		RepositorioDeLeiloes leiloes = mock(RepositorioDeLeiloes.class);
		RepositorioDePagamentos pagamentos = mock(RepositorioDePagamentos.class);
		Relogio relogio = mock(Relogio.class);
		
		
		Leilao leilao = new CriadorDeLeilao()
	            .para("Playstation")
	            .lance(new Usuario("José da Silva"), 2000.0)
	            .lance(new Usuario("Maria Pereira"), 2500.0)
	            .constroi();
		
		 when(leiloes.encerrados()).thenReturn(Arrays.asList(leilao));
	   //  when(avaliador.getMaiorLance()).thenReturn(2500.0);
		 Calendar domingo = Calendar.getInstance();
	        domingo.set(2012, Calendar.APRIL, 8);
		 when(relogio.hoje()).thenReturn(domingo);
		 
	     GeradorDePagamentos gerador = new GeradorDePagamentos(leiloes, pagamentos, new Avaliador(),relogio);
	     gerador.gera();
	     
	     ArgumentCaptor<Pagamento> argumento = ArgumentCaptor.forClass(Pagamento.class);
	     verify(pagamentos).salvar(argumento.capture());
	     
	     Pagamento pagamentoGerado = argumento.getValue();
	     assertEquals(2500.0, pagamentoGerado.getValor(), 0.00001);
	     
	     assertEquals(Calendar.MONDAY, 
	             pagamentoGerado.getData().get(Calendar.DAY_OF_WEEK));
	         assertEquals(9, 
	             pagamentoGerado.getData().get(Calendar.DAY_OF_MONTH));
	}

}
