package br.com.mb.stockmanagerbrokerage;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class LogMessage {
	private Integer httpStatus;
	private String httpMethod;
	private String path;
	private String clientIp;
	private String javaMethod;
	private String response;
}
