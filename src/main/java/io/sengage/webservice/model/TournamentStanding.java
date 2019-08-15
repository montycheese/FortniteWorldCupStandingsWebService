package io.sengage.webservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBIndexRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConvertedEnum;

@Builder(toBuilder=true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = TournamentStanding.TABLE_NAME)
public class TournamentStanding {
	public static final String TABLE_NAME = "TournamentStandings";
	public static final String TOURNAMENT_WEEK_REGION_RANK_INDEX = "TournamentWeekRegion-StandingRank-Index";
	public static final String TOURNAMENT_WEEK_REGION_ATTR_NAME = "TournamentWeekRegion";
	public static final String RANK_ATTR_NAME = "StandingRank";
	public static final String TOURNAMENT_WEEK_REGION_NAME_ATTR_NAME = "TournamentWeekRegionName";
	
	@DynamoDBHashKey(attributeName = TOURNAMENT_WEEK_REGION_NAME_ATTR_NAME)
	private String tournamentWeekRegionName;
	@DynamoDBIndexRangeKey(globalSecondaryIndexName = TOURNAMENT_WEEK_REGION_RANK_INDEX, attributeName = RANK_ATTR_NAME)
	private int rank;
	@DynamoDBTypeConvertedEnum
	private Tournament tournament;
	private int points;
	private int prize;
	private int week;
	private String currencySymbol;
	@DynamoDBTypeConvertedEnum
	private Region region;
	@DynamoDBTypeConvertedEnum
	private SquadType squadType;
	private String name1;
	private String name2;
	private String name3;
	private String name4;
	@DynamoDBIndexHashKey(globalSecondaryIndexName = TOURNAMENT_WEEK_REGION_RANK_INDEX, attributeName = TOURNAMENT_WEEK_REGION_ATTR_NAME)
	private String tournamentWeekRegion;
	
	private int delta;
	
}
