package pt.up.fc.dcc.mooshak.shared.kora;

public class InfoMessage {
	private String type;
	private String[] args;
	public InfoMessage(String type, String...args) {
		this.type=type;
		this.args=args;
	}
	
	public String getTipo() {
		return type;
	}
	public void setTipo(String tipo) {
		this.type = tipo;
	}

	public String[] getArgs() {
		return args;
	}

	public void setArgs(String...args) {
		this.args = args;
	}
}

