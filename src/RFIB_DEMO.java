import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JOptionPane;
import org.w3c.dom.css.RGBColor;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.core.PShape;
import processing.data.IntList;
import processing.core.PSurface;
import processing.event.KeyEvent;
import processing.event.MouseEvent;
// import com.jogamp.newt.Screen;
import RFIB.RFIBricks_Cores;
// import RFIB.X16;

public class RFIB_DEMO extends PApplet {
	RFIBricks_Cores RFIB;
	float boxSizex = 100.0F;
	float boxSizey = 100.0F;
	int boxNumX = 6; int boxNumY = 6; 
	int W =(int)(boxSizex*(boxNumX+2)); 
	int H =(int)(boxSizey*(boxNumY+1)); 
	int one = color(175, 255,175);
	int two = color(175,175,255);
	int strokeW=6;
	int target = color(60);
	int movable = color(120);
	int error = color(255, 0, 0);
	int warning = color(255, 255, 34);
	int hintColor = error;
	int[] selected = new int[0];
	HashMap<Integer, int[]> blocks = new HashMap();
	ArrayList<int[]> validList = new ArrayList();
	ArrayList<int[]> hints = new ArrayList();
	IntList tmp = new IntList();
	IntList stack = new IntList();
	int turn=1;
	boolean start = false;
	PFont f1,f2;	
	PImage Crown;	
	int movingID=-1, newID=-1;
	
	static public void main(String[] args) {
		PApplet.main(RFIB_DEMO.class.getName());
	}
	String[] AllowBlockType = {  /* Color,TAG ON BOTTOM */
		"9999", /*Board*/ "9998", /*Virtual Board*/			
			//70-71-72  73-74-75 76-77-78  79-80-81 81-82-83 
			//84-85-86 87-88-89   
			//90-91  92-93 94-95 96-97   98-99
		// "9101", /*Black Block With 1 tag*/ "9102",/*Black Block With 2 tag*/ 
		"9201", /*white*/ "9301", /*black*/ "9101", /*Red*/		
			//"9601", //Tangle 直角三角形 ID: 20-Black,30-white,40-Gray,50-Red
			//"9701", //Tangle   正三角體 ID: 20-Black,30-white,40-Gray,50-Red//
			//"0001", //Knob-Widget
	};
	int FrameRate = 60;
	boolean Flag_fullscreen=false;

	boolean Flag_ToConnectTheReader  = false;
	short[] EnableAntenna = {1,2,3,4};
	String ReaderIP = "192.168.1.93";
	double ReaderPower=32, Sensitive=-70;

	//===============================VIDEO=============================================
	// int fronSize = 120;
	// PImage Block_Red, Block_Gray;						//Flag_TwoBlockStackDemo	
	// PImage img_Button, img_ToggleSwitch, img_TiltSwitch;	//Flag_DemoWidget
	// int BlockH, BlockW, BlockGap=H/30;
	// int D=41, U=38, BU=89, reSize=3;
	//=================================================================================
	public void settings() {
		if (Flag_fullscreen) fullScreen(P2D); else size(W, H,P2D); 
	}
	public void setup() {
		surface.setResizable(true);
		RFIB = new RFIBricks_Cores(ReaderIP, ReaderPower, Sensitive, EnableAntenna, Flag_ToConnectTheReader);
		// RFIB.setShowSysMesg(false);
		// RFIB.setShowReceiveTag(false);
		// RFIB.setSysTagBased("7428 0000");
		// RFIB.setAllowBlockType(AllowBlockType);
		// RFIB.startReceive();
		frameRate(FrameRate);		
		Crown = loadImage("crown2.png");
		Crown.resize((int)(Crown.width*boxSizex/1600), (int)(Crown.height*boxSizey/1600));
		f1 = createFont("helevetica", boxSizex*2/5, true);
		f2 = createFont("helevetica", boxSizex/4, true);
		strokeWeight(strokeW);

		// RFIB.addRefreshTime(200);
		// RFIB.addDisappearTime(-200);
		blocks.put(0,new int[] {  0, 0,0 });
		
	}
	public void draw() {
		background(0);
		// RFIB.statesUpdate();	
		translate(boxSizex/2,boxSizey/2);
		DrawBoard();			
		DrawBlock();
		if ((millis()/100)%2==0)
			DrawHint();
		rotate(HALF_PI);
		rotate(PI);
		show();
		if(start){
			if(Flag_ToConnectTheReader){
				takeOff();
				putDown();
			}
			if(hints.size()==0){
				for (int tmpID:blocks.keySet()) {
					if(tmpID!=0&&(tmpID/10000-92.5)*turn<0){
						makeValidList(tmpID);
						if(validList.size()>1){
							if (hints.size()>0){
								if(validList.get(1)[2]-hints.get(0)[2]<0)
									continue;
								else if(validList.get(1)[2]-hints.get(0)[2]>0) 
									hints.clear();
							}
							hints.add(new int[]{blocks.get(tmpID)[0],blocks.get(tmpID)[1],validList.get(1)[2]});
						}
					}
				}
				hintColor=movable;
			}
			if(hintColor==error){
				JOptionPane.showMessageDialog(frame, "Invalid action!");
				// textFont(f1);
				// text("Invalid move!", boxSizey/2-H/2,(int)(W-boxSizex*1.1));	
				start=false;
			}
		}else{
			// hintColor=warning;
			fill(255,0,0);
			textFont(f2);
			textAlign(CENTER);
			// text("'r': start/continue", boxSizey/2-H/2,(int)(W-boxSizex*0.7));	
			text("'r': start/continue", H/2, (int)(boxSizex*0.2));	
		}
	}
	private void show(){	
		int w=0;int b=0;
		for (int tmpID:blocks.keySet()) {
			if(tmpID/10000-92.5>0)b++;
			else if(tmpID!=0) w++;
		}
		if (hintColor==target){
			if (turn==-1)b++;
			else w++;
		}
		noFill();
		stroke(175,175,255);
		rect((float)(-1) * boxSizey,(float)(boxNumX+0.45)* boxSizex,  boxSizex,boxSizey);
		stroke(175,255,175);
		rect( (float)(-6) * boxSizey,(float)(boxNumX+0.45) * boxSizex,boxSizex,boxSizey);
		textFont(f1);
		translate(-H+boxSizey/2,(int) (W-boxSizex*0.9));
		fill(one);
		text(Integer.toString(w), boxSizey,0);
		fill(two);
		text(Integer.toString(b), H-boxSizey,0);
		if(w*b==0&&w+b!=0&&hintColor==movable&&hints.size()==0){
			start=false;
			fill(255, 160, 42);
			if ((millis()/100)%3!=0)
				text("win!", (int)(boxSizey*(b-w<0?2.2:boxNumY-1.2)),(int)(-boxSizex*0.1));
				// text("win!", (int)(boxSizey*(turn<0?2.2:boxNumY-1.2)-H+boxSizey/2),(int)(W-boxSizex*0.9));
		}
	}
	public void takeOff(){
		//ckeck if blocks still in stack then set missing (only 1)
		for (Integer tmpID : blocks.keySet()) { 
			if(tmpID!=0){
				if (!RFIB.StackedOrders3D.containsKey(tmpID)||RFIB.StackedOrders3D.get(tmpID)[2]==3){ 
					movingID=tmpID;
					break;
				}
			}
		}
		if(movingID>0){
			if(movingID>0)
			System.out.print("move:");
			System.out.println(movingID);
			checkOff(movingID);
			movingID=-1;
		}
	}
	public void  putDown(){
		for (int tmpID : RFIB.StackedOrders3D.keySet()) { 
			if (!blocks.containsKey(tmpID)&&RFIB.StackedOrders3D.get(tmpID)[0]>-1){ //new guy not in blocks
				newID=tmpID;
				if (RFIB.StackedOrders3D.get(tmpID)[2]==2){ 
					for (int stacked : stack) {
						if (stacked==tmpID){
							newID=-1;
							break;
						}
					}
					if(newID>0){
						stack.append(newID);
					}
				}
				if(newID>0)break;
			}
		}
		if(newID>0){
			System.out.print("new");
			System.out.println(newID);
			checkOn(newID);
			movingID=-1;
			newID=-1;
		}
	}
	public void  checkOff(int movingID){
		if(hintColor==warning){
			for (int i =0;i<hints.size();i++) {
				if(hints.get(i)[0]==blocks.get(movingID)[0]&&hints.get(i)[1]==blocks.get(movingID)[1]&&hints.get(i)[1]!=((movingID/10000-92.5)>0?boxNumY-1:0)){
					hints.remove(i);
					blocks.remove(movingID);
					movingID=-1;
					break;
				}
			}
			if(movingID!=-1)
				hintColor=error;
		}else if(hintColor==target){
			// hints.add(new int[]{RFIB.StackedOrders3D.get(movingID)[0],RFIB.StackedOrders3D.get(movingID)[1]});}
			hintColor=error;
		}else if(hintColor==movable){
			hintColor=error;
			for (int[] hint:hints) {
				if(hint[0]==blocks.get(movingID)[0]&&hint[1]==blocks.get(movingID)[1]){
					hints.clear();
					makeValidList(movingID);
					for (int[] validOne: validList) {
						hints.add(new int[] {validOne[0], validOne[1]});
					}
					hintColor = target;
					blocks.put(0, new int[]{blocks.get(0)[0],blocks.get(0)[1],blocks.get(movingID)[2]});
					blocks.remove(movingID);
					System.out.println("rm movingID");
					break;	
				}
			}
		}
	}
	public void  checkOn(int newID){
		if(hintColor==warning){
			hintColor=error;
			for (int i=0;i<hints.size() ;i++ ) {
				if(hints.get(i)[0]==blocks.get(0)[0]&&hints.get(i)[1]==blocks.get(0)[1]&&hints.get(i)[1]==((newID/10000-92.5)>0?boxNumY-1:0)){
					hints.remove(i);
					int tmpID=getIDfromLocation(blocks.get(0)[0],blocks.get(0)[1]);
					blocks.put(tmpID,new int[] {blocks.get(tmpID)[0],blocks.get(tmpID)[1],1});
					hintColor=warning;
					break;
				}
			}
		}else{
			int isvalid = valid(((int[])blocks.get(0))[0], ((int[])blocks.get(0))[1]);
			if (isvalid >= 0) {
				int[] arrayOfInt = (int[])validList.get(isvalid);
				hints.clear();
				blocks.put(newID,blocks.get(0));
				if (isvalid > 0) {//not self
					if (arrayOfInt[2] >= 0) {
						hintColor = warning;
						for (int j = 0; j < arrayOfInt.length - 2; j++) {
							hints.add(new int[]{blocks.get(arrayOfInt[(j + 2)])[0], blocks.get(arrayOfInt[(j + 2)])[1]});
						}
					}
					turn = (-turn);
					hintColor = warning;
					if(blocks.get(newID)[2]==0){
						if(blocks.get(0)[1]==0 && newID/10000-92.5<0){
							hints.add(new int[] {blocks.get(0)[0],blocks.get(0)[1]});
						}else if(blocks.get(0)[1]==boxNumY-1 && newID/10000-92.5>0){
							hints.add(new int[] {blocks.get(0)[0],blocks.get(0)[1]});
						}
					}
				}
			}else {
				hints.add(new int[] {blocks.get(0)[0],blocks.get(0)[1]});
				hintColor = error;
			}
				selected = null;
		}
	}
	public void mouseClicked(MouseEvent evt) { 
		setpre();
		if (mouseButton == LEFT){
			for (int i :RFIB.StackedOrders3D.keySet()) {
				if (i!=0 && RFIB.StackedOrders3D.get(i)[0] == blocks.get(0)[0] && RFIB.StackedOrders3D.get(i)[1] == blocks.get(0)[1]) {
					movingID=i;
					break;
				}
			}
			System.out.print(evt.getCount());
		// if (evt.getCount()<2){
		// 	if((movingID/10+movingID%10)%2!=1)
		// 		movingID++;
		// 	RFIB._Testing_RemoveHoldingTag("7428 0000 9999 0" + (blocks.get(0)[0] + 1) + "0" + (blocks.get(0)[1] + 1) + " 0001", "7428 0000 " + Integer.toString(movingID / 100) + " " + Integer.toString(movingID).substring(4, 6) + "01 0001");
		// 	System.out.print("move:");
		// 	System.out.println(movingID);
		// }
		// else{
		while (RFIB.StackedOrders3D.containsKey(movingID)){
			RFIB._Testing_RemoveHoldingTag("7428 0000 9999 0" + (blocks.get(0)[0] + 1) + "0" + (blocks.get(0)[1] + 1) + " 0001", "7428 0000 " + Integer.toString(movingID / 100) + " " + Integer.toString(movingID).substring(4, 6) + "01 0001");
			System.out.print("move:");
			System.out.println(movingID);
			if((movingID/10+movingID%10)%2==1)movingID--;
			else{
				checkOff(movingID);
				movingID++;
			}
		}
		// }
			movingID=-1;
		}
		else if (mouseButton == RIGHT){
			for (int i :RFIB.StackedOrders3D.keySet()) {
				if (blocks.get(0)[0] == RFIB.StackedOrders3D.get(i)[0] && RFIB.StackedOrders3D.get(i)[1]==blocks.get(0)[1]){//add on another block
				 	 newID=i;
				 	 break;
				}
			}
			if(newID>0){
				RFIB._Testing_AddHoldingTag("7428 0000 "+Integer.toString(newID / 100)+" "+String.format ("%02d",newID%100+1)+"01 0001", "7428 0000 " + Integer.toString(newID / 100) + " " + Integer.toString(newID).substring(4, 6) + "03 0001");
			}else{
				newID=blocks.get(0)[0]*10+blocks.get(0)[1]+(9251-turn*50)*100;
				RFIB._Testing_AddHoldingTag("7428 0000 9999 0" + Integer.toString(1 + newID%100/10) + "0" + Integer.toString(1 + newID%10) + " 0001", "7428 0000 " + Integer.toString(newID / 100) + " " + Integer.toString(newID).substring(4, 6) + "01 0001");
				if(blocks.get(0)[2]>0) 
					RFIB._Testing_AddHoldingTag("7428 0000 "+Integer.toString(newID / 100)+" "+String.format ("%02d",newID%100+1)+"01 0001", "7428 0000 " + Integer.toString(newID / 100) + " " + Integer.toString(newID).substring(4, 6) + "03 0001");
			}
			System.out.print("new:");
			System.out.println(newID);
			checkOn(newID);
			newID=-1;
		} 
	}

	private int valid(int X, int Y){
		for (int i = 0; i < validList.size(); i++) {
			if ((X == ((int[])validList.get(i))[0]) && (Y == ((int[])validList.get(i))[1])) 
				return i;
		}
		return -1;
	}
	private void makeValidList(int movingID) {
		selected = blocks.get(movingID);
		validList.clear();
		validList.add(new int[] { selected[0], selected[1], -1 });
		if (turn * (movingID/100 - 9212) < 0.0D) {
			searchJump(selected[0], selected[1],new IntList(),blocks.get(movingID)[2],-1,-1);
			if (validList.size() == 1) {
				for(int i=0;i<blocks.get(movingID)[2]+1;i++){
					if ((getIDfromLocation(selected[0] + 1, selected[1] - turn*((i-0.5)<0?1:-1)) == 0) && 
						(0 <= selected[0] + 1) && (selected[0] + 1 < boxNumX) && (0 <= selected[1] - turn*((i-0.5)<0?1:-1)) && (selected[1] - turn*((i-0.5)<0?1:-1) < boxNumY)) {
						validList.add(new int[] { selected[0] + 1, selected[1] - turn*((i-0.5)<0?1:-1), -1 });
					}
					if ((getIDfromLocation(selected[0] - 1, selected[1] - turn*((i-0.5)<0?1:-1)) == 0) && 
						(0 <= selected[0] - 1) && (selected[0] - 1 < boxNumX) && (0 <= selected[1] - turn*((i-0.5)<0?1:-1)) && (selected[1] - turn*((i-0.5)<0?1:-1) < boxNumY))
						validList.add(new int[] { selected[0] - 1, selected[1] - turn*((i-0.5)<0?1:-1), -1 });
				}
			}
		}
	}

	private void searchJump(int posX, int posY,IntList eat,int direction,int from,int to){
		boolean[] i = {true,true,true,true};
		int k;
		int dir=turn;
		for(int d=0;d<direction+1;d++,dir=-dir){
			if (posX + 2==from&&posY - dir * 2==to){;}
			else if ((0 <= posX + 2) && (posX + 2 < boxNumX) && (0 <= posY - dir * 2) && (posY - dir * 2 < boxNumY)) {
				k = getIDfromLocation(posX + 1, posY - dir * 1);

				// if ((k != 0) && ((RFIB.StackedOrders3D.get(k)[5]/10000-92.5)* turn > 0.0D)&& 
				if ((k != 0) && ((k/10000-92.5)* turn > 0.0D)&& 
					(getIDfromLocation(posX + 2, posY - dir * 2) == 0)) {
					IntList eat1=eat.copy();
					eat1.append(k);
					searchJump(posX + 2, posY - dir * 2,eat1,direction,posX,posY);
					i[0+d]=false;
				}
			}
			if (posX - 2==from&&posY - dir * 2==to){;}
			else if ((0 <= posX - 2) && (posX - 2 <boxNumX) && (0 <= posY - dir * 2) && (posY - dir * 2 < boxNumY)) {
				k = getIDfromLocation(posX - 1, posY - dir * 1);
				// if ((k != 0) && ((RFIB.StackedOrders3D.get(k)[5]/10000-92.5) * turn > 0.0D) && 
				if ((k != 0) && ((k/10000-92.5) * turn > 0.0D) && 
					(getIDfromLocation(posX - 2, posY - dir * 2) == 0)) {
					IntList eat2=eat.copy();
					eat2.append(k);
					searchJump(posX - 2, posY - dir * 2,eat2,direction,posX,posY);
					i[2+d]=false;
				}
			}
		}
		if (i[0]&& i[1]&& i[2]&& i[3] && (eat.size() != 0)) {
			int[] arrayOfInt = new int[eat.size() + 2];
			arrayOfInt[0] = posX;
			arrayOfInt[1] = posY;
			for (int m = 0; m < eat.size(); m++)
				arrayOfInt[(m + 2)] = eat.get(m);
			validList.add(arrayOfInt);
		}
	}

	private void setpre(){
		blocks.put(0, new int[]{(int)(mouseX / boxSizex-0.5),(int)(mouseY / boxSizey-0.5),blocks.get(0)[2]});
	}
	private int getIDfromLocation(int x, int y) {
		for (int i :blocks.keySet()) {
			if (i!=0 && blocks.get(i)[0] == x && blocks.get(i)[1] == y) return i;
		}
		return 0;
	}
	private boolean reload() { 
		int x;int y;
		blocks.clear();
		hints.clear();
		turn = -turn;
		ArrayList<int[]> tmpArray = new ArrayList();
		for (int tmpID:RFIB.StackedOrders3D.keySet()) { 
			x=RFIB.StackedOrders3D.get(tmpID)[0];
			y=RFIB.StackedOrders3D.get(tmpID)[1];
			if (RFIB.StackedOrders3D.get(tmpID)[2] != 1) {
				tmpArray.add(new int[]{x,y,tmpID});
			}else{
				if ((x+y)%2==1)
					hints.add(new int[] {x,y});
				else if (tmpID / 100 == 9301) 
					blocks.put( tmpID,new int[] { x,y,0});
				else if (tmpID / 100 == 9201) 
					blocks.put( tmpID, new int[] { x,y,0});
				else {
					RFIB.printStackedOrders3D();
					JOptionPane.showMessageDialog(frame, "Only black or white blocks!");
					return false;
				}
			}
		}
		for (int[] stack:tmpArray) { 
			for (int tmpID:RFIB.StackedOrders3D.keySet()) { 
				if(stack[2]!=tmpID&&stack[0]==RFIB.StackedOrders3D.get(tmpID)[0]&&stack[1]==RFIB.StackedOrders3D.get(tmpID)[1]){
					blocks.put( tmpID,new int[] { blocks.get(tmpID)[0],blocks.get(tmpID)[1],1});
					break;
				}
			}
		}
		blocks.put(0,new int[] {  0, 0,0 });
		if (hints.size()>0) {
			hintColor=error;
		}
		else {hintColor=movable;
		}
			return true; 
	}
	private void DrawHint(){
		if(hintColor==target||hintColor==movable){
			if(turn>0)
	    		stroke(0,255,0);
			else 
	    		stroke(0,0,255);
		}else
			stroke(hintColor);
	    for (int hint=0;hint<hints.size();hint++) {
	      noFill();
	      
	      rect((int)(hints.get(hint)[0]) * boxSizex-strokeW/2, (int)(hints.get(hint)[1]) * boxSizey-strokeW/2, boxSizex+strokeW, boxSizey+strokeW);
	    }
	  }
	private void DrawBoard() {	
		noStroke();
		for (int i = 0; i < boxNumY; i++)
			for (int j = 0; j < boxNumX; j++) {
				if ((j + i) % 2 == 0) {
					fill(255);
					ellipse((float)(j+0.5) * boxSizex, (float)(i+0.5) * boxSizey, boxSizex/10, boxSizey/10);
				}
			}
	}	
	private void DrawBlock() {
		tmp.clear();
		for (Integer tmpID : RFIB.StackedOrders3D.keySet()) { 
			if (RFIB.StackedOrders3D.get(tmpID)[2] > 1) tmp.append(tmpID); 
			else
				DrawTheObjectXYZ(tmpID, RFIB.StackedOrders3D.get(tmpID)[5], RFIB.StackedOrders3D.get(tmpID)[0], RFIB.StackedOrders3D.get(tmpID)[1], RFIB.StackedOrders3D.get(tmpID)[2]);
		}
		for (Integer tmpID : tmp)
			DrawTheObjectXYZ(tmpID, RFIB.StackedOrders3D.get(tmpID)[5], RFIB.StackedOrders3D.get(tmpID)[0], RFIB.StackedOrders3D.get(tmpID)[1], RFIB.StackedOrders3D.get(tmpID)[2]);
	}	
	private void DrawTheObjectXYZ(int blockID, int BlockType, int X, int Y, int Z){
		if (BlockType == 9201)
			stroke(one);
		else if (BlockType == 9301)
			stroke(two);
		if (Z == 2) {
	    	image(Crown, (int)((X+1)* boxSizex), (int)((Y-0.4) * boxSizey));
		}
		else if(Z == 1){
			noFill();
			rect((int) X * boxSizex-strokeW/2, (int)Y * boxSizey-strokeW/2, boxSizex+strokeW, boxSizey+strokeW);
		}
	}
	private void preStack(){
		RFIB.StackedOrders3D.clear();
		RFIB._Testing_AddHoldingTag("7428 0000 9999 0101 0001","7428 0000 9301 0001 0001");
		RFIB._Testing_AddHoldingTag("7428 0000 9999 0301 0001","7428 0000 9301 2001 0001");
		RFIB._Testing_AddHoldingTag("7428 0000 9999 0501 0001","7428 0000 9301 4001 0001");
		RFIB._Testing_AddHoldingTag("7428 0000 9999 0202 0001","7428 0000 9301 1101 0001");
		RFIB._Testing_AddHoldingTag("7428 0000 9999 0402 0001","7428 0000 9301 3101 0001");
		RFIB._Testing_AddHoldingTag("7428 0000 9999 0602 0001","7428 0000 9301 5101 0001");
		RFIB._Testing_AddHoldingTag("7428 0000 9999 0105 0001","7428 0000 9201 0401 0001");
		RFIB._Testing_AddHoldingTag("7428 0000 9999 0305 0001","7428 0000 9201 2401 0001");
		RFIB._Testing_AddHoldingTag("7428 0000 9999 0505 0001","7428 0000 9201 4401 0001");
		RFIB._Testing_AddHoldingTag("7428 0000 9999 0206 0001","7428 0000 9201 1501 0001");
		RFIB._Testing_AddHoldingTag("7428 0000 9999 0406 0001","7428 0000 9201 3501 0001");
		RFIB._Testing_AddHoldingTag("7428 0000 9999 0606 0001","7428 0000 9201 5501 0001");

	}
	public void printBlocks() {
	  System.out.println("======= printBlocks() ==========================");
	  for (Integer tmpID : blocks.keySet()) {
	    System.out.println(tmpID + " ::: " + ((int[])blocks.get(tmpID))[0] + " " + ((int[])blocks.get(tmpID))[1] + " " + ((int[])blocks.get(tmpID))[2]);
	  }
	  System.out.println("=====================================================\n");
	}
	public void keyPressed() {
		if(key=='b') preStack();
		if(key=='q') printBlocks();
		if(key=='p') RFIB.printStackedOrders();
		if(key=='t') {turn=-turn;hintColor=movable;hints.clear();System.out.println(turn);}
		if(key=='r') {start =reload();System.out.println(blocks.size());}
		// if(key==' ') { RFIB.startToBuild(); RFIB.printNoiseIDs(); }
		// if(key=='`') RFIB.printAllReceivedIDs();
		// if(key=='C' || key=='c')RFIB.CleanAllBlocks();
		// if(key=='1') RFIB.printNoiseIDs();
		// if(key=='+') RFIB.addRefreshTime(50);
		// if(key=='-') RFIB.addRefreshTime(-50);
		// if(key=='>') RFIB.addDisappearTime(50);
		// if(key=='<') RFIB.addDisappearTime(-50);
	}

}