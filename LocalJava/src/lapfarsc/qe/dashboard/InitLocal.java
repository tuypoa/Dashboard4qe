/**
 * @author tuypoa
 *
 *
 */
package lapfarsc.qe.dashboard;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;

import lapfarsc.qe.dashboard.business.HeadBusiness;
import lapfarsc.qe.dashboard.business.Slave1Business;
import lapfarsc.qe.dashboard.util.Dominios.ArgTypeEnum;

public class InitLocal {

	private static String POSTGRES_ADDRESS = "192.168.0.106:5432";
	public static String PATH_MONITORAMENTO = "05-quantum/PW-output/";
	
	public static void main(String[] args) throws Exception{
		/*
		 ARGS:
		 0 = TIPO DE JAR (HEAD OU SLAVE1) 
		 */
		if(args==null || args.length < 1) {
			System.out.println("--> Arg0 IS REQUIRED.");
			return;
		}
		
		//VERIFICAR SE JA ESTA EXECUTANDO
		Runtime run = Runtime.getRuntime();
		Process pr = run.exec("ps aux");
		pr.waitFor();
		BufferedReader buf = new BufferedReader(new InputStreamReader(pr.getInputStream()));
		String line = "";
		int i = 0;
		while ((line=buf.readLine())!=null) {
			if(line.indexOf("Dashboard4qe")!=-1){
				if(i==1){
					System.out.println("--> THERE IS ANOTHER PROCESS STILL RUNNING.");
					return;
				}
				i++;
			}
		}
				
		ArgTypeEnum execType = ArgTypeEnum.getByName( args[0] );
		if(execType != null){				
			//ACESSAR POSTGRES
			String url = "jdbc:postgresql://"+POSTGRES_ADDRESS+"/dashboard4qe?user=postgres&password=postgres";
			Connection conn = null;
			try {
				conn = DriverManager.getConnection(url);				
				//iniciar tarefa
				switch (execType) {
				case HEAD:
					HeadBusiness head = new HeadBusiness(conn);
					head.acessarTodasMaquinas();						
					break;
				case SLAVE1:						
					Slave1Business slave1 = new Slave1Business(conn, Integer.parseInt(args[1]));
					slave1.lerTodosProcessos();
					if( slave1.analisarTodosOutputs() ){ //se teve novidades
						slave1.gravarJarLeitura();
					}	
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