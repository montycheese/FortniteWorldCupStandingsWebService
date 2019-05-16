package io.sengage.webservice.model;

import io.sengage.webservice.persistence.converters.InstantConverter;

import java.time.Instant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverted;

@Builder(toBuilder=true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@DynamoDBTable(tableName = Feedback.TABLE_NAME)
public class Feedback {
	public static final String TABLE_NAME = "Feedback";
	
	@DynamoDBHashKey
	private String id;
	private String body;
	@DynamoDBTypeConverted(converter = InstantConverter.class)
	private Instant createdAt;
	

}
