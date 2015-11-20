package zamtrax.lights;

public class Attenuation {

	private float constant;
	private float linear;
	private float exponential;

	public Attenuation(float constant, float linear, float exponential) {
		this.constant = constant;
		this.linear = linear;
		this.exponential = exponential;
	}

	public float getConstant() {
		return constant;
	}

	public void setConstant(float constant) {
		this.constant = constant;
	}

	public float getLinear() {
		return linear;
	}

	public void setLinear(float linear) {
		this.linear = linear;
	}

	public float getExponential() {
		return exponential;
	}

	public void setExponential(float exponential) {
		this.exponential = exponential;
	}

}
