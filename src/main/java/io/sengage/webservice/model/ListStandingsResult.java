package io.sengage.webservice.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ListStandingsResult {

	private List<Standing> entries;
	private int week;
	private boolean solos;
	Region region;
}
