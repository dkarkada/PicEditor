import java.awt.event.MouseEvent;
import java.util.Stack;

public class CommandStack<T> extends Stack<T>{
	int size;
	public CommandStack(int s){
		size=s;
	}
	public Object push(Object obj){
		while(this.size() >= size){
			this.remove(0);
		}
		return super.push((T) obj);
	}
}

interface Command {
	void execute();
	void undo();
}

class PaintStrokeCommand implements Command{
	Painting p;
	MouseEvent e;
	CanvasPanel cp;
	
	public PaintStrokeCommand(Painting ptg, MouseEvent me, CanvasPanel c){
		p=ptg;
		e=me;
		cp=c;
		execute();
	}
	public void execute(){
		p.addStroke(cp);
		p.addPoint(e.getX(), e.getY());
	}
	public void undo(){
		p.removeStroke();
	}
}

class AddPaintingCommand implements Command{
	public void execute(){
		
	}
	public void undo(){
		
	}
}
