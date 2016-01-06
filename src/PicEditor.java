import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.UIManager.*;

public class PicEditor {
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException{
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		JFrame j = new JFrame();
		j.setLayout(new FlowLayout());
		Rectangle screenSize = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		j.setSize((int)screenSize.getWidth(), (int)screenSize.getHeight());
		
		MainPanel p = new MainPanel(j);
		j.setContentPane(p);
		j.setVisible(true);
		j.validate();
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}

class MainPanel extends JPanel implements MouseListener, ActionListener, KeyListener{
	JFrame jf;
	ToolPanel t;
	AttributeScrollPane a;
	Canvas c;
	int toolID=1;
	public boolean ctrlKey, shiftKey, enterKey;
	
	public MainPanel(JFrame j){
		jf=j;
		setVisible(true);
		setLayout(new BorderLayout());
		c = new Canvas(this);
		add(c, BorderLayout.CENTER);
		t = new ToolPanel(this);
		add(t, BorderLayout.PAGE_START);
		a = new AttributeScrollPane(this);
		add(a, BorderLayout.LINE_START);
		
		initKeyState();
		addKeyListener(this);
		setFocusable(true);
		addMouseListener(this);
	}
	public Dimension getPreferredSize(){
		return new Dimension(jf.getWidth()-16, jf.getHeight()-40);
	}
	public void initKeyState(){
		ctrlKey = shiftKey = enterKey
				= false;
	}
	public void setTool(int i){
		toolID=i;
		c.cp.setTool(i);
	}
	
	public void keyPressed(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_CONTROL)
			ctrlKey=true;
		if(e.getKeyCode()==KeyEvent.VK_SHIFT)
			shiftKey=true;
		if(e.getKeyCode()==KeyEvent.VK_Z && ctrlKey)
			c.cp.undo();
	}
	public void keyReleased(KeyEvent e) {
		if(e.getKeyCode()==KeyEvent.VK_CONTROL)
			ctrlKey=false;
		if(e.getKeyCode()==KeyEvent.VK_SHIFT)
			shiftKey=false;
		if(e.getKeyCode()==KeyEvent.VK_ENTER)
			c.cp.pressEnter();
	}
	public void keyTyped(KeyEvent e) {}

	public void mouseClicked(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {
		requestFocusInWindow();
	}
	public void mouseReleased(MouseEvent e) {}

	public void actionPerformed(ActionEvent e) {}
	
	public void o(String s){
		System.out.println(s);
	}
}



