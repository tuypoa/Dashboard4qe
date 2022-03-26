package lapfarsc.qe.dashboard.dto;

public class QeInfoScfDTO {

	private Integer qeResumoCodigo;
	private Integer scfCycles;
	private Integer bfgsSteps;
	private Double enthalpy;
	private Double volume;
	private Double density;
	private Integer iterations;
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
		return bfgsSteps;
	}
	public void setBfgsSteps(Integer bfgsSteps) {
		this.bfgsSteps = bfgsSteps;
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
