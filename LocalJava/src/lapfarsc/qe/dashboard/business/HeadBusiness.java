/**
 * @author tuypoa
 *
 *
 */
package lapfarsc.qe.dashboard.business;

import java.io.InputStream;
import java.math.BigDecimal;
import java.net.NoRouteToHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import lapfarsc.qe.dashboard.dto.MaquinaDTO;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;


public class HeadBusiness {
	
	private Connection conn = null;
	private List<MaquinaDTO> listMaquinaDTO = null;
	
	public HeadBusiness(Connection conn){
		this.conn = conn;
	}
	
	public int accessAll() throws Exception {
		this.selectListMaquinasDTO();	
		for (MaquinaDTO maqDTO : listMaquinaDTO) {			
			sshInicialMaquinaDTO(maqDTO);			
			//break;			
		}		
		updateOfflineTodasMaquinasDTO();		
		updateOnlineMaquinasDTO();
		
		int count = 0;
		//acionamento do SLAVE1 para as maquinas ON
		this.selectListMaquinasDTO();	
		for (MaquinaDTO maqDTO : listMaquinaDTO) {
			if(maqDTO.getOnline() && !maqDTO.getIgnorar()){
				
				count++;
			}
		}
		return count;
	}
	
	private void sshInicialMaquinaDTO(MaquinaDTO maqDTO) throws Exception{		
			
		Session session = null;
		Channel channel = null;
		try{				
			session = getSessionSSH(maqDTO.getSsh(), maqDTO.getSenha());
			session.connect();
			
			System.out.println(maqDTO.getSsh()+"> SSH OK");
			maqDTO.setOnline( Boolean.TRUE );
			
			String[] commands = new String[]{
					"top -bn1 |grep Cpu",
					"top -bn1 |grep Mem"
			};
			StringBuilder sb = new StringBuilder();
			
			for (String cmd : commands) {
				channel = session.openChannel("exec");
				((ChannelExec) channel).setCommand(cmd);
				channel.setInputStream(null);
				((ChannelExec) channel).setErrStream(System.err);
				InputStream in = channel.getInputStream();
				channel.connect();		
				byte[] tmp=new byte[1024];
				while(true){
					while(in.available()>0){
						int i=in.read(tmp, 0, 1024);
						if(i<0)break;
						sb.append(new String(tmp, 0, i));
					}
					if(channel.isClosed()){
						if(in.available()>0) continue;						
						break;
					}
					try{Thread.sleep(1000);}catch(Exception ee){}
				}
				in.close();
			}
			//TODO COLOCAR INFOS NO DTO
			String kw = "%Cpu(s):";
			int kwid = sb.indexOf(kw);
			if(kwid!=-1){
				String cpuused = sb.substring(kwid+kw.length(), sb.indexOf("us",kwid+kw.length()) );
				maqDTO.setCpuUsed(BigDecimal.valueOf( Double.parseDouble( cpuused.replace(",", ".") ) ) );
			}
			kw = "Mem";
			kwid = sb.indexOf(kw);
			if(kwid!=-1){
				String info = sb.substring(kwid+kw.length(), sb.indexOf("used",kwid+kw.length()) ).trim();
				String memtot = info.substring(info.indexOf(":")+1, info.indexOf("total") );
				String memused = info.substring( info.lastIndexOf(",")+4>info.length()? info.substring(0,info.length()-4).lastIndexOf(",")+1 : info.lastIndexOf(",")+1, info.length() );
				maqDTO.setMemUsed(BigDecimal.valueOf( 100*(Double.parseDouble( memused.replace(",", ".") ) / Double.parseDouble( memtot.replace(",", ".")) ) ) );
			}
			//System.out.println(sb.toString());
		
		} catch (Throwable e) {
			if(e.getCause() instanceof NoRouteToHostException){
				maqDTO.setOnline( Boolean.FALSE );
				System.out.println(maqDTO.getSsh()+"> OFFLINE: "+e.getCause());
				//e.printStackTrace();
			}else{
				System.out.println(maqDTO.getSsh()+"> ERRO: "+e.getCause());
				//e.printStackTrace();
			}
		}finally{
			if(channel!=null && !channel.isClosed()) channel.disconnect();
			if(session!=null) session.disconnect();
		}
		//System.out.println(maqDTO.getSsh()+"> SSH DISCONNECTED: "+channel.getExitStatus());
	}
	

	private Session getSessionSSH(final String ssh, final String senha) throws Throwable {
		JSch jsch = new JSch();
		String sshsp[] = ssh.split("@");
		Session session = jsch.getSession(sshsp[0], sshsp[1], 22);
		UserInfo ui = new UserInfo() {
			@Override
			public void showMessage(String m) {
				System.out.println(ssh+"> SSH CONNECTION MSG: "+ m);
			}			
			@Override
			public boolean promptYesNo(String arg0) { return true; }
			@Override
			public boolean promptPassword(String arg0) { return true; }
			@Override
			public boolean promptPassphrase(String arg0) { return true; }
			@Override
			public String getPassword() {	
				return senha;
			}
			@Override
			public String getPassphrase() { return null; }
		};
		session.setUserInfo(ui);
		
		java.util.Properties config = new java.util.Properties(); 
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
		
		return session;
	}
	
	
	private void updateOfflineTodasMaquinasDTO() throws Exception {
		PreparedStatement ps = null;
		try{
			ps = conn.prepareStatement("UPDATE maquina SET online=FALSE ");
			ps.executeUpdate();
		}finally{
			if(ps!=null) ps.close();
		}
	}
	
	private void updateOnlineMaquinasDTO() throws Exception {		
		PreparedStatement ps = null;
		try{
			ps = conn.prepareStatement("UPDATE maquina SET " +
					"	online=?, cpuused=?, memused=?, ultimoacesso=NOW() " +
					" WHERE codigo = ? ");
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
	
	private void selectListMaquinasDTO() throws Exception {
		//listagem de maquinas		
		PreparedStatement ps = null;
		ResultSet rs = null;
		try{
			ps = conn.prepareStatement("SELECT " +
					" codigo,nome,ssh,senha,rootpath,jarpath,mincpu,maxcpu," +
					"	COALESCE(cpuused,0) AS cpuused, COALESCE(memused,0) AS memused," +
					"   TO_CHAR(ultimoacesso,'dd/mm/yyyy HH:mm:ss') AS ultimoacesso," +
					" iniciarjob,online,ignorar " +
					" FROM maquina ORDER BY ultimoacesso");
			rs = ps.executeQuery();
			
			this.listMaquinaDTO = new ArrayList<MaquinaDTO>();	
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
				maq.setOnline(rs.getBoolean("ignorar"));
				this.listMaquinaDTO.add(maq);
			}			
		}finally{
			if(rs!=null) rs.close();
			if(ps!=null) ps.close();
		}
	}
	
	
	
}
