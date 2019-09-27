package dome;

import java.util.Date;
import java.util.UUID;

import com.mmall.common.ServerResponse;

public class DAOTest {
	public static void main(String[] args) {
		System.out.println(ServerResponse.creatBySuccess("msg", new Date()).objectToJson(ServerResponse.creatBySuccess("msg", new Date())));
		//{"status":0,"msg":"msg","data":1565277752727,"success":true}没有@JsonIgnore
		
		//System.out.println(UUID.randomUUID().toString());
		
		Integer num = new Integer(3);
		System.out.println(null == num);//false
				//num = new Integer("");//异常
		System.out.println(null == num);
				num = null;
		System.out.println(null == num);//ture
	}
}
