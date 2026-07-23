package nano.server.dtos;

public class EnergyDto {
	private String id;
	private float volts;
	private float amps;
	private float freq;
	private int diff;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public float getVolts() {
		return volts;
	}

	public void setVolts(float volts) {
		this.volts = volts;
	}

	public float getAmps() {
		return amps;
	}

	public void setAmps(float amps) {
		this.amps = amps;
	}

	public float getFreq() {
		return freq;
	}

	public void setFreq(float freq) {
		this.freq = freq;
	}

	public int getDiff() {
		return diff;
	}

	public void setDiff(int diff) {
		this.diff = diff;
	}
}
