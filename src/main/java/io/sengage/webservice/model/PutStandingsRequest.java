package io.sengage.webservice.model;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PutStandingsRequest {
	private List<Standing> standings;
}
