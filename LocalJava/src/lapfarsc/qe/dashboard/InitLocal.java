/**
 * @author tuypoa
 *
 *
 */
package lapfarsc.qe.dashboard;

import java.sql.Connection;
import java.sql.DriverManager;

import lapfarsc.qe.dashboard.business.HeadBusiness;
import lapfarsc.qe.dashboard.business.Slave1Business;
import lapfarsc.qe.dashboard.util.Dominios.ArgTypeEnum;

import org.postgresql.util.PSQLException;

public class InitLocal {

	private static String POSTGRES_ADDRESS = "192.168.0.100:5432";

	public static void main(String[] args) throws Exception{
		/*
		 ARGS:
		 0 = TIPO DE JAR (HEAD OU SLAVE1) 
		 */
		if(args==null || args.length < 1) {
			System.out.println("--> Arg0 IS REQUIRED.");
			return;
		}
		
		//TODO VERIFICAR SE JA ESTA EXECUTANDO
		
		
		
		ArgTypeEnum execType = ArgTypeEnum.getByName( args[0] );
		if(execType != null){				
			//ACESSAR POSTGRES
			String url = "jdbc:postgresql://"+POSTGRES_ADDRESS+"/dashboard4qe?user=postgres&password=postgres";
			Connection conn = null;
			try {	
				try {
					conn = DriverManager.getConnection(url);
				} catch (PSQLException ce) {
					if(ce.getMessage().indexOf("refused")!=-1){
						conn = DriverManager.getConnection(url.replace(POSTGRES_ADDRESS, "localhost"));	
					}else{
						throw ce;
					}
				}
				//iniciar tarefa
				switch (execType) {
				case HEAD:
					HeadBusiness head = new HeadBusiness(conn);
					head.acessarTodasMaquinas();						
					break;
				case SLAVE1:						
					Slave1Business slave1 = new Slave1Business(conn, Integer.parseInt(args[1]));
					slave1.gravarJarLeitura();
					//slave1.lerTodosProcessos();
					//slave1.analisarTodosOutputs();
					break;						
				default:
					System.out.println("--> Arg0 NOT FOUND.");
					break;
				}
			}finally{
				if(conn!=null) conn.close();
				System.out.println(execType.getArg() + " END");
			}
		}else{
			System.out.println("--> Arg0 NOT FOUND.");
		}
	}		
}