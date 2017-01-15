
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Pong extends JApplet implements KeyListener
{
	private DrawingArea canvas;
	private JButton start, pause, reset,AI,easy,medium,hard,impossible;
	public void init()
	{
		canvas = new DrawingArea();
		getContentPane().add(canvas, BorderLayout.CENTER);

		JPanel buttons = new JPanel();
		start = new JButton("Start");
		start.addActionListener(canvas);
		buttons.add(start);

		pause = new JButton("Pause");
		pause.addActionListener(canvas);
		buttons.add(pause);
		pause.setEnabled(false);

		reset = new JButton("Reset");
		reset.addActionListener(canvas);
		buttons.add(reset);

		AI = new JButton("Computer");
		AI.addActionListener(canvas);
		buttons.add(AI);

		easy = new JButton("Easy");
		easy.addActionListener(canvas);
		buttons.add(easy);
		easy.setEnabled(false);

		medium = new JButton("Medium");
		medium.addActionListener(canvas);
		buttons.add(medium);
		medium.setEnabled(false);

		hard = new JButton("Hard");
		hard.addActionListener(canvas);
		buttons.add(hard);
		hard.setEnabled(false);

		impossible = new JButton("Endless");
		impossible.addActionListener(canvas);
		buttons.add(impossible);
		impossible.setEnabled(false);

		getContentPane().add(buttons,BorderLayout.SOUTH);
		canvas.addKeyListener(canvas);
		canvas.requestFocus();
	}
	class DrawingArea extends JPanel implements ActionListener, Runnable, KeyListener
	{
		private Thread runner;
		private int x,y,size,ballcenter,paddle1center,paddle2center;
		private int p1x,p1y,p2x,p2y,length,width,p1,p2,counter,difficulty;
		private double hitpos,time;
		private boolean running, left,right,computer,endless;
		DrawingArea()
		{
			difficulty = 20;
			computer = false;
			right = left = false;
			switch((int)(Math.random()*2+1))
			{
			case 1: right = true; break;
			case 2: left = true; break;
			}
			time = 0;
			counter=p1=p2=0;
			p1x = 750;
			p1y = 157;
			p2x = 50;
			p2y = 157;
			x = 390;//400 = center x pos
			y = 222;//232 = center y pos
			//464 = panel size with bottom panel covering
			size = 20;
			length = 150;
			width = 10;
		}
		public void paintComponent(Graphics g)
		{
			super.paintComponent(g);
			setBackground(Color.black);
			g.setColor(Color.blue);
			g.fillRect(0,0,800,4);
			g.fillRect(0,460,800,4);
			g.fillRect(398,0,4,464);
			g.setColor(Color.white);
			g.setFont(new Font("Arial", Font.BOLD, 35));
			if(!computer)
				g.drawString("Player 2",10,50);
			if(computer)
				g.drawString("Computer",10,50);
			g.drawString("Player 1",660,50);
			if(endless)
				g.drawString(""+time/100,440,50);
			else
			{
				g.drawString(""+p1,440,50);
				g.drawString(""+p2,340,50);
			}
			g.fillRect(p1x,p1y,width,length);
			g.fillRect(p2x,p2y,width,length);
			if(p1==5)
			{
				g.drawString("Player 1 Wins!",300,232);
				running = false;
			}
			if(p2==5&&!computer)
			{
				g.drawString("Player 2 Wins!",300,232);
				running = false;
			}
			if(p2==5&&computer)
			{
				g.drawString("Computer Wins!",300,232);
				running = false;
			}
			if(p1!=5&&p2!=5)
				g.fillRect(x,y,size,size);
			if(computer)
			{
				easy.setEnabled(true);
				medium.setEnabled(true);
				hard.setEnabled(true);
				impossible.setEnabled(true);
			}
			else
			{
				easy.setEnabled(false);
				medium.setEnabled(false);
				hard.setEnabled(false);
				impossible.setEnabled(false);
			}
			this.requestFocus();
		}
		public void run()
		{
			start.setEnabled(false);
			pause.setEnabled(true);
			while(running)
			{
				ballcenter = y+size/2;
				paddle1center = p1y+length/2;
				paddle2center = p2y+length/2;
				if(x+size==p1x&&(y+size>p1y&&y<p1y+length))
				{
					hitpos=(ballcenter-paddle1center)/10;
					left = true;
					right = false;
				}
				if(x==p2x+width&&(y+size>p2y&&y<p2y+length))
				{
					hitpos=(double)(ballcenter-paddle2center)/10;
					right = true;
					left = false;
				}
				if(hitpos>0&&hitpos<1)
					hitpos=1;
				if(right)
				{
					x+=size/2;
					if(y+hitpos<4)
						hitpos = -hitpos;
					if(y+size+hitpos>460)
						hitpos = -hitpos;
					y+=hitpos;
				}
				if(left)
				{
					x-=size/2;
					if(y+hitpos<4)
						hitpos = hitpos*(-1);
					if(y+size+hitpos>460)
						hitpos = hitpos*(-1);
					y+=hitpos;
				}
				if(x+size>800)
				{
					left = true;
					right = false;
					x=390;
					y=222;
					hitpos=0;
					p2++;
					if(endless)
						p2=5;
				}
				if(x<0)
				{
					right = true;
					left = false;
					x=390;
					y=222;
					hitpos=0;
					p1++;
				}
				counter++;
				if(counter==100)
					counter = 0;
				if(computer)
				{
					if(p2y+length<y&&counter%difficulty==0)
						p2y+=length/4;
					if(p2y>y&&counter%difficulty==0)
						p2y-=length/4;
					if(endless)
						time+=10;
				}
				repaint();
				Thread.yield();
				try
				{
					Thread.sleep(10);
				}
				catch(InterruptedException e){}
			}
			running = false;

			start.setEnabled(true);
			pause.setEnabled(false);
		}
		public void actionPerformed(ActionEvent e)
		{
			String command = e.getActionCommand();
			if(command.equals("Start"))
				if(p1!=5&&p2!=5)
				{
					running = true;
					runner = new Thread(this);
					runner.start();
				}
			if(command.equals("Pause"))
				running = false;
			if(command.equals("Reset"))
			{
				running = right = left = false;
				p1x = 750;
				p1y = 157;
				p2x = 50;
				p2y = 157;
				x = 390;
				y = 222;
				hitpos=time=0;
				p1=p2=0;
				switch((int)(Math.random()*2+1))
				{
					case 1: right = true; break;
					case 2: left = true; break;
				}
				if(computer)
				{
					right = true;
					left = false;
					p2y = 156;
				}
				repaint();
			}
			if(command.equals("Computer"))
			{
				computer = !computer;
				p1x = 750;
				p1y = 157;
				p2x = 50;
				if(computer)
					p2y = 156;
				else
					p2y = 157;
				x = 390;
				y = 222;
				hitpos=0;
				p1=p2=0;
				running = left = endless = false;
				right = true;
				repaint();
			}
			if(command.equals("Easy"))
			{
				difficulty=20;
				p1x = 750;
				p1y = 157;
				p2x = 50;
				p2y = 156;
				x = 390;
				y = 222;
				hitpos=0;
				p1=p2=0;
				running = left = endless = false;
				right = true;
				repaint();
			}
			if(command.equals("Medium"))
			{
				difficulty=10;
				p1x = 750;
				p1y = 157;
				p2x = 50;
				p2y = 156;
				x = 390;
				y = 222;
				hitpos=0;
				p1=p2=0;
				running = left = endless = false;
				right = true;
				repaint();
			}
			if(command.equals("Hard"))
			{
				difficulty=5;
				p1x = 750;
				p1y = 157;
				p2x = 50;
				p2y = 156;
				x = 390;
				y = 222;
				hitpos=0;
				p1=p2=0;
				running = left = endless = false;
				right = true;
				repaint();
			}
			if(command.equals("Endless"))
			{
				difficulty=1;
				p1x = 750;
				p1y = 157;
				p2x = 50;
				p2y = 156;
				x = 390;
				y = 222;
				hitpos=0;
				p1=p2=0;
				running = left = false;
				right = endless = true;
				repaint();
			}
		}
		public void keyPressed (KeyEvent e)
		{
			int value = e.getKeyCode();
			if(running)
			{
				if(value == KeyEvent.VK_UP&&p1y-length/10>4)
					p1y-=length/4;
				if(value == KeyEvent.VK_DOWN&&p1y+length+length/10<460)
					p1y+=length/4;
				repaint();
			}
		}
		public void keyTyped (KeyEvent e)
		{
			char letter = e.getKeyChar();
			if(running)
				if(!computer)
				{
					if(letter == 'w'&&p2y-length/10>4)
						p2y-=length/4;
					if(letter == 's'&&p2y+length+length/10<460)
						p2y+=length/4;
				}
		}
		public void keyReleased (KeyEvent e) {}
	}
	public void keyPressed (KeyEvent e) {}
	public void keyTyped (KeyEvent e) {}
	public void keyReleased (KeyEvent e) {}
}