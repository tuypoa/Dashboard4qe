package lapfarsc.qe.dashboard.dto;

public class QeResumoDTO {

	private Integer codigo;
	private Integer qeArquivoInCodigo;
	private String hashOutput;
	private Boolean processar;
	private String nome;
	private Double tamanhoKb;
	private Integer qtdeCpu;
	private String ultimaLida;
	private Boolean concluido;
	private Boolean executando;
	private String erro;
	
	public Integer getCodigo() {
		return codigo;
	}
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	public Integer getQeArquivoInCodigo() {
		return qeArquivoInCodigo;
	}
	public void setQeArquivoInCodigo(Integer qeArquivoInCodigo) {
		this.qeArquivoInCodigo = qeArquivoInCodigo;
	}
	public String getHashOutput() {
		return hashOutput;
	}
	public void setHashOutput(String hashOutput) {
		this.hashOutput = hashOutput;
	}
	public Boolean getProcessar() {
		return processar;
	}
	public void setProcessar(Boolean processar) {
		this.processar = processar;
	}	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public Double getTamanhoKb() {
		return tamanhoKb;
	}
	public void setTamanhoKb(Double tamanhoKb) {
		this.tamanhoKb = tamanhoKb;
	}
	public Integer getQtdeCpu() {
		return qtdeCpu;
	}
	public void setQtdeCpu(Integer qtdeCpu) {
		this.qtdeCpu = qtdeCpu;
	}
	public String getUltimaLida() {
		return ultimaLida;
	}
	public void setUltimaLida(String ultimaLida) {
		this.ultimaLida = ultimaLida;
	}
	public Boolean getConcluido() {
		return concluido;
	}
	public void setConcluido(Boolean concluido) {
		this.concluido = concluido;
	}
	public Boolean getExecutando() {
		return executando;
	}
	public void setExecutando(Boolean executando) {
		this.executando = executando;
	}
	public String getErro() {
		return erro;
	}
	public void setErro(String erro) {
		this.erro = erro;
	}
	
	
}
