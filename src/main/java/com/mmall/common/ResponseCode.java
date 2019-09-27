package com.mmall.common;

public enum ResponseCode {
		SUCCESS(0,"SUCCESS"),
		ERROR(1,"ERROR"),
		NEED_LOGIN(10,"NEED_LOGIN"),
		ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT");
	
		private final int code;
		private final String desc;

		ResponseCode(int code,String desc){
			this.code = code;
			this.desc = desc;
		}
		
		public int getCode() {
			return code;
		}
		public String getDesc() {
			return desc;
		}
}


//public final class Enum extends Enum
//{
//
//	public static final Enum Success;
//	public static final Enum ERROR;
//	private final int code;
//	private final String decs;
//	private static final Enum ENUM$VALUES[];
//
//	private Enum(String s, int i, int code, String desc)
//	{
//		super(s, i);
//		this.code = code;
//		decs = desc;
//	}
//
//	public static Enum[] values()
//	{
//		Enum aenum[];
//		int i;
//		Enum aenum1[];
//		System.arraycopy(aenum = ENUM$VALUES, 0, aenum1 = new Enum[i = aenum.length], 0, i);
//		return aenum1;
//	}
//
//	public static Enum valueOf(String s)
//	{
//		return (Enum)Enum.valueOf(package1/Enum, s);
//	}
//
//	static 
//	{
//		Success = new Enum("Success", 0, 0, "success");
//		ERROR = new Enum("ERROR", 1, 1, "error");
//		ENUM$VALUES = ,m(new Enum[] {
//			Success, ERROR
//		});
//	}
//}