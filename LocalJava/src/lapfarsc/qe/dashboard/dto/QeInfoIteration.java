package lapfarsc.qe.dashboard.dto;

public class QeInfoIteration {

	private Integer qeResumoCodigo;
	private Integer scfCycles;
	private Integer iteration;
	private Double cputime;
	
	public Integer getQeResumoCodigo() {
		return qeResumoCodigo;
	}
	public void setQeResumoCodigo(Integer qeResumoCodigo) {
		this.qeResumoCodigo = qeResumoCodigo;
	}
	public Integer getScfCycles() {
		return scfCycles;
	}
	public void setScfCycles(Integer scfCycles) {
		this.scfCycles = scfCycles;
	}
	public Integer getIteration() {
		return iteration;
	}
	public void setIteration(Integer iteration) {
		this.iteration = iteration;
	}
	public Double getCputime() {
		return cputime;
	}
	public void setCputime(Double cputime) {
		this.cputime = cputime;
	}
	
	
}
