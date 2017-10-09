import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class Output {
	
	Map<String,JSONArray> lastResult;
    JSONArray unluckyStudent;
    JSONArray admitted;
    JSONArray unluckyDepartment;
    
    Output(){
    	lastResult = new HashMap<String, JSONArray>();
    	unluckyStudent = new JSONArray();
    	admitted = new JSONArray();
    	unluckyDepartment = new JSONArray();
    }
	
	public void Print() throws Exception{
		
        PrintStream ps = new PrintStream("./output.txt");
        System.setOut(ps);//把创建的打印输出流赋给系统。即系统下次向 ps输出
        
        String c1 = "unlucky_student";
        unluckyStudent = getUnluckStudent();
        lastResult.put(c1, unluckyStudent);
        
        
        String c2 = "admitted";
        admitted = getAdmitted();
        lastResult.put(c2, admitted);
        
        String c3 = "unlucky_department";
        unluckyDepartment = getUnluckyDepartment();
        lastResult.put(c3, unluckyDepartment);
		 
		
        JSONObject mapJson = new JSONObject(lastResult);
		System.out.println(mapJson);
		
		
		
	}

	private JSONArray getAdmitted() {
		// TODO Auto-generated method stub
		JSONArray tmp = new JSONArray();
		int num = Match.result.size();
		JSONObject[] match = new JSONObject[num];
		
		int i = 0;
	    for (Map.Entry<String, ArrayList<String>> entry : Match.result.entrySet()) {
	    	match[i] = new JSONObject();
	    	
	    	String s = entry.getKey();
	    	ArrayList<String> arr = entry.getValue();
            
			match[i].put("department_no", s);
			match[i].put("member", arr);
			tmp.put(match[i]);
	        i++;
	    }
	    
		return tmp;
	}

	private JSONArray getUnluckyDepartment() {
		
		ArrayList<String> dno = new ArrayList<String>();
		for(int i = 0; i < Input.depa.length; i++){
			if(Input.depa[i].getMemberLimit() == Input.depa[i].getNumRemaining()){
				dno.add(Input.depa[i].getNo());
			}
		    
		}
		JSONArray tmp = new JSONArray(dno);
		return tmp;
	}

	private JSONArray getUnluckStudent() {
		
		ArrayList<String> sno = new ArrayList<String>();
		for(int i = 0; i < Input.stu.length; i++){
			if(Input.stu[i].getNumAdmit() == 0){
				sno.add(Input.stu[i].getNo());
			}
		    
		}
		JSONArray tmp = new JSONArray(sno);
		return tmp;
	}

}
