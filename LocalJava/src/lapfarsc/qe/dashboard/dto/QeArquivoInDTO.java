package lapfarsc.qe.dashboard.dto;

public class QeArquivoInDTO {

	private Integer codigo;
	private String hashMaqArq;
	private String nome;
	private String descricao;
	private String conteudo;
	private Integer moleculaCodigo;
	
	public Integer getCodigo() {
		return codigo;
	}
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	public String getHashMaqArq() {
		return hashMaqArq;
	}
	public void setHashMaqArq(String hashMaqArq) {
		this.hashMaqArq = hashMaqArq;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getConteudo() {
		return conteudo;
	}
	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}
	public Integer getMoleculaCodigo() {
		return moleculaCodigo;
	}
	public void setMoleculaCodigo(Integer moleculaCodigo) {
		this.moleculaCodigo = moleculaCodigo;
	}
}
