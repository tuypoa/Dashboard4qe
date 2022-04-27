package lapfarsc.qe.dashboard.business;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import lapfarsc.qe.dashboard.dto.QeInfoIterationDTO;
import lapfarsc.qe.dashboard.dto.QeInfoScfDTO;
import lapfarsc.qe.dashboard.dto.QeResumoDTO;

public class OutputQeBusiness {

	private DatabaseBusiness db = null;
	
	public OutputQeBusiness(DatabaseBusiness db){
			this.db = db;
	}
	
	public void processarArquivoOutput(QeResumoDTO qeResumoDTO, File arquivo) throws Exception{
		if(arquivo.exists() && arquivo.isFile()){
			//localizar se ha leitura na tb "qeinfoscf" e qual ultimo "scfcycles"			
			QeInfoScfDTO infoScfDTO = db.selectQeInfoScfDTOUltimoPeloResumo(qeResumoDTO.getCodigo());
			
			Integer numProcessadores = null;
			
			FileReader fr = null;
		    BufferedReader br = null;
			try{
				StringBuilder conteudo = new StringBuilder();
				
				fr = new FileReader(arquivo);											
				br = new BufferedReader(fr);
		        int read, N = 1024;
		        char[] buffer = new char[N];
		        
		        List<QeInfoScfDTO> listInfoSCF = new ArrayList<QeInfoScfDTO>();
		        List<QeInfoIterationDTO> listIteration = new ArrayList<QeInfoIterationDTO>();
		        int scfCiclo = 0;
		        String key = null;
		        while(true) {
		            read = br.read(buffer, 0, N);
		            String text = null;
		            try{
		            	text = new String(buffer, 0, read);
		            }catch(StringIndexOutOfBoundsException sioobe){
		            	text = null;
		            	System.out.println("> ERROR: "+ arquivo.getName());
		            	System.out.println(">>> "+sioobe.getMessage());
		            }
		            if(text!=null){
		            	conteudo.append(text);
		            }
		           
            		if(infoScfDTO == null && numProcessadores == null){
            			//ler cabecalho 
            			key = "running on ";
            			numProcessadores = Integer.parseInt(conteudo.substring(conteudo.indexOf(key)+key.length(), conteudo.indexOf("processors", conteudo.indexOf(key) )).trim());
            		}
            		
		            //ENCONTRAR A POSICAO DO ARQUIVO QUE SERA ANALISADA
		            //KEY: Writing
		            //CADA CICLO DE qeinfoscf CONCLUIDO *****
		            String keySplit = "Writing config-only to output data";
		            if(conteudo.indexOf(keySplit)!=-1){
		            	String[] pedacoCiclo = conteudo.toString().split(keySplit);
		            	if(pedacoCiclo.length>1){

	            			for (int i = 0; i < pedacoCiclo.length-1; i++) {
	            				//System.out.println(pedacoCiclo[i].length());
	            				String pc = pedacoCiclo[i];			            		
			            		key = "number of scf cycles    = ";
			            		boolean ultimo = pc.indexOf("Final enthalpy           = ")!=-1;
			            		
			            		if(ultimo || pc.indexOf(key)!=-1){
				            		scfCiclo = ultimo? scfCiclo+1 :  Integer.parseInt(pc.substring(pc.indexOf(key)+key.length(), pc.indexOf("\n", pc.indexOf(key) )).trim());				            		
				            		if(infoScfDTO == null || infoScfDTO.getScfCycles() < scfCiclo){
				            			//KEY: Self-consistent Calculation
				    		            //KEY: End of self-consistent calculation
				    		            //CONTEM OS qeinfoiteration DE CADA CICLO ****				            			
				            			String calc = pc.substring( pc.indexOf("     Self-consistent Calculation") , pc.indexOf("     End of self-consistent calculation") );
				            			String[] ci = calc.split("iteration #"); 
				            			for (int j = 1; j < ci.length; j++) {
				            				String pci = ci[j]; 
				            				QeInfoIterationDTO oi = new QeInfoIterationDTO();
				            				oi.setQeResumoCodigo(qeResumoDTO.getCodigo());
				            				oi.setScfCycles(scfCiclo);
				            				oi.setIteration( Integer.parseInt(pci.substring(0, pci.indexOf("ecut")).trim()));
				            				key = "total cpu time spent up to now is";
				            				oi.setCputime( Long.parseLong( pci.substring(pci.indexOf(key)+key.length(), pci.indexOf(".", pci.indexOf(key) ) ).trim()));
				            				listIteration.add(oi);
				            			}
				            			
				            			QeInfoScfDTO o = new QeInfoScfDTO();
					            		o.setQeResumoCodigo(qeResumoDTO.getCodigo());
					            		o.setScfCycles(scfCiclo);
					            		if(ultimo){
					            			key = " scf cycles and ";
					            			o.setBfgsSteps( Integer.parseInt(pc.substring(pc.indexOf(key)+key.length(), pc.indexOf("bfgs", pc.indexOf(key) )).trim()));
					            			key = "Final enthalpy           = ";
						            		o.setEnthalpy(Double.parseDouble(pc.substring(pc.indexOf(key)+key.length(), pc.indexOf("Ry", pc.indexOf(key) )).trim()));
					            		}else{
					            			key = "number of bfgs steps    = ";
					            			o.setBfgsSteps( Integer.parseInt(pc.substring(pc.indexOf(key)+key.length(), pc.indexOf("\n", pc.indexOf(key) )).trim()));
					            			key = "enthalpy           new  = ";
						            		o.setEnthalpy(Double.parseDouble(pc.substring(pc.indexOf(key)+key.length(), pc.indexOf("Ry", pc.indexOf(key) )).trim()));
					            		}					            		
					            		key = "new unit-cell volume = ";
					            		o.setVolume(Double.parseDouble(pc.substring(pc.indexOf("(",pc.indexOf(key))+1, pc.indexOf("Ang", pc.indexOf(key) )).trim()));
					            		key = "density = ";
					            		o.setDensity(Double.parseDouble(pc.substring(pc.indexOf(key)+key.length(), pc.indexOf("g", pc.indexOf(key) )).trim()));
					            		key = "convergence has been achieved in";
					            		o.setIterations( Integer.parseInt(pc.substring(pc.indexOf(key)+key.length(), pc.indexOf("iterations", pc.indexOf(key) )).trim()));
					            		key = "CELL_PARAMETERS";
					            		o.setCellparams( pc.substring(pc.indexOf(key), pc.indexOf("\n\n", pc.indexOf(key))));
					            		key = "ATOMIC_POSITIONS";
					            		o.setAtomicpositions( pc.substring(pc.indexOf(key), pc.indexOf("\n\n", pc.indexOf(key))));
					            		listInfoSCF.add(o);
				            		}
				            	}	
							}
		            		conteudo = new StringBuilder( conteudo.substring(conteudo.lastIndexOf(keySplit)+keySplit.length()) );
		            	}		            	
		            }
		            
		            if(read < N){
		            	break;
		            }		            
		        }
		        if(conteudo.indexOf("Writing all to output data")!=-1){ //ultimo ciclo
		        	qeResumoDTO.setConcluido( Boolean.TRUE );		
		        }
		        if(conteudo.indexOf("stopping ...")!=-1){
		        	String separador = " %%%%";
		        	String erro = conteudo.substring(conteudo.indexOf("\n",conteudo.indexOf(separador) )+1, 
		        					conteudo.indexOf(separador, conteudo.indexOf(separador)+separador.length()) );
		        	qeResumoDTO.setConcluido( Boolean.FALSE );	
		        	qeResumoDTO.setErro(erro);
		        }
		        
		        //atualiza 
		        if(numProcessadores!=null){
		        	qeResumoDTO.setQtdeCpu(numProcessadores);
		        	db.updateQeResumoDTOQtdeCpu(qeResumoDTO);
		        }
		        for (QeInfoScfDTO qescf : listInfoSCF) {
		        	db.incluirQeInfoScfDTO(qescf);
				}
		        for (QeInfoIterationDTO qeit : listIteration) {
		        	db.incluirQeInfoIterationDTO(qeit);
				}
		        System.out.println("OUTPUT: "+arquivo.getName() +" > OK");
		        
		        qeResumoDTO.setProcessar(Boolean.FALSE);
		        db.updateQeResumoDTOSituacao(qeResumoDTO);
			}finally{
				if(br!=null) br.close();
				if(fr!=null) fr.close();
			}			
		}		
	}
	

}
