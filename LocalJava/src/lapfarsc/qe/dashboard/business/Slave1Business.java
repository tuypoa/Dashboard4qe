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
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import lapfarsc.qe.dashboard.dto.CmdTopDTO;
import lapfarsc.qe.dashboard.dto.JarLeituraDTO;
import lapfarsc.qe.dashboard.dto.MaquinaDTO;

public class Slave1Business {
	
	private Connection conn = null;
	private MaquinaDTO maquinaDTO = null;
	
	public Slave1Business(Connection conn, Integer maqId) throws Exception{
		this.conn = conn;
		this.maquinaDTO = selectMaquinaDTO(maqId);
	}

	public void gravarJarLeitura() throws Exception {
		Process process = Runtime.getRuntime().exec("top -bn1");
		int exitCode = process.waitFor();
		if (exitCode == 0) {
			BufferedReader bufferedReader = new BufferedReader(
					new InputStreamReader(process.getInputStream()));
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
			incluirJarLeituraDTO(jlDTO);
		}
	}
	
	private void incluirJarLeituraDTO(JarLeituraDTO dto) throws Exception {		
		PreparedStatement ps = null;
		try{
			ps = conn.prepareStatement("INSERT INTO jarleitura(maquina_codigo,cpuused,memused) values (?,?,?)");
			int p = 1;
			ps.setInt(p++, dto.getMaquinaCodigo()); 
			ps.setBigDecimal(p++, dto.getCpuUsed());
			ps.setBigDecimal(p++, dto.getMemUsed());
			ps.executeUpdate();
		}finally{
			if(ps!=null) ps.close();
		}
	}

	
	private MaquinaDTO selectMaquinaDTO(Integer id) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			ps = conn.prepareStatement("SELECT " +
					" codigo,nome,ssh,senha,rootpath,jarpath,mincpu,maxcpu," +
					"	COALESCE(cpuused,0) AS cpuused, COALESCE(memused,0) AS memused," +
					"   TO_CHAR(ultimoacesso,'dd/mm/yyyy HH:mm:ss') AS ultimoacesso," +
					" iniciarjob,online,ignorar " +
					" FROM maquina WHERE codigo = ? ");
			ps.setInt(1, id);
			rs = ps.executeQuery();
			
			if(rs.next()){
				MaquinaDTO maq = new MaquinaDTO();
				maq.setCodigo(rs.getInt("codigo"));
				maq.setNome(rs.getString("nome"));
				maq.setSsh(rs.getString("ssh"));
				maq.setSenha(rs.getString("senha"));
				maq.setRootPath(rs.getString("rootpath"));
				maq.setJarPath(rs.getString("jarpath"));
				maq.setMinCpu(rs.getInt("mincpu"));
				maq.setMaxCpu(rs.getInt("maxcpu"));
				
				maq.setCpuUsed(rs.getBigDecimal("cpuused"));
				maq.setMemUsed(rs.getBigDecimal("memused"));
				maq.setUltimoAcesso(rs.getString("ultimoacesso"));
				
				maq.setIniciarJob(rs.getBoolean("iniciarjob"));
				maq.setOnline(rs.getBoolean("online"));
				maq.setOnline(rs.getBoolean("ignorar"));
				return maq;
			}			
		}finally{
			if(rs!=null) rs.close();
			if(ps!=null) ps.close();
		}
		return maquinaDTO;
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
