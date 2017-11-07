import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Random;

public class Tanque {
	protected double x,y;
	protected double aux_x,aux_y;
	private double angulo;
	private double velocidade;
	private Color cor;
	private boolean estaAtivo;
	protected int pontosVida;
	
	Random random;
	ArrayList<Disparo> disparos = new ArrayList<>();
	Disparo removerDisparo;
	
	public Tanque(int x, int y, int a, Color cor){
		this.x = x;
		this.y = y;
		this.angulo = a;
		this.cor = cor;
		velocidade = 1;
		this.estaAtivo = false;
		random = new Random();
		pontosVida = 3;
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
			if (y>=450){
				if(angulo > 90 && angulo < 180) angulo = 360 - angulo - 180;
				if(angulo >= 180 && angulo < 270) angulo = 360 - angulo + 180;
				if(velocidade < 0){
					velocidade *= -1;
					girarAntiHorario(5);
				}
				
			}
			if (x>=610){
				if(angulo > 0 && angulo <= 90) angulo= 360 - angulo;
				if(angulo >= 90 && angulo < 180) angulo= 360 - angulo;
				if(velocidade < 0){
					velocidade *= -1;
				}
			}
			x = x + Math.sin(Math.toRadians(angulo)) * velocidade;
			y = y - Math.cos(Math.toRadians(angulo)) * velocidade;
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
		
		for(Disparo disparo: disparos) {
			disparo.mover();
			disparo.draw(g2d);
			if(disparo.remover())
				removerDisparo = disparo;
		}
		disparos.remove(removerDisparo);
		
		//Aplicamos o sistema de coordenadas.
		g2d.transform(depois);
		
		//Desenha as esteiras
		g2d.setColor(Color.BLACK);
		g2d.fillRect(-15, -12, 30, 24);
		
		//g2d.setColor(Color.LIGHT_GRAY);
		//g2d.setStroke(new BasicStroke(2));
		//for(int i = -12+((int)x%2); i <= 8; i += 6)
			//if(i >= -12 && i < 24)
				//g2d.drawLine(-14, i, 14, i);
		
		//Desenhamos o corpo do tanque.
		g2d.setColor(cor);
		g2d.fillRect(-10, -12, 20, 24);
		//Agora as esteiras
		/*for(int i = -12; i <= 8; i += 4){
			g2d.setColor(Color.LIGHT_GRAY);
			g2d.fillRect(-15, i, 5, 4);
			g2d.fillRect(10, i, 5, 4);
			g2d.setColor(Color.BLACK);
			g2d.fillRect(-15, i, 5, 4);
			g2d.fillRect(10, i, 5, 4);
		}*/
		
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
			g2d.drawString("Angulo: " + (int)angulo + "", 25, l+=15);
			g2d.drawString("Velocidade: " + String.format("%.1f", velocidade), 25, l+=15);
			g2d.drawString("Posição X: " + String.format("%.2f", x), 25, l+=15);
			g2d.drawString("Posição Y: " + String.format("%.2f", y), 25, l+=15);
			g2d.drawString("Disparos: " + disparos.size() + "/3", 25, l+=15);
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
		for(int i = 0; i < (3 - disparos.size()) * 5; i += 6){
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
		if(disparos.size() < 3)
			disparos.add(new Disparo(x, y, angulo));
	}
	
	public boolean acertou(Tanque inimigo) {
		for(Disparo disparo: disparos)
			if(disparo.acertou(inimigo.x, inimigo.y)) {
				disparos.remove(disparo);
				return true;
			}
		return false;
	}
}