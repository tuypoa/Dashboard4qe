package lapfarsc.qe.dashboard.business;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import lapfarsc.qe.dashboard.dto.ComandoDTO;
import lapfarsc.qe.dashboard.dto.JarLeituraDTO;
import lapfarsc.qe.dashboard.dto.MaquinaDTO;
import lapfarsc.qe.dashboard.dto.MoleculaDTO;
import lapfarsc.qe.dashboard.dto.PsauxDTO;
import lapfarsc.qe.dashboard.dto.QeArquivoInDTO;
import lapfarsc.qe.dashboard.dto.QeResumoDTO;

public class DatabaseBusiness {
	
	private Connection conn = null;
	
	public DatabaseBusiness(Connection conn){
		this.conn = conn;
	}
	
	/*
	 * TABELA jarleitura
	 */	
	public void incluirJarLeituraDTO(JarLeituraDTO dto) throws Exception {		
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

	/*
	 * TABELA comando
	 */	
	public ComandoDTO selectComandoDTO(Integer id) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			ps = conn.prepareStatement("SELECT codigo,cmdtemplate,cmdprefixo FROM comando WHERE codigo = ? ");
			ps.setInt(1, id);
			rs = ps.executeQuery();			
			if(rs.next()){
				ComandoDTO dto = new ComandoDTO();
				dto.setCodigo(rs.getInt("codigo"));
				dto.setTemplate(rs.getString("cmdtemplate"));
				dto.setPrefixo(rs.getString("cmdprefixo"));
				return dto;
			}			
		}finally{
			if(rs!=null) rs.close();
			if(ps!=null) ps.close();
		}
		return null;
	}

	public List<ComandoDTO> selectListComandoDTO() throws Exception {
		//listagem 		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			ps = conn.prepareStatement("SELECT codigo,cmdtemplate,cmdprefixo FROM comando ORDER BY codigo");
			rs = ps.executeQuery();			
			List<ComandoDTO> listComandoDTO = new ArrayList<ComandoDTO>();	
			while(rs.next()){
				ComandoDTO dto = new ComandoDTO();
				dto.setCodigo(rs.getInt("codigo"));
				dto.setTemplate(rs.getString("cmdtemplate"));
				dto.setPrefixo(rs.getString("cmdprefixo"));
				listComandoDTO.add(dto);
			}	
			return listComandoDTO;
		}finally{
			if(rs!=null) rs.close();
			if(ps!=null) ps.close();
		}
	}
	

	/*
	 * TABELA molecula
	 */
	public List<MoleculaDTO> selectListMoleculaDTO() throws Exception {
		//listagem 		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			ps = conn.prepareStatement("SELECT codigo,nome FROM molecula ORDER BY codigo");
			rs = ps.executeQuery();			
			List<MoleculaDTO> listDTO = new ArrayList<MoleculaDTO>();	
			while(rs.next()){
				MoleculaDTO dto = new MoleculaDTO();
				dto.setCodigo(rs.getInt("codigo"));
				dto.setNome(rs.getString("nome"));
				listDTO.add(dto);
			}	
			return listDTO;
		}finally{
			if(rs!=null) rs.close();
			if(ps!=null) ps.close();
		}
	}
	public MoleculaDTO selectMoleculaDTO(Integer id) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			ps = conn.prepareStatement("SELECT codigo,nome FROM molecula WHERE codigo = ? ");
			ps.setInt(1, id);
			rs = ps.executeQuery();			
			if(rs.next()){
				MoleculaDTO dto = new MoleculaDTO();
				dto.setCodigo(rs.getInt("codigo"));
				dto.setNome(rs.getString("nome"));
				return dto;
			}			
		}finally{
			if(rs!=null) rs.close();
			if(ps!=null) ps.close();
		}
		return null;
	}
	
	/*
	 * TABELA qearquivoin
	 */	
	public QeArquivoInDTO selectQeArquivoInDTOPeloHash(String hash) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			ps = conn.prepareStatement("SELECT codigo,hash,nome,conteudo,molecula_codigo FROM qearquivoin WHERE hash LIKE ? ");
			ps.setString(1, hash);
			rs = ps.executeQuery();			
			if(rs.next()){
				QeArquivoInDTO dto = new QeArquivoInDTO();
				dto.setCodigo(rs.getInt("codigo"));
				dto.setHash(rs.getString("hash"));
				dto.setNome(rs.getString("nome"));
				dto.setConteudo(rs.getString("conteudo"));
				dto.setMoleculaCodigo(rs.getInt("molecula_codigo"));
				return dto;
			}			
		}finally{
			if(rs!=null) rs.close();
			if(ps!=null) ps.close();
		}
		return null;
	}
	
	public void incluirQeArquivoInDTO(QeArquivoInDTO dto) throws Exception {		
		PreparedStatement ps = null;
		try{
			ps = conn.prepareStatement("INSERT INTO qearquivoin(hash,nome,descricao,conteudo,molecula_codigo) values (?,?,?,?,?)");
			int p = 1;
			ps.setString(p++, dto.getHash());
			ps.setString(p++, dto.getNome());
			ps.setString(p++, dto.getDescricao());
			ps.setString(p++, dto.getConteudo());
			ps.setInt(p++, dto.getMoleculaCodigo());
			ps.executeUpdate();
		}finally{
			if(ps!=null) ps.close();
		}
	}	
	
	/*
	 * TABELA qeresumo
	 */	
	public QeResumoDTO selectQeResumoDTOPeloNome(Integer arquivoInCodigo, String nome) throws Exception {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			ps = conn.prepareStatement("SELECT codigo,qearquivoin_codigo,hashoutput,processar,nome,tamanhokb,qtdecpu," +					
					"   TO_CHAR(ultimalida,'dd/mm/yyyy HH:mm:ss') AS ultimalida," +
					"   concluido,executando FROM qeresumo " +
					" WHERE qearquivoin_codigo = ? AND nome LIKE ? ");
			ps.setInt(1, arquivoInCodigo);
			ps.setString(2, nome);
			rs = ps.executeQuery();			
			if(rs.next()){
				QeResumoDTO dto = new QeResumoDTO();
				dto.setCodigo(rs.getInt("codigo"));
				dto.setQeArquivoInCodigo(rs.getInt("qearquivoin_codigo"));
				dto.setHashOutput(rs.getString("hashoutput"));
				dto.setProcessar(rs.getBoolean("processar"));
				dto.setNome(rs.getString("nome"));
				dto.setTamanhoKb(rs.getDouble("tamanhokb"));
				dto.setQtdeCpu(rs.getInt("qtdecpu"));
				dto.setUltimaLida(rs.getString("ultimalida"));
				dto.setConcluido(rs.getBoolean("concluido"));
				dto.setExecutando(rs.getBoolean("executando"));
				return dto;
			}			
		}finally{
			if(rs!=null) rs.close();
			if(ps!=null) ps.close();
		}
		return null;
	}
	
	public void incluirQeResumoDTO(QeResumoDTO dto) throws Exception {		
		PreparedStatement ps = null;
		try{
			ps = conn.prepareStatement("INSERT INTO qeresumo(" +
					"	qearquivoin_codigo,hashoutput,nome,tamanhokb,qtdecpu,ultimalida" +
					" ) values (?,?,?,?,0,NOW())");
			int p = 1;
			ps.setInt(p++, dto.getQeArquivoInCodigo());
			ps.setString(p++, dto.getHashOutput());
			ps.setString(p++, dto.getNome());
			ps.setDouble(p++, dto.getTamanhoKb());
			ps.executeUpdate();
		}finally{
			if(ps!=null) ps.close();
		}
	}	
	
	public void updateNaoExecutandoTodosQeResumoDTO(Integer maquinaCodigo) throws Exception {
		PreparedStatement ps = null;
		try{
			ps = conn.prepareStatement("UPDATE qeresumo SET executando=FALSE WHERE qearquivoin_codigo = ? ");
			ps.setInt(1, maquinaCodigo);
			ps.executeUpdate();
		}finally{
			if(ps!=null) ps.close();
		}
	}
		
	
	
	/*
	 * TABELA psaux
	 */	
	public void incluirListPsauxDTO(List<PsauxDTO> listDTO) throws Exception {		
		PreparedStatement ps = null;
		PreparedStatement psDel = null;
		try{
			psDel = conn.prepareStatement("TRUNCATE psaux");
			psDel.executeUpdate();
			
			ps = conn.prepareStatement("INSERT INTO psaux(maquina_codigo,comando_codigo,pid,uid, " +
					" qearquivoin_codigo,qeresumo_codigo,cpu,mem,conteudo) values (?,?,?,?,?,?,?,?,?)");
			for (PsauxDTO dto : listDTO) {
				int p = 1;
				ps.setInt(p++, dto.getMaquinaCodigo());
				ps.setInt(p++, dto.getComandoCodigo());
				ps.setInt(p++, dto.getPid());
				ps.setString(p++, dto.getUid());
				if(dto.getQeArquivoInCodigo()==null){
					ps.setNull(p++, Types.NULL);
				}else{
					ps.setInt(p++, dto.getQeArquivoInCodigo());
				}
				if(dto.getQeResumoCodigo()==null){
					ps.setNull(p++, Types.NULL);
				}else{
					ps.setInt(p++, dto.getQeResumoCodigo());
				}
				ps.setBigDecimal(p++, dto.getCpu());
				ps.setBigDecimal(p++, dto.getMem());
				ps.setString(p++, dto.getConteudo());
				ps.executeUpdate();	
			}
		}finally{
			if(ps!=null) ps.close();
			if(psDel!=null) psDel.close();
		}
	}
	
	/*
	 * TABELA maquina
	 */	
	public MaquinaDTO selectMaquinaDTO(Integer id) throws Exception {
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
		return null;
	}	

	public List<MaquinaDTO> selectListMaquinaDTO() throws Exception {
		//listagem de maquinas		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			ps = conn.prepareStatement("SELECT " +
					" codigo,nome,ssh,senha,rootpath,jarpath,mincpu,maxcpu," +
					"	COALESCE(cpuused,0) AS cpuused, COALESCE(memused,0) AS memused," +
					"   TO_CHAR(ultimoacesso,'dd/mm/yyyy HH:mm:ss') AS ultimoacesso," +
					" iniciarjob,online,ignorar " +
					" FROM maquina ORDER BY ultimoacesso DESC");
			rs = ps.executeQuery();
			
			List<MaquinaDTO> listMaquinaDTO = new ArrayList<MaquinaDTO>();	
			while(rs.next()){
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
				maq.setIgnorar(rs.getBoolean("ignorar"));
				listMaquinaDTO.add(maq);
			}	
			return listMaquinaDTO;
		}finally{
			if(rs!=null) rs.close();
			if(ps!=null) ps.close();
		}
	}
	
	public void updateOfflineTodasMaquinasDTO() throws Exception {
		PreparedStatement ps = null;
		try{
			ps = conn.prepareStatement("UPDATE maquina SET online=FALSE ");
			ps.executeUpdate();
		}finally{
			if(ps!=null) ps.close();
		}
	}
		
	public void updateOnlineMaquinasDTO(List<MaquinaDTO> listMaquinaDTO) throws Exception {		
		PreparedStatement ps = null;
		try{
			ps = conn.prepareStatement("UPDATE maquina SET online=?, cpuused=?, memused=?, ultimoacesso=NOW() WHERE codigo = ? ");
			for (MaquinaDTO m : listMaquinaDTO) {
				if(m.getOnline()){
					int p = 1;
					ps.setBoolean(p++, m.getOnline()); 
					ps.setBigDecimal(p++, m.getCpuUsed());
					ps.setBigDecimal(p++, m.getMemUsed());
					ps.setInt(p++, m.getCodigo());
					ps.executeUpdate();
				}
			}
		}finally{
			if(ps!=null) ps.close();
		}
	}
	
}
