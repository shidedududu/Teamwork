import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Match {
	
	public static Map<String, ArrayList<String>> result;
	private static ArrayList<String>[] resultList;
	private static Map<String, ArrayList<String>> map;
    private static ArrayList<String>[] list;
    private static Map<String, Integer> stuMap;
    
    Match(){
    	result = new HashMap<String, ArrayList<String>>();
    	resultList= new ArrayList[20];
    	map = new HashMap<String, ArrayList<String>>();
	    list= new ArrayList[20];
	    stuMap = new HashMap<String, Integer>();
    }

	public void matching() throws Exception{
	    
	    //将学生学号与下标映射存入stuMap
	    for(int i = 0; i < Input.stu.length; i++) {
	    	String no = Input.stu[i].getNo();
	    	stuMap.put(no, i);
	    }
	    
	    //每个部门对应一个List存报名学生学号
	    for(int i=0; i < Input.depa.length; i++) {
	    	list[i] = new ArrayList<String>();
	    	resultList[i] = new ArrayList<String>();
	    	String no = Input.depa[i].getNo();
	    	
	    	map.put(no, list[i]);
	    	result.put(no, resultList[i]);
	    }
	    
	    for(int i = 0; i < 5; i++) {
	    	matchProcess(i);
	    	
            clearList();
	    }
	    
	    //将没有接受到学生的部门删除
	    for(int i = 0; i < Input.depa.length; i++){
	    	String dno = Input.depa[i].getNo();
	    	if(result.get(dno).size() == 0){
	    		result.remove(dno);
	    	}
	    }
	    
//	    System.out.println(result);
	   
	    
	}

	private void clearList() {
		// TODO Auto-generated method stub
		for(int j = 0; j < Input.depa.length; j++) {
    		list[j].clear();
    	}
	}

	private void matchProcess(int index) {
		
		for(int j = 0; j < Input.stu.length; j++) {
			
    		if(Input.stu[j].getDeptments().length-1 >= index) {
    			String dept = Input.stu[j].getDeptments()[index];
	    	    String no = Input.stu[j].getNo();
	    	    //判断时间是否冲突，如果不冲突，则添加
	    	    
	    	    if(isFreeTimeConflict(index,dept) == false){
	    			map.get(dept).add(no);
	    	    }
    		}
    		
    	}
		
		//对时间不冲突的学生与部门，第二次筛选
		for(int i = 0; i < Input.depa.length; i++) {
			String dno = Input.depa[i].getNo();
			int num = Input.depa[i].getNumRemaining();
			ArrayList<String> tmp = map.get(dno);//报名学生学号List
			int newNum = 0;
			
			//如果部门剩余人数大于或者等于学生报名人数,全部录取
			if(num >= 0){
				if(num >= tmp.size()) {
					newNum = num - tmp.size();
					result.get(dno).addAll(tmp);
					Input.depa[i].setNumRemaining(newNum);
					dealNumAdmit(tmp);
				}
				//如果剩余人数小于报名人数，根据匹配标签比率大小选取
				else {
					//根据标签删除一些人，传递部门下标，学生学号List，人数
					tmp = filter(i,tmp,num);
					newNum = num - tmp.size();
					result.get(dno).addAll(tmp);
					Input.depa[i].setNumRemaining(newNum);
					dealNumAdmit(tmp);
				}
			}
			
		}
		
//		System.out.println(map);
//		System.out.println(result);
	
    }

	private ArrayList<String> filter(int index, ArrayList<String> tmp, int num) {
		
		//将学生学号，按照标签匹配排序，标签匹配为匹配标签数目除以学生总标签
		String[] stuNo = (String[])tmp.toArray(new String[tmp.size()]);
		double[] rate = new double[tmp.size()];
		String[] depaTags = Input.depa[index].getTags();
		
		for(int i = 0 ; i < stuNo.length; i++){
			String sno = stuNo[i];
			int indexx = stuMap.get(sno);
			String[] stuTags = Input.stu[indexx].getTags();//得到学生标签
			
			rate[i] = getRate(depaTags,stuTags);
		}
		stuNo = mysort(rate,stuNo);
		
		ArrayList<String> arrayList = new ArrayList<String>(Arrays.asList(stuNo));
		//淘汰末尾元素
		num = arrayList.size() - num;
		for(int i = 0; i < num; i++){
			int a = arrayList.size() - 1;
			arrayList.remove(a);
		}

		return arrayList;
	}

	private String[] mysort(double[] rate, String[] stuNo) {
		
		for(int i = 0; i < rate.length-1; i++){
			int k = i;
			for(int j = i+1; j<rate.length; j++){
				if(rate[j] > rate[k]) k=j;
			}
			if(k!=i){
				double temp1;
				temp1=rate[k];
				rate[k]=rate[i];
				rate[i]=temp1;
				
				String temp2;
				temp2=stuNo[k];
				stuNo[k]=stuNo[i];
				stuNo[i]=temp2;
				
			}
			
		}
		
		return stuNo;
	}

	private double getRate(String[] depaTags, String[] stuTags) {
		// TODO Auto-generated method stub
		int num1 = depaTags.length;
		int num2 = stuTags.length;
		int num = 0;
		for(int i = 0; i < num1; i++) {
			for(int j = 0; j < num2; j++) {
				if(depaTags[i].equals(stuTags[j])){
					num++;
				}
			}
		}
		double rate = (double)num/num2;
		return rate;
	}

	private void dealNumAdmit(ArrayList<String> tmp) {
		for(int i = 0; i < tmp.size(); i++){
			String sno = tmp.get(i);
			int index = stuMap.get(sno);
			int num = Input.stu[index].getNumAdmit();
			Input.stu[index].setNumAdmit(num + 1);
		}
		
	}

	private boolean isFreeTimeConflict(int index, String string) {
	    
		int[][] stuFreeTime = Input.stu[index].getDateTime();
		int[][] depFreeTime = null;
		
		//根据部门编号得到部门例会时间
		for(int i = 0; i<Input.depa.length; i++) {
			
			if(string.equals(Input.depa[i].getNo())) {
				depFreeTime = Input.depa[i].getDateTime();
				break;
			}
		}
		
		for(int i = 0; i<= Input.depa[i].getDateTime().length; i++) {
			boolean isInclude = false;
			for(int j=0; j<stuFreeTime.length; j++) {
				if(stuFreeTime[j][1] != 0){
					if(stuFreeTime[j][0] <= depFreeTime[i][0]&&stuFreeTime[j][1] >= depFreeTime[i][1]){
					    isInclude = true; 
					    break;
				    }
				}
				if(isInclude == false){
					return false;
				}
			}
		}
		
		return true;
	}
    
	/*
	public static void main(String[] args) throws Exception {

		Match match = new Match();
		match.tryy();
		
	}
	*/
}






	