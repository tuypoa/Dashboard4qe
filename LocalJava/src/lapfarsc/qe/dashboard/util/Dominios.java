package lapfarsc.qe.dashboard.util;

public class Dominios {

	
	public enum ArgTypeEnum{
		HEAD("HEAD"),
		SLAVE1("SLAVE1");
		String arg;
		ArgTypeEnum(String arg){this.arg=arg;}
		public String getArg(){return this.arg;}
		public static ArgTypeEnum getByName(String index){
			for (ArgTypeEnum e : ArgTypeEnum.values()) {
				if( e.arg.equals( index ) ){
					return e;
				}
			}
			return null;
		}
	}
}
