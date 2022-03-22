package lapfarsc.qe.dashboard.dto;

public class MaquinaQeArquivoInDTO {
	private Integer maquinaCodigo;
	private Integer qeArquivoInCodigo;
	private String hashArqIn;
	private String rootPath;  
	private Integer ordem; 
	private Boolean ignorar;
	
	public Integer getMaquinaCodigo() {
		return maquinaCodigo;
	}
	public void setMaquinaCodigo(Integer maquinaCodigo) {
		this.maquinaCodigo = maquinaCodigo;
	}
	public Integer getQeArquivoInCodigo() {
		return qeArquivoInCodigo;
	}
	public void setQeArquivoInCodigo(Integer qeArquivoInCodigo) {
		this.qeArquivoInCodigo = qeArquivoInCodigo;
	}
	public String getHashArqIn() {
		return hashArqIn;
	}
	public void setHashArqIn(String hashArqIn) {
		this.hashArqIn = hashArqIn;
	}
	public String getRootPath() {
		return rootPath;
	}
	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}
	public Integer getOrdem() {
		return ordem;
	}
	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}
	public Boolean getIgnorar() {
		return ignorar;
	}
	public void setIgnorar(Boolean ignorar) {
		this.ignorar = ignorar;
	}
	
	
}
