package io.sengage.webservice.model;

import java.util.List;

import lombok.Data;

@Data
public class PutStandingsRequest {
	private List<Standing> standings;
}
