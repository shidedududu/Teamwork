import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONObject;

public class CustomInput {
	
	private Random random = new Random();
	private static final int SNUM =300;
	private static final int DNUM = 20;
	private String[] sno = new String[SNUM];
	private String[] dno = new String[DNUM];
//	private ArrayList<String> time = new ArrayList<String>();
//	private ArrayList<String> tags = new ArrayList<String>();
	
	public void init(){
		for(int i = 0; i < SNUM; i++) {
			int tmp = ((i / 50) + 1) * 100 + i % 50 + 1; 
			String s = String.valueOf(tmp);
			sno[i] = "031502"+s;	
		}
		
		for(int i = 0; i < DNUM; i++) {
			String s = String.valueOf(i);
			if(i<10) {
				dno[i] = "D00"+s;
			}
			else {
				dno[i] = "D0"+s;
				
			}
		}
		
	}
	
	
	public void customInput(){
		Map<String,JSONArray>map;
		JSONArray stuArr;
//	    JSONArray depaArr;
		
		map = new HashMap<String, JSONArray>();
		
		stuArr = new JSONArray();
		stuArr = getStuArr();
		map.put("students", stuArr);
		
		JSONObject mapJson = new JSONObject(map);
		System.out.println(mapJson);
		
	}

	private JSONArray getStuArr() {
		
		JSONArray tmp = new JSONArray();
		JSONObject[] stuObj= new JSONObject[SNUM];
		
		for(int i=0; i < SNUM; i++) {
			stuObj[i] = new JSONObject();
			
			int depaMember = random.nextInt(5)+1;
			String[] depa = getDepa(depaMember);
			
			stuObj[i].put("student_no", sno[i]);
			stuObj[i].put("applications_department", depa);
			tmp.put(stuObj[i]);
		}

		return tmp;
	}

	private String[] getDepa(int depaMember) {
		String[] tmp = new String[depaMember];
		for(int i = 0; i < depaMember; i++){
			int index = random.nextInt(20);
            tmp[i] = dno[index];

		}
		return tmp;
	}


	

}
