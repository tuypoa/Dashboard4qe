package lapfarsc.qe.dashboard.dto;

import java.math.BigDecimal;

public class PsauxDTO {

	private Integer maquinaCodigo;
	private Integer comandoCodigo;
	private Integer pid;
	private String uid;
	private Integer qeArquivoInCodigo;
	private Integer qeResumoCodigo;
	private BigDecimal cpu;
	private BigDecimal mem;
	private String conteudo;
	
	public Integer getMaquinaCodigo() {
		return maquinaCodigo;
	}
	public void setMaquinaCodigo(Integer maquinaCodigo) {
		this.maquinaCodigo = maquinaCodigo;
	}
	public Integer getComandoCodigo() {
		return comandoCodigo;
	}
	public void setComandoCodigo(Integer comandoCodigo) {
		this.comandoCodigo = comandoCodigo;
	}
	public Integer getPid() {
		return pid;
	}
	public void setPid(Integer pid) {
		this.pid = pid;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public Integer getQeArquivoInCodigo() {
		return qeArquivoInCodigo;
	}
	public void setQeArquivoInCodigo(Integer qeArquivoInCodigo) {
		this.qeArquivoInCodigo = qeArquivoInCodigo;
	}
	public Integer getQeResumoCodigo() {
		return qeResumoCodigo;
	}
	public void setQeResumoCodigo(Integer qeResumoCodigo) {
		this.qeResumoCodigo = qeResumoCodigo;
	}
	public BigDecimal getCpu() {
		return cpu;
	}
	public void setCpu(BigDecimal cpu) {
		this.cpu = cpu;
	}
	public BigDecimal getMem() {
		return mem;
	}
	public void setMem(BigDecimal mem) {
		this.mem = mem;
	}
	public String getConteudo() {
		return conteudo;
	}
	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}
	
	
}
