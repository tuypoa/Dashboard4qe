package lapfarsc.qe.dashboard.business;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import lapfarsc.qe.dashboard.dto.QeResumoDTO;

public class OutputQeBusiness {

	private DatabaseBusiness db = null;
	
	public OutputQeBusiness(DatabaseBusiness db){
			this.db = db;
	}
	
	public void processarArquivoOutput(QeResumoDTO qeResumoDTO, File arquivo) throws IOException{
		
		//localizar se ha leitura na tb "qeinfoscf" e qual ultimo "scfcycles"
		//considerar somente depois, senao, ler o cabecalho tb
		
		
		if(arquivo.exists() && arquivo.isFile()){
			
			FileReader fr = null;
		    BufferedReader br = null;
			try{
				StringBuilder conteudo = new StringBuilder();
				
				fr = new FileReader(arquivo);											
				br = new BufferedReader(fr);
		        int read, N = 1024;
		        char[] buffer = new char[N];
		        
		        //int i = 0;			        
		        while(true) {
		            read = br.read(buffer, 0, N);
		            String text = new String(buffer, 0, read);
		            
		            conteudo.append(text);
		            
		            //ENCONTRAR A POSICAO DO ARQUIVO QUE SERA ANALISADA
		          /*  if(conteudo){
		            	
		            }
		            */
		            if(read < N){
		            	break;
		            }		            
		        }
			}finally{
				if(br!=null) br.close();
				if(fr!=null) fr.close();
			}			
		}		
	}
	

}
