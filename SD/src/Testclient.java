/*
public class JSONTest {  
    public static void main(String[] args) throws Exception {  
        JSONTokener jsonTokener = new JSONTokener(new FileReader(new File("json.txt")));  
        JSONArray jsonArray = new JSONArray(jsonTokener);//获取整个json文件的内容，因为最外层是数组，所以用JSONArray来构造  
        System.out.println(jsonArray);  
          
        //这个JSONArray数组只包含一个JSONObject对象，标为jsonObject1  
        JSONObject jsonObject1 = jsonArray.getJSONObject(0);  
        System.out.println(jsonObject1);   
          
        //jsonObject1只包含一个institute字段，这里获取这个字段内容赋给jsonObject2  
        JSONObject jsonObject2 = jsonObject1.getJSONObject("institute");  
        System.out.println(jsonObject2);  
          
        //jsonObject2包含name字段和grade字段，grade字段对应的是一个JSONArray数组  
        String valueOfname = jsonObject2.getString("name");  
        System.out.println(valueOfname);  
        JSONArray jsonArray2 = jsonObject2.getJSONArray("grade");  
        System.out.println(jsonArray2);  
          
        //jsonArray2数组包含3个对象，每个对象里面有name字段和class字段，这里获取第二个对象  
        JSONObject jsonObject3 = jsonArray2.getJSONObject(1);  
        System.out.println(jsonObject3);  
          
        //然后从jsonObject3对象里获取class字段对应的JSONArray数组  
        JSONArray jsonArray3 = jsonObject3.getJSONArray("class");  
        System.out.println(jsonArray3);  
          
        //下面直接获取no.等于3的students数量，过程都一样  
        int num = jsonArray3.getJSONObject(2).getInt("students");  
        System.out.println("最后获取的结果为：" + num);  
    }  
}  

*/