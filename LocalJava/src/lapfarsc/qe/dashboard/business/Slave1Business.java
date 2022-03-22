/**
 * @author tuypoa
 *
 *
 */
package lapfarsc.qe.dashboard.business;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import lapfarsc.qe.dashboard.InitLocal;
import lapfarsc.qe.dashboard.dto.CmdTopDTO;
import lapfarsc.qe.dashboard.dto.ComandoDTO;
import lapfarsc.qe.dashboard.dto.JarLeituraDTO;
import lapfarsc.qe.dashboard.dto.MaquinaQeArquivoInDTO;
import lapfarsc.qe.dashboard.dto.MaquinaDTO;
import lapfarsc.qe.dashboard.dto.MoleculaDTO;
import lapfarsc.qe.dashboard.dto.PsauxDTO;
import lapfarsc.qe.dashboard.dto.QeArquivoInDTO;
import lapfarsc.qe.dashboard.dto.QeResumoDTO;
import lapfarsc.qe.dashboard.util.Dominios.ComandoEnum;

import org.apache.commons.codec.digest.DigestUtils;

public class Slave1Business {
	
	private DatabaseBusiness db = null;
	private MaquinaDTO maquinaDTO = null;
	
	public Slave1Business(Connection conn, Integer maqId) throws Exception{
		this.db = new DatabaseBusiness(conn);
		this.maquinaDTO = db.selectMaquinaDTO(maqId);
	}

	private void gravarJarLeitura() throws Exception {
		Process process = Runtime.getRuntime().exec("top -bn1");
		int exitCode = process.waitFor();
		if (exitCode == 0) {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = "";
			String output = "";
			int i=0;
			while((line = bufferedReader.readLine()) != null){
				output += line + "\n";
				if(i++==5) break;
			}
			
			CmdTopDTO cmdDTO = HeadBusiness.getCommandTopInfos(output);
			JarLeituraDTO jlDTO = new JarLeituraDTO();
			jlDTO.setCpuUsed( cmdDTO.getCpuUsed() );
			jlDTO.setMemUsed( cmdDTO.getMemUsed() );
			jlDTO.setMaquinaCodigo(maquinaDTO.getCodigo());
			db.incluirJarLeituraDTO(jlDTO);
		}
	}
	
	public void lerTodosProcessos() throws Exception {
		List<ComandoDTO> listComandoDTO = db.selectListComandoDTO();		
		Process process = Runtime.getRuntime().exec("ps aux");
		int exitCode = process.waitFor();
		if (exitCode == 0) {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = "";
			HashMap<ComandoEnum,List<String>> map = new HashMap<ComandoEnum,List<String>>();
			while((line = bufferedReader.readLine()) != null){
				for (ComandoDTO comandoDTO : listComandoDTO) {
					int poscmd = line.indexOf(comandoDTO.getPrefixo());					
					if(poscmd!=-1){
						ComandoEnum tipo = ComandoEnum.getByIndex(comandoDTO.getCodigo()); 
						switch (tipo) {
						case MPIRUN_PW:
						case PW:						
							List<String> l = map.get(tipo);
							if(l==null){
								l = new ArrayList<String>();
								map.put(tipo, l);
							}
							//System.out.println(line);
							l.add(line);							
							break;
						case JAVA_JAR:
							break;
						}
						break;
					}
				}
			}
			//PROCESSAR LINHAS DO PS AUX
			List<PsauxDTO> listPsauxDTO = new ArrayList<PsauxDTO>();
			//System.out.println(map.get(ComandoEnum.MPIRUN_PW).size());
			db.updateNaoExecutandoTodosQeResumoDTO(maquinaDTO.getCodigo());
			Set<ComandoEnum> s = map.keySet();
			for (ComandoEnum cs : s) {
				List<String> l = map.get(cs);
				for (String p : l) {
					while(p.indexOf("  ")!=-1){
						p = p.replaceAll("  ", " ");
					}
					//user 17962 0.0 0.0 88188 5228 pts/0 S+ Mar15 0:47 mpirun -np 8 pw.x -in arquivo.in
					String col[] = p.split(" ");
					PsauxDTO dto = new PsauxDTO();
					dto.setMaquinaCodigo(maquinaDTO.getCodigo());
					dto.setComandoCodigo( cs.getIndex() );
					dto.setPid( Integer.parseInt(col[1]) );
					dto.setUid( col[0].trim() );
					dto.setCpu( BigDecimal.valueOf( Double.parseDouble(col[2].replace(",","."))) );
					dto.setMem( BigDecimal.valueOf( Double.parseDouble(col[3].replace(",","."))) );
					dto.setConteudo( p.substring( p.indexOf(col[9])+col[9].length()+1 ) );
					QeArquivoInDTO arquivoInDTO = localizarArquivoInDTOPeloNome( col[col.length-1]);
					if(arquivoInDTO!=null){
						dto.setQeArquivoInCodigo(arquivoInDTO.getCodigo());
						QeResumoDTO resumoDTO = localizarResumoDTO(arquivoInDTO);
						if(resumoDTO!=null){
							dto.setQeResumoCodigo(resumoDTO.getCodigo());
						}
					}
					listPsauxDTO.add(dto);
				}
			}		
			db.incluirListPsauxDTO(listPsauxDTO);
			
			
		}
	}
	
	private QeArquivoInDTO localizarArquivoInDTOPeloNome(String nome) throws Exception{
		//ENCONTRAR MOLECULA no nome do arquivo
		MoleculaDTO moleculaDTO = null;
		List<MoleculaDTO> listMoleculaDTO = db.selectListMoleculaDTO();
		for (MoleculaDTO m : listMoleculaDTO) {
			if(nome.indexOf(m.getNome().toLowerCase())!=-1){
				moleculaDTO = m;
				break;
			}
		}
		if(moleculaDTO!=null){
			String rootPathArquivo = maquinaDTO.getRootPath()+File.separator+
					moleculaDTO.getNome()+File.separator+
					InitLocal.PATH_MONITORAMENTO; 
			String filename = rootPathArquivo+nome;
			
			File arquivoIn = new File(filename);
			if(arquivoIn.exists() && arquivoIn.isFile()){		
				InputStream is = Files.newInputStream(Paths.get(filename));
				String hashArq = DigestUtils.md5Hex(is);				
				is.close();
				
				String hashMaqArq = DigestUtils.md5Hex( maquinaDTO.getCodigo() +";"+ hashArq );				
				QeArquivoInDTO arquivoInDTO = db.selectQeArquivoInDTOPeloHash(hashMaqArq);
				if(arquivoInDTO==null){					
					arquivoInDTO = new QeArquivoInDTO();
					arquivoInDTO.setHashMaqArq(hashMaqArq);
					arquivoInDTO.setNome(nome);
					arquivoInDTO.setDescricao(parseNomeArquivoIn(nome));
					arquivoInDTO.setMoleculaCodigo(moleculaDTO.getCodigo());
					arquivoInDTO.setConteudo(loadTextFile(arquivoIn));
					db.incluirQeArquivoInDTO(arquivoInDTO);
				}
				arquivoInDTO = db.selectQeArquivoInDTOPeloHash(hashMaqArq);
				
				//incluir vinculo com a maquina
				MaquinaQeArquivoInDTO maDTO = db.selectMaquinaQeArquivoInDTO(maquinaDTO.getCodigo(), arquivoInDTO.getCodigo());
				if(maDTO==null){
					maDTO = new MaquinaQeArquivoInDTO();
					maDTO.setMaquinaCodigo(maquinaDTO.getCodigo());
					maDTO.setQeArquivoInCodigo(arquivoInDTO.getCodigo());
					maDTO.setHashArqIn(hashArq);
					maDTO.setRootPath( rootPathArquivo );
					db.incluirMaquinaQeArquivoInDTO(maDTO);
				}
				//System.out.println(parseNomeArquivoIn(nome));
				return arquivoInDTO;
			}
		}
		return null;
	}
	
	private String parseNomeArquivoIn(String nome){
		//UC-default_00_1macitentan-1maltitol-VEMRAI.in
		try{ 
			StringBuilder sb = new StringBuilder();
			nome = nome.replace("UC-", "").replace(".in", "");
			String s2 = nome.substring(nome.indexOf("_",nome.indexOf("_")+1)+1 );
			sb.append( s2.substring(0,1) ).append(" ");
			sb.append( s2.substring(1,2).toUpperCase() ).append(s2.substring(2, s2.indexOf("-")) );//Macitentan
			if(s2.indexOf("-", s2.indexOf("-")+1) != -1){
				sb.append(" : ").append( s2.substring(s2.indexOf("-")+1,s2.indexOf("-")+2) ).append(" ");
				sb.append( s2.substring(s2.indexOf("-")+2, s2.indexOf("-")+3).toUpperCase() ).append( s2.substring(s2.indexOf("-")+3, s2.indexOf("-", s2.indexOf("-")+1) ) );//Maltitol
			}
			String s1 = nome.substring(0, nome.indexOf("_"));
			s1 = s1.substring(0,1).toUpperCase() + s1.substring(1);
			sb.append(" (").append( s1 ).append(")"); //(Default)
			return sb.toString();
		}catch (Exception e){
			return nome.replace("UC-", "").replace(".in", "");	
		}		
	}
	
	
	private QeResumoDTO localizarResumoDTO(QeArquivoInDTO arquivoInDTO) throws Exception{
		MaquinaQeArquivoInDTO maDTO = db.selectMaquinaQeArquivoInDTO(maquinaDTO.getCodigo(), arquivoInDTO.getCodigo());
		String nome = arquivoInDTO.getNome().substring(0, arquivoInDTO.getNome().lastIndexOf("."))+".out";
		QeResumoDTO resumoDTO = db.selectQeResumoDTOPeloNome(arquivoInDTO.getCodigo(), nome);
		
		String filename = maDTO.getRootPath()+nome;
		
		File arquivoOut = new File(filename);
		if(arquivoOut.exists() && arquivoOut.isFile()){		
			InputStream is = Files.newInputStream(Paths.get(filename));
			String md5 = DigestUtils.md5Hex(is);
			is.close();			
			double tamanhoKb = (double) ((double) Files.size(Paths.get(filename))) / 1024;
			
			if(resumoDTO==null){
				resumoDTO = new QeResumoDTO();
				resumoDTO.setQeArquivoInCodigo(arquivoInDTO.getCodigo());
				resumoDTO.setHashOutput(md5);
				resumoDTO.setNome(nome);
				resumoDTO.setTamanhoKb(tamanhoKb);
				db.incluirQeResumoDTO(resumoDTO);
			}else if(!resumoDTO.getHashOutput().equals(md5)){
				//atualizar hashoutput , tamanhokb, executando
				resumoDTO.setHashOutput(md5);
				resumoDTO.setTamanhoKb(tamanhoKb);
				db.updateQeResumoDTOHash(resumoDTO);
			}else{
				db.updateQeResumoDTOExecutando(resumoDTO);
			}
		}
		return resumoDTO;
	}
	
	public void analisarTodosOutputs() throws Exception {
		OutputQeBusiness ob = new OutputQeBusiness(db);
		
		//analisar os em andamento: processar TRUE
		List<QeResumoDTO> listResumoDTO = db.selectQeResumoDTOAProcessar();
		for (QeResumoDTO qeResumoDTO : listResumoDTO) {				
			MoleculaDTO mol = db.selectMoleculaDTO(db.selectQeArquivoInDTO( qeResumoDTO.getQeArquivoInCodigo()).getMoleculaCodigo());				
			String filename = maquinaDTO.getRootPath()+File.separator+
					mol.getNome()+File.separator+
					InitLocal.PATH_MONITORAMENTO+qeResumoDTO.getNome();
			File arquivoOut = new File(filename);
			ob.processarArquivoOutput(qeResumoDTO, arquivoOut);
		}
		
		//TODO ATUALIZAR OS CONCLUIDOS ou INTERROMPIDOS
		//listar todos .OUT e verificar hash do database
		
		if(listResumoDTO.size()>0 ){ //OU TEVE concluidos 
			this.gravarJarLeitura();
		}
	}
	
	private String loadTextFile(File file) throws IOException {
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
}

