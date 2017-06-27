package br.com.caelum.leilao.servico;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

import br.com.caelum.leilao.builder.CriadorDeLeilao;
import br.com.caelum.leilao.dominio.Leilao;
import br.com.caelum.leilao.infra.dao.Carteiro;
import br.com.caelum.leilao.infra.dao.LeilaoDao;
import br.com.caelum.leilao.infra.dao.LeilaoDaoFalso;
import br.com.caelum.leilao.infra.dao.RepositorioDeLeiloes;

import static org.mockito.Mockito.*;

public class EncerradorDeLeilaoTest {

	@Test
	public void deveEncerrarLeiloesQueIniciaramSemanaPassada(){

		Calendar antiga = Calendar.getInstance();

		antiga.set(1999, 1,1);

		Leilao leilao1 = new CriadorDeLeilao().para("Tv Plasma 42").naData(antiga).constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("Tv Plasma 32").naData(antiga).constroi();
		List<Leilao> leiloesAntigos = Arrays.asList(leilao1,leilao2);
		RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
		// ensinando o mock a reagir da maneira que esperamos!
		when(daoFalso.correntes()).thenReturn(leiloesAntigos);

		Carteiro carteiro = mock(Carteiro.class);

		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso,carteiro);
		encerrador.encerra();

		assertTrue(leilao1.isEncerrado());
		assertTrue(leilao2.isEncerrado());
		assertEquals(2, encerrador.getTotalEncerrados());
	}
	@Test
	public void naoEncerraLelioesQueComecaramOntem(){
		Calendar antiga = Calendar.getInstance();

		antiga.set(2017, 6,26);

		Leilao leilao1 = new CriadorDeLeilao().para("Tv Plasma 42").naData(antiga).constroi();
		Leilao leilao2 = new CriadorDeLeilao().para("Tv Plasma 32").naData(antiga).constroi();
		List<Leilao> leiloesAntigos = Arrays.asList(leilao1,leilao2);
		RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
		// ensinando o mock a reagir da maneira que esperamos!
		when(daoFalso.correntes()).thenReturn(leiloesAntigos);


		Carteiro carteiro = mock(Carteiro.class);
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso,carteiro);
		encerrador.encerra();

		assertFalse(leilao1.isEncerrado());
		assertFalse(leilao2.isEncerrado());
		assertEquals(0, encerrador.getTotalEncerrados());


	}
	@Test
	public void deveAtualizarLeiloesEncerrados(){
		
		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 1,20);
		Leilao leilao1 = new CriadorDeLeilao().para("Tv Plasma 42").naData(antiga).constroi();
		
		RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
		Carteiro carteiro = mock(Carteiro.class);
		
		when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1));
		
		EncerradorDeLeilao encerrador = new EncerradorDeLeilao(daoFalso,carteiro);
		encerrador.encerra();
		
		verify(daoFalso,times(1)).atualiza(leilao1);
	}
	 @Test
	    public void deveContinuarAExecucaoMesmoQuandoDaoFalha() {
	        Calendar antiga = Calendar.getInstance();
	        antiga.set(1999, 1, 20);

	        Leilao leilao1 = new CriadorDeLeilao().para("TV de plasma")
	            .naData(antiga).constroi();
	        Leilao leilao2 = new CriadorDeLeilao().para("Geladeira")
	            .naData(antiga).constroi();

	        RepositorioDeLeiloes daoFalso = mock(RepositorioDeLeiloes.class);
	        when(daoFalso.correntes()).thenReturn(Arrays.asList(leilao1, leilao2));

	        doThrow(new RuntimeException()).when(daoFalso).atualiza(leilao1);

	        Carteiro carteiroFalso = mock(Carteiro.class);
	        EncerradorDeLeilao encerrador = 
	            new EncerradorDeLeilao(daoFalso, carteiroFalso);

	        encerrador.encerra();

	        verify(daoFalso).atualiza(leilao2);
	        verify(carteiroFalso).envia(leilao2);
	    }
}
