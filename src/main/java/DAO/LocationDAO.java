package DAO;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.replication.ReplicationResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class LocationDAO implements LocationInterface{
	
	public Location getOneLocation(RestHighLevelClient client, String id_location) throws IOException {
		GetRequest getRequest = new GetRequest(
		        "locations", 
		        "doc",  
		        id_location); 
		String sourceAsString = "";
		try {
			
			GetResponse getResponse = client.get(getRequest);

			if (getResponse.isExists()) {
			    sourceAsString = getResponse.getSourceAsString();        
			    //System.out.println(sourceAsString+"\n");
			} else {
			    System.out.println("Impossible de trouver la location "+id_location);
			    return null;
			}
		} catch (ElasticsearchException e) {
		    if (e.status() == RestStatus.NOT_FOUND) {
		        
		    }
		}

		return new ObjectMapper().readValue(sourceAsString, Location.class);
	}
	
	public ArrayList<Location> getLocations(RestHighLevelClient client,  String map_id) throws IOException {
		ArrayList<Location> locations = new ArrayList<Location>();
		
		SearchRequest searchRequest = new SearchRequest("locations"); 
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder(); 
		searchSourceBuilder.query(QueryBuilders.termQuery("mapName", map_id)); 
		searchRequest.source(searchSourceBuilder);
		
		SearchResponse searchResponse = client.search(searchRequest);
		SearchHits hits = searchResponse.getHits();
		long totalHits = hits.getTotalHits();
		float maxScore = hits.getMaxScore();
		System.out.println("total : "+totalHits+", maxScore : "+maxScore);
		
		SearchHit[] searchHits = hits.getHits();
		for (SearchHit hit : searchHits) {
			String sourceAsString = hit.getSourceAsString();
			Location location= new ObjectMapper().readValue(sourceAsString, Location.class);
			
			locations.add(location);
		}
		
		return locations;

	}


	public boolean insertLocation(RestHighLevelClient client, Location location,Map map) throws IOException
	{	
		java.util.Map<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("id", location.id);
		jsonMap.put("nameplace", location.nameplace);
		jsonMap.put("mapName", location.mapName);
		jsonMap.put("urlImg", location.urlImg);
		jsonMap.put("content", location.content);
		jsonMap.put("longitude", location.longitude);
		jsonMap.put("latitude", location.latitude);
		jsonMap.put("isFavorite", location.isFavorite);
		
		IndexRequest indexRequest = new IndexRequest("locations", "doc",map.name)
		        .source(jsonMap)
		        .opType(DocWriteRequest.OpType.CREATE);
		
		try {
			IndexResponse indexResponse = client.index(indexRequest);
		} catch(ElasticsearchException e) {
		    if (e.status() == RestStatus.CONFLICT) {
		        System.out.println("insert ne fonctionne pas (la location existe déjà ?)");
		        return false;
		    }
		}
		return true;
	}
	
	public boolean insertLocationAndUpdateMap(RestHighLevelClient client, Location location,String map_name) throws IOException
	{	
		java.util.Map<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("id", location.id);
		jsonMap.put("nameplace", location.nameplace);
		jsonMap.put("mapName", location.mapName);
		jsonMap.put("urlImg", location.urlImg);
		jsonMap.put("content", location.content);
		jsonMap.put("longitude", location.longitude);
		jsonMap.put("latitude", location.latitude);
		jsonMap.put("isFavorite", location.isFavorite);
		Map map = DAO.getActionMap().getOneMap(client, map_name);
		map.addLocation(location.nameplace);
		
		IndexRequest indexRequest = new IndexRequest("locations", "doc",location.nameplace)
		        .source(jsonMap)
		        .opType(DocWriteRequest.OpType.CREATE);
		
		try {
			IndexResponse indexResponse = client.index(indexRequest);
			DAO.getActionMap().updateMap(client, map);
		}catch(ElasticsearchException e) {
		    if (e.status() == RestStatus.CONFLICT) {
		        System.out.println("insert ne fonctionne pas (la location existe déjà ?)");
		        return false;
		    }
		}
		return true;
	}
	
	public boolean updateLocation(RestHighLevelClient client, Location location) throws IOException{
		java.util.Map<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("urlImg", location.urlImg);
		jsonMap.put("content", location.content);
		jsonMap.put("isFavorite", location.isFavorite);
		UpdateRequest request = new UpdateRequest("locations", 
		        "doc",  
		        location.id)
		        .doc(jsonMap);
		UpdateResponse updateResponse = client.update(request);
		request.docAsUpsert(false);
		
		if (updateResponse.getResult() == DocWriteResponse.Result.UPDATED) {
			System.out.println("La map "+location.nameplace+ " a été mis à jour");
		} else if (updateResponse.getResult() == DocWriteResponse.Result.DELETED) {
			System.out.println("La map "+location.nameplace+ " a été supprimé");
			return false;
		} else if (updateResponse.getResult() == DocWriteResponse.Result.NOOP) {
			System.out.println("La map "+location.nameplace+ " n'a pas pu être mis à jour");
			return false;
		}
		return true;
	}
	
	public boolean deleteLocation(RestHighLevelClient client, String id_location) throws IOException {
		Location location = this.getOneLocation(client, id_location);
		DeleteRequest request = new DeleteRequest(
		        "locations",    
		        "doc",     
		        id_location);
		DeleteResponse deleteResponse = client.delete(request);
		Map map = DAO.getActionMap().getOneMap(client, location.mapName);
		map.removeLocation(id_location);
		DAO.getActionMap().updateMap(client, map);
		
		ReplicationResponse.ShardInfo shardInfo = deleteResponse.getShardInfo();
		if (shardInfo.getFailed() > 0) {
		    for (ReplicationResponse.ShardInfo.Failure failure : shardInfo.getFailures()) {
		        String reason = failure.reason(); 
		        System.out.println(reason);
		        return false;
		    }
		}
		return true;		
	}
	
	public void createIndexLocation(RestHighLevelClient client) throws IOException {
		try
		{
			CreateIndexRequest request = new CreateIndexRequest("locations");
			CreateIndexResponse createIndexResponse = client.indices().create(request);
		}
		catch(Exception e)
		{
			System.out.println("l'index users existe déjà");
		}
	}


}
