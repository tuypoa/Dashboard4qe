/**
 * @author tuypoa
 *
 *
 */
package lapfarsc.qe.dashboard.business;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;

public class Slave1Business {
	
	private Connection conn = null;
	
	public Slave1Business(Connection conn){
		this.conn = conn;
	}
	
	
	public int readAll() {
		int count = 0;
		
		return count;
	}
		
	public String loadTextFile(File file) throws IOException {
		FileReader fr = null;
	    BufferedReader br = null;
		try{
			StringBuilder conteudo = new StringBuilder();
			fr = new FileReader(file);											
			br = new BufferedReader(fr);
	        int read, N = 1024;
	        char[] buffer = new char[N];
	        
	        //int i = 0;			        
	        while(true) {
	            read = br.read(buffer, 0, N);
	            String text = new String(buffer, 0, read);
	            conteudo.append(text);
	            if(read < N){
	            	if(conteudo.length()>0){
	            		return conteudo.toString();
	            	}
	            	break;
	            }		            
	        }
		}finally{
			if(br!=null) br.close();
			if(fr!=null) fr.close();
		}
		return null;
	}

	public void saveTextFile(File fileOutput, String conteudo) throws IOException {
		FileOutputStream fos = null;
		try{
			fos = new FileOutputStream(fileOutput);
			fos.write(conteudo.getBytes());
			fos.flush();
		}finally{
			if(fos!=null) fos.close();
		}
	}
	
}
