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

import org.apache.commons.codec.digest.DigestUtils;

import lapfarsc.qe.dashboard.InitLocal;
import lapfarsc.qe.dashboard.dto.CmdTopDTO;
import lapfarsc.qe.dashboard.dto.ComandoDTO;
import lapfarsc.qe.dashboard.dto.JarLeituraDTO;
import lapfarsc.qe.dashboard.dto.MaquinaDTO;
import lapfarsc.qe.dashboard.dto.MoleculaDTO;
import lapfarsc.qe.dashboard.dto.PsauxDTO;
import lapfarsc.qe.dashboard.dto.QeArquivoInDTO;
import lapfarsc.qe.dashboard.dto.QeResumoDTO;
import lapfarsc.qe.dashboard.util.Dominios.ComandoEnum;

public class Slave1Business {
	
	private DatabaseBusiness db = null;
	private MaquinaDTO maquinaDTO = null;
	
	public Slave1Business(Connection conn, Integer maqId) throws Exception{
		this.db = new DatabaseBusiness(conn);
		this.maquinaDTO = db.selectMaquinaDTO(maqId);
	}

	public void gravarJarLeitura() throws Exception {
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
			String filename = maquinaDTO.getRootPath()+File.separator+
					moleculaDTO.getNome()+File.separator+
					InitLocal.PATH_MONITORAMENTO+nome;
			
			File arquivoIn = new File(filename);
			if(arquivoIn.exists() && arquivoIn.isFile()){		
				InputStream is = Files.newInputStream(Paths.get(filename));
				String md5 = DigestUtils.md5Hex(is);
				is.close();
				
				QeArquivoInDTO arquivoInDTO = db.selectQeArquivoInDTOPeloHash(md5);
				if(arquivoInDTO==null){
					arquivoInDTO = new QeArquivoInDTO();
					arquivoInDTO.setHash(md5);
					arquivoInDTO.setNome(nome);
					arquivoInDTO.setMoleculaCodigo(moleculaDTO.getCodigo());
					arquivoInDTO.setConteudo(loadTextFile(arquivoIn));
					db.incluirQeArquivoInDTO(arquivoInDTO);
				}
				return db.selectQeArquivoInDTOPeloHash(md5);
			}
		}
		return null;
	}
	
	private QeResumoDTO localizarResumoDTO(QeArquivoInDTO arquivoInDTO) throws Exception{
		MoleculaDTO moleculaDTO = db.selectMoleculaDTO( arquivoInDTO.getMoleculaCodigo() );
		String nome = arquivoInDTO.getNome().substring(0, arquivoInDTO.getNome().lastIndexOf("."))+".out";		
		QeResumoDTO resumoDTO = db.selectQeResumoDTOPeloNome(arquivoInDTO.getCodigo(), nome);
		
		String filename = maquinaDTO.getRootPath()+File.separator+
				moleculaDTO.getNome()+File.separator+
				InitLocal.PATH_MONITORAMENTO+nome;
		
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
				//atualizar hashoutput , processar , tamanhokb, executando
				//
				
			}
		}
		return resumoDTO;
	}
	
	public boolean analisarTodosOutputs() throws Exception {
		//analisar os em andamento: processar TRUE
		
		//ATUALIZAR OS CONCLUIDOS
		
		return false;
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
