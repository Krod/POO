import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.HashSet;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.Timer;

@SuppressWarnings("serial")
public class Arena extends JComponent implements MouseListener, ActionListener, MouseMotionListener, KeyListener, MouseWheelListener, ComponentListener{
	protected static int largura,altura;
	private HashSet<Tanque> tanques, tanquesRemover;
	private Timer contador;
	private int mouseX;
	private Tanque tanqueAtivo;
	
	public Arena(int l,int a){
		largura = l; 
		altura = a;
		tanques = new HashSet<Tanque>();
		tanquesRemover = new HashSet<>();
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		addComponentListener(this);
		addKeyListener(this);
		setFocusable(true);
		contador = new Timer(10,this);
		contador.start();
	}
	public void adicionaTanque(Tanque t){
		tanques.add(t);
	}
	public Dimension getMaximumSize(){
		return getPreferredSize();
	}
	public Dimension getMinimumSize(){
		return getPreferredSize();
	}
	public Dimension getPreferredSize(){
		return new Dimension(largura,altura);
	}
	@Override
	protected void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
		RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setColor(new Color(245,245,255));
		g2d.fillRect(0,0,largura,altura);
		g2d.setColor(new Color(220,220,220));
		
		// Desenha a grade
		for(int _largura=0;_largura<=largura;_largura+=20)
			g2d.drawLine(_largura,0,_largura,altura);
		for(int _altura=0;_altura<=altura;_altura+=20) 
			g2d.drawLine(0,_altura,largura,_altura);
		
		// Desenhamos todos os tanques
		for(Tanque t:tanques) {
			if(tanqueAtivo != null && t != tanqueAtivo && tanqueAtivo.acertou(t)) {
				t.pontosVida--;
				if(t.pontosVida == 0)
					tanquesRemover.add(t);
			}
			t.draw(g2d);
		}
		
		tanques.removeAll(tanquesRemover);
	}
	public void mouseClicked(MouseEvent e){
		for(Tanque t:tanques) {
			t.setEstaAtivo(false);
			tanqueAtivo = null;
		}
		for(Tanque t:tanques){
			boolean clicado = t.getRectEnvolvente().contains(e.getX(),e.getY());
			if (clicado){
				t.setEstaAtivo(true);
				tanqueAtivo = t;
				/*switch(e.getButton()){
					case MouseEvent.BUTTON1: 
						t.aumentarVelocidade(); 
						break;
					case MouseEvent.BUTTON2: 
						t.aumentarVelocidade();
						break;
					case MouseEvent.BUTTON3: 
						t.girarHorario(3); 
						break;
				}*/
				break;
			}
		}
		repaint();
	}
	public void mouseEntered(MouseEvent e) { }
	public void mouseExited(MouseEvent e) { }
	public void mousePressed(MouseEvent e) { }
	public void mouseReleased(MouseEvent e) { }
	public void actionPerformed(ActionEvent e){
		for(Tanque t:tanques)
			t.mover();
		repaint();
	}
	public void mouseMoved(MouseEvent e) {
		if(tanqueAtivo != null)
			if(e.getX() > mouseX)
				tanqueAtivo.girarHorario(2);
			else if(e.getX() < mouseX)
				tanqueAtivo.girarAntiHorario(2);
		mouseX = e.getX();
	}
	public void mouseDragged(MouseEvent e) { }
	
	@Override
	public void keyPressed(KeyEvent e) {

			if(tanqueAtivo != null){
				switch(e.getKeyCode()){
			    case KeyEvent.VK_LEFT:
			    	tanqueAtivo.girarAntiHorario(3);
			    	break;
		        case KeyEvent.VK_UP: 
		        	tanqueAtivo.aumentarVelocidade(); 
		        	break;
		        case KeyEvent.VK_DOWN :
		        	tanqueAtivo.diminuirVelocidade();
		        	break;
				case KeyEvent.VK_RIGHT: 
					tanqueAtivo.girarHorario(3); 
					break;
				case KeyEvent.VK_SPACE: 
					tanqueAtivo.disparar();
					//atirar(t.getId());
					//agora = System.currentTimeMillis();
					break;
				}
			}
			//repaint();
	}
	
	@Override
	public void keyTyped(KeyEvent e) { }
	@Override
	public void keyReleased(KeyEvent e) { }
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if(tanqueAtivo != null) {
			if(e.getWheelRotation() < 0)
				tanqueAtivo.aumentarVelocidade();
			else
				tanqueAtivo.diminuirVelocidade();
		}
	}
	
	public static void main(String args[]){
		Arena arena = new Arena(800,600);
	    Random random = new Random();
		arena.adicionaTanque(new Tanque(100,200,random.nextInt(360),Color.BLUE));
		arena.adicionaTanque(new Tanque(200,200,random.nextInt(360),Color.RED));
		arena.adicionaTanque(new Tanque(470,360,random.nextInt(360),Color.GREEN));
		arena.adicionaTanque(new Tanque(450,50,random.nextInt(360),Color.YELLOW));
		JFrame janela = new JFrame("Tanques");
		janela.getContentPane().add(arena);
		janela.pack();
		janela.setVisible(true);
		janela.setDefaultCloseOperation(3);
	}
	@Override
	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void componentMoved(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void componentResized(ComponentEvent e) {
		altura = e.getComponent().getHeight();
		largura = e.getComponent().getWidth();
	}
	@Override
	public void componentShown(ComponentEvent e) {
		// TODO Auto-generated method stub
		
	}
}