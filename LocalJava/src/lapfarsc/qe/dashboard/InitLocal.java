/**
 * @author tuypoa
 *
 *
 */
package lapfarsc.qe.dashboard;

import java.io.File;
import java.io.FileFilter;

public class InitLocal {


	public static void main(String[] args) throws Exception{
		int countLidos = 0;
		
		try {
			
			String rootPath = ""; //from db
			
			File path = new File(rootPath);
			
			if(!path.exists()) {
				System.out.println("--> PATH DOES NOT EXIST: "+path.getAbsolutePath());
				return;
			}
			
			File[] arquivos = path.listFiles(new FileFilter() {	
				@Override
				public boolean accept(File arg0) {
					return  arg0.getName().toLowerCase().endsWith(".out");
				}
			});
			
			if(arquivos==null || arquivos.length == 0) {
				//System.out.println("--> FILE NOT FOUND: "+path.getAbsolutePath());
				return;
			}
			
			if(arquivos!=null && arquivos.length > 0) {
				for (File file : arquivos) {
					
					
					//break;
				}
			}
		}finally {
			//System.out.println( ">> "+ countLidos + " READ OK.");
			System.out.println("END.");	
		}
	}
	
	
	
	
}
