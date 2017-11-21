import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class Tanque {
	private double x,y;
	private double aux_x,aux_y;
	private double angulo;
	private double velocidade;
	private Color cor;
	private boolean estaAtivo;
	private int pontosVida;
	private int maxPontosVida;
	private int pontosDisparos;
	private int maxPontosDisparos;
	private int id;
	private String mensagem;
	private String nome;
	
	private Random random;
	private  Disparo[] disparos;
	private Disparo removerDisparo;
	
	public Tanque() {
		
	}
	
	public Tanque(int x, int y, int a, Color cor){
		this.x = x;
		this.y = y;
		this.angulo = a;
		this.cor = cor;
		velocidade = 1;
		this.estaAtivo = false;
		random = new Random();
		pontosVida = 3;
		disparos = new Disparo[3];
		pontosDisparos = 3;
		id = Arena.id++;
		nome = "Rodrigo";
		maxPontosDisparos = 3;
		maxPontosVida = 3;
	}

	public void copia(Tanque t) {
		this.x = t.x;
		this.y = t.y;
		this.angulo = t.angulo;
		this.velocidade = t.velocidade;
		this.cor = t.cor;
		this.disparos = t.disparos;
		this.pontosVida = t.pontosVida;
		this.id = t.id;
	}
	
	public void aumentarVelocidade(){
		if(velocidade < 2)
			velocidade+=0.2;
	}
	public void diminuirVelocidade() {
		if(velocidade > -2)
			velocidade-=0.2;
	}
	public void girarHorario(int a){
		angulo += a;
		if(angulo > 360)
			angulo -= 360;
	}
	public void girarAntiHorario(int a){
		angulo -= a;
		if(angulo < 0)
			angulo = 360 - a;
	}
	
	public void mover(){
		aux_x = x + Math.sin(Math.toRadians(angulo)) * velocidade;
		aux_y = y - Math.cos(Math.toRadians(angulo)) * velocidade;
		
		if(aux_x <= Arena.largura - 20 && aux_x >= 20 && aux_y <= Arena.altura - 24 && aux_y >= 24 && estaAtivo) {
			x = aux_x;
			y = aux_y;
		} else {
			if (x<=30 ){
				if(angulo >= 270 && angulo < 360) angulo = 360 - angulo;
				if(angulo > 180 && angulo <= 270) angulo = 360 - angulo;
				if(velocidade < 0){
					velocidade *= -1;
					girarHorario(5);
				}
				
			}
			if (y<=30){
				if(angulo > 270 && angulo <= 360) angulo = 360 - angulo + 180;
				if(angulo >= 0 && angulo < 90) angulo = 360 - angulo - 180;
				if(velocidade < 0){
					velocidade *= -1;
				}
			}
			if (y>=Arena.altura - 30){
				if(angulo > 90 && angulo < 180) angulo = 360 - angulo - 180;
				if(angulo >= 180 && angulo < 270) angulo = 360 - angulo + 180;
				if(velocidade < 0){
					velocidade *= -1;
					girarAntiHorario(5);
				}
				
			}
			if (x>=Arena.largura - 30){
				if(angulo > 0 && angulo <= 90) angulo= 360 - angulo;
				if(angulo >= 90 && angulo < 180) angulo= 360 - angulo;
				if(velocidade < 0){
					velocidade *= -1;
				}
			}
			x += Math.sin(Math.toRadians(angulo)) * velocidade;
			y -= Math.cos(Math.toRadians(angulo)) * velocidade;
			x = (double)Math.round(x * 1000d) / 1000d;
			y = (double)Math.round(y * 1000d) / 1000d;
		}
	}
	public void setEstaAtivo(boolean estaAtivo){
		this.estaAtivo = estaAtivo;
	}
	
	public boolean getEstaAtivo() {
		return estaAtivo;
	}
	
	public void draw(Graphics2D g2d){
		//Armazenamos o sistema de coordenadas original.
		AffineTransform antes = g2d.getTransform();
		//Criamos um sistema de coordenadas para o tanque.
		AffineTransform depois = new AffineTransform();
		depois.translate(x, y);
		depois.rotate(Math.toRadians(angulo));
		
		for(int i = 0; i < 3; i++)
			if(disparos[i] != null) {
				disparos[i].mover();
				disparos[i].draw(g2d);
				if(disparos[i].remover()) {
					disparos[i] = null;
					pontosDisparos++;
				}
			}
		
		//Aplicamos o sistema de coordenadas.
		g2d.transform(depois);
		
		//Desenha as esteiras
		g2d.setColor(Color.BLACK);
		g2d.fillRect(-15, -12, 30, 24);
		
		//Desenhamos o corpo do tanque.
		g2d.setColor(cor);
		g2d.fillRect(-10, -12, 20, 24);
		//Agora as esteiras
		for(int i = -12; i <= 8; i += 4){
			g2d.setColor(Color.LIGHT_GRAY);
			g2d.fillRect(-15, i, 5, 4);
			g2d.fillRect(10, i, 5, 4);
			g2d.setColor(Color.BLACK);
			g2d.fillRect(-15, i, 5, 4);
			g2d.fillRect(10, i, 5, 4);
		}
		
		//Desenha o canhão
		g2d.setColor(Color.LIGHT_GRAY);
		g2d.fillRect(-3, -25, 6, 25);
		g2d.setColor(cor);
		g2d.drawRect(-3, -25, 6, 25);
		
		//Se o tanque estiver ativo
		//Desenhamos uma margem
		if(estaAtivo){
			g2d.setColor(new Color(120,120,120));
			Stroke linha = g2d.getStroke();
			g2d.setStroke(new BasicStroke(1f,BasicStroke.CAP_ROUND,
										  BasicStroke.JOIN_ROUND,0,
										  new float[]{8},0));
			g2d.drawRect(-24, -32, 48, 55);
			g2d.setStroke(linha);
		}
		//Aplicamos o sistema de coordenadas
		g2d.setTransform(antes);
		
		// Imprime na tela algumas informações para depuração
		if(estaAtivo) {
			int l = 10;
			//g2d.drawString("Angulo: " + (int)angulo + "", 25, l+=15);
			g2d.drawString("Velocidade: " + String.format("%.1f", velocidade), 25, l+=15);
			//g2d.drawString("Posição X: " + String.format("%f", x), 25, l+=15);
			//g2d.drawString("Posição Y: " + String.format("%f", y), 25, l+=15);
			g2d.drawString("Disparos: " + pontosDisparos + "/3", 25, l+=15);
			g2d.drawString("Pontos de Vida: " + pontosVida, 25, l+=15);
		}
		
		
		// Desenha a barra de vida
		for(int i = 0; i <= pontosVida * 4; i += 6){
			g2d.setColor(Color.RED);
			g2d.fillRect((int)x + 30, (int)y + 20 - i, 6, 6);
		}
		g2d.setColor(Color.BLACK);
		g2d.drawRect((int)x + 30, (int)y + 8, 6, 18);
		
		// Desenha a barra de disparos
		for(int i = 0; i < pontosDisparos * 5; i += 6){
			g2d.setColor(Color.BLUE);
			g2d.fillRect((int)x + 38, (int)y + 20 - i, 6, 6);
		}
		g2d.setColor(Color.BLACK);
		g2d.drawRect((int)x + 38, (int)y + 8, 6, 18);
	}
	
	public Shape getRectEnvolvente(){
		AffineTransform at = new AffineTransform();
		at.translate(x,y);
		at.rotate(Math.toRadians(angulo));
		Rectangle rect = new Rectangle(-24,-32,48,55);
		return at.createTransformedShape(rect);
	}
	
	public void disparar() {
		if(pontosDisparos > 0) {
			for(int i = 0; i < 3; i++)
				if(disparos[i] == null) {
					disparos[i] = new Disparo(x, y, angulo);
					pontosDisparos--;
					break;
				}
		}
	}
	
	public boolean acertou(Tanque inimigo) {
		for(int i = 0; i < 3; i++)
			if(disparos[i] != null) {
				if(disparos[i].acertou(inimigo.x, inimigo.y)) {
					disparos[i] = null;
					pontosDisparos++;
					return true;
				}
			}
		return false;
	}
	
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getAngulo() {
		return angulo;
	}

	public void setAngulo(double angulo) {
		this.angulo = angulo;
	}

	public double getVelocidade() {
		return velocidade;
	}

	public void setVelocidade(double velocidade) {
		this.velocidade = velocidade;
	}

	public Color getCor() {
		return cor;
	}

	public void setCor(Color cor) {
		this.cor = cor;
	}

	public int getPontosDeVida() {
		return pontosVida;
	}

	public void subtraiPontoDeVida() {
		if(pontosVida > 0)
			pontosVida--;
	}

	public void adicionaPontoDeDisparos() {
		if(pontosDisparos < maxPontosDisparos)
			pontosDisparos++;
	}

	public void subtraiPontoDeDisparos() {
		if(pontosDisparos > 0)
			pontosDisparos--;
	}
	
	public int getPontosVida() {
		return pontosVida;
	}

	public void setPontosDeVida(int pontosVida) {
		this.pontosVida = pontosVida;
	}

	public int getMaxPontosVida() {
		return maxPontosVida;
	}

	public void setMaxPontosVida(int maxPontosVida) {
		this.maxPontosVida = maxPontosVida;
	}

	public int getPontosDeDisparos() {
		return pontosDisparos;
	}

	public void setPontosDeDisparos(int pontosDisparos) {
		this.pontosDisparos = pontosDisparos;
	}

	public int getMaxPontosDisparos() {
		return maxPontosDisparos;
	}

	public void setMaxPontosDisparos(int maxPontosDisparos) {
		this.maxPontosDisparos = maxPontosDisparos;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Disparo[] getDisparos() {
		return disparos;
	}

	public void setDisparos(Disparo[] disparos) {
		this.disparos = disparos;
	}
}