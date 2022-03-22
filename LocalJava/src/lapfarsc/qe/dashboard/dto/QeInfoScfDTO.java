package lapfarsc.qe.dashboard.dto;

public class QeInfoScfDTO {

	private Integer qeResumoCodigo;
	private Integer scfCycles;
	private Integer BfgsSteps;
	private Boolean converged;
	private Double enthalpy;
	private Double volume;
	private Double density;
	private Integer iterations;
	private Double cputime;
	private String cellparams;
	private String atomicpositions;
	
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
	public Integer getBfgsSteps() {
		return BfgsSteps;
	}
	public void setBfgsSteps(Integer bfgsSteps) {
		BfgsSteps = bfgsSteps;
	}
	public Boolean getConverged() {
		return converged;
	}
	public void setConverged(Boolean converged) {
		this.converged = converged;
	}
	public Double getEnthalpy() {
		return enthalpy;
	}
	public void setEnthalpy(Double enthalpy) {
		this.enthalpy = enthalpy;
	}
	public Double getVolume() {
		return volume;
	}
	public void setVolume(Double volume) {
		this.volume = volume;
	}
	public Double getDensity() {
		return density;
	}
	public void setDensity(Double density) {
		this.density = density;
	}
	public Integer getIterations() {
		return iterations;
	}
	public void setIterations(Integer iterations) {
		this.iterations = iterations;
	}
	public Double getCputime() {
		return cputime;
	}
	public void setCputime(Double cputime) {
		this.cputime = cputime;
	}
	public String getCellparams() {
		return cellparams;
	}
	public void setCellparams(String cellparams) {
		this.cellparams = cellparams;
	}
	public String getAtomicpositions() {
		return atomicpositions;
	}
	public void setAtomicpositions(String atomicpositions) {
		this.atomicpositions = atomicpositions;
	}
	
}
