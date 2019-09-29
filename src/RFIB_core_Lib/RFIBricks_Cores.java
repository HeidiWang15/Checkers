package RFIB;

// import com.impinj.octane.AntennaConfig;
// import com.impinj.octane.AntennaConfigGroup;
// import com.impinj.octane.ImpinjReader;
// import com.impinj.octane.OctaneSdkException;
// import com.impinj.octane.ReaderMode;
// import com.impinj.octane.ReportConfig;
// import com.impinj.octane.SearchMode;
// import com.impinj.octane.Settings;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class RFIBricks_Cores
{
  // private static final long serialVersionUID = 1L;
  // public static HashMap<String, Double> DetectedTagsInitial = new HashMap();
  // public ArrayList<String> StackedOrders = new ArrayList();
  // public ArrayList<String> StackedOrdersRecursive = new ArrayList();
  // private HashMap<String, Long> DispearCount = new HashMap();
  public RFIBricks_Cores(String ReaderIP, double ReaderPower, double Sensitive, short[] EnableAntenna, boolean Flag_ToConnectTheReade){};
  public HashMap<Integer, int[]> StackedOrders3D = new HashMap();
  private int z;
   public void _Testing_AddHoldingTag(String ID1, String ID2)
  {
 if(ID2.substring(17,19).equals("03"))
	this.StackedOrders3D.put(Integer.parseInt(ID1.substring(10,14)+ID1.substring(15,17)),new int[] {
    this.StackedOrders3D.get(Integer.parseInt(ID2.substring(10,14)+ID2.substring(15,17)))[0],
    this.StackedOrders3D.get(Integer.parseInt(ID2.substring(10,14)+ID2.substring(15,17)))[1],2,1,123456,Integer.parseInt(ID1.substring(10,14)),Integer.parseInt(ID1.substring(10,14)+ID1.substring(15,17))});
  else
    this.StackedOrders3D.put(Integer.parseInt(ID2.substring(10,14)+ID2.substring(15,17)),new int[] {Integer.parseInt(ID1.substring(15,17))-1,Integer.parseInt(ID1.substring(17,19))-1,1,1,123456,Integer.parseInt(ID2.substring(10,14)),Integer.parseInt(ID2.substring(10,14)+ID2.substring(15,17))});
    // if (this.Flag_DetectTheNoiseTags) return; this.TestingHoldingTags.add(ID1); this.TestingHoldingTags.add(ID2); 
} 
  public void _Testing_RemoveHoldingTag(String ID1, String ID2) { 
	this.StackedOrders3D.remove(Integer.parseInt(ID2.substring(10,14)+ID2.substring(15,17)));
  	// if (this.Flag_DetectTheNoiseTags) return; this.TestingHoldingTags.remove(ID1); this.TestingHoldingTags.remove(ID2);
  }
  public void printStackedOrders3D() {
    System.out.println("======= printStackedOrders3D() ==========================");
    for (Integer tmpID : this.StackedOrders3D.keySet()) {
      System.out.println(tmpID + " ::: " + ((int[])this.StackedOrders3D.get(tmpID))[0] + " " + ((int[])this.StackedOrders3D.get(tmpID))[1] + " " + ((int[])this.StackedOrders3D.get(tmpID))[2] + " " + ((int[])this.StackedOrders3D.get(tmpID))[3] + " " + ((int[])this.StackedOrders3D.get(tmpID))[4] + " " + ((int[])this.StackedOrders3D.get(tmpID))[5] + " " + ((int[])this.StackedOrders3D.get(tmpID))[6]);
    }
    System.out.println("=====================================================\n");
  }
  }