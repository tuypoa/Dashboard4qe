package lapfarsc.qe.dashboard.dto;

public class MaqArqHashDTO {

	private Integer maquinaCodigo;
	private Integer arquivoInCodigo;
	private String hashArquivoIn;
	private String hashMaquinaArquivoIn;
	private Boolean ignorar;
	private String rootPath;
	private String nomeArquivo;
	
	public Integer getMaquinaCodigo() {
		return maquinaCodigo;
	}
	public void setMaquinaCodigo(Integer maquinaCodigo) {
		this.maquinaCodigo = maquinaCodigo;
	}
	public Integer getArquivoInCodigo() {
		return arquivoInCodigo;
	}
	public void setArquivoInCodigo(Integer arquivoInCodigo) {
		this.arquivoInCodigo = arquivoInCodigo;
	}
	public String getHashArquivoIn() {
		return hashArquivoIn;
	}
	public void setHashArquivoIn(String hashArquivoIn) {
		this.hashArquivoIn = hashArquivoIn;
	}
	public String getHashMaquinaArquivoIn() {
		return hashMaquinaArquivoIn;
	}
	public void setHashMaquinaArquivoIn(String hashMaquinaArquivoIn) {
		this.hashMaquinaArquivoIn = hashMaquinaArquivoIn;
	}
	public Boolean getIgnorar() {
		return ignorar;
	}
	public void setIgnorar(Boolean ignorar) {
		this.ignorar = ignorar;
	}
	public String getRootPath() {
		return rootPath;
	}
	public void setRootPath(String rootPath) {
		this.rootPath = rootPath;
	}
	public String getNomeArquivo() {
		return nomeArquivo;
	}
	public void setNomeArquivo(String nomeArquivo) {
		this.nomeArquivo = nomeArquivo;
	}
	
	
}
