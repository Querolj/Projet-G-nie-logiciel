package DAO;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
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
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

/*
 * Index name : "maps"
 * 
 */

public class MapDAO implements MapInterface{

	public ArrayList<Map> getMaps(RestHighLevelClient client) throws IOException {
		ArrayList<Map> maps = new ArrayList<Map>();
		
		SearchRequest searchRequest = new SearchRequest("maps"); 
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder(); 
		searchSourceBuilder.query(QueryBuilders.matchAllQuery()); 
		searchRequest.source(searchSourceBuilder);
		
		SearchResponse searchResponse = client.search(searchRequest);
		SearchHits hits = searchResponse.getHits();
		long totalHits = hits.getTotalHits();
		float maxScore = hits.getMaxScore();
		System.out.println("total : "+totalHits+", maxScore : "+maxScore);
		
		SearchHit[] searchHits = hits.getHits();
		for (SearchHit hit : searchHits) {
		    // do something with the SearchHit
			//String index = hit.getId();
			
			String sourceAsString = hit.getSourceAsString();
			Map map = new ObjectMapper().readValue(sourceAsString, Map.class);
			
			maps.add(map);
		}
		
		return maps;
	}
	
	public ArrayList<Map> getMaps(RestHighLevelClient client, int from, int size) throws IOException {
		ArrayList<Map> maps = new ArrayList<Map>();
		
		SearchRequest searchRequest = new SearchRequest("maps"); 
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder(); 
		searchSourceBuilder.query(QueryBuilders.matchAllQuery()); 
		searchSourceBuilder.from(from); 
		searchSourceBuilder.size(size);
		searchRequest.source(searchSourceBuilder);
		
		SearchResponse searchResponse = client.search(searchRequest);
		SearchHits hits = searchResponse.getHits();
		long totalHits = hits.getTotalHits();
		float maxScore = hits.getMaxScore();
		System.out.println("total : "+totalHits+", maxScore : "+maxScore);
		
		SearchHit[] searchHits = hits.getHits();
		for (SearchHit hit : searchHits) {
		    // do something with the SearchHit
			//String index = hit.getId();
			
			String sourceAsString = hit.getSourceAsString();
			Map map = new ObjectMapper().readValue(sourceAsString, Map.class);
			
			maps.add(map);
		}
		
		return maps;
	}

	public Map getOneMap(RestHighLevelClient client, String name) throws IOException {
		GetRequest getRequest = new GetRequest(
		        "maps", 
		        "doc",  
		        name); 
		String sourceAsString = "";
		try {
			
			GetResponse getResponse = client.get(getRequest);

			if (getResponse.isExists()) {
			    sourceAsString = getResponse.getSourceAsString();        
			    //System.out.println(sourceAsString+"\n");
			} else {
			    System.out.println("Impossible de trouver l'User "+name);
			    return null;
			}
		} catch (ElasticsearchException e) {
		    if (e.status() == RestStatus.NOT_FOUND) {
		        
		    }
		}

		return new ObjectMapper().readValue(sourceAsString, Map.class);
	}


	public ArrayList<Map> getPublicMaps(RestHighLevelClient client) throws IOException{
		ArrayList<Map> maps = new ArrayList<Map>();
		
		SearchRequest searchRequest = new SearchRequest("maps"); 
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder(); 
		searchSourceBuilder.query(QueryBuilders.termQuery("isPublic", "true"));
		searchRequest.source(searchSourceBuilder);
		
		SearchResponse searchResponse = client.search(searchRequest);
		SearchHits hits = searchResponse.getHits();
		long totalHits = hits.getTotalHits();
		float maxScore = hits.getMaxScore();
		System.out.println("total : "+totalHits+", maxScore : "+maxScore);
		
		SearchHit[] searchHits = hits.getHits();																																																															
		for (SearchHit hit : searchHits) {
		    // do something with the SearchHit
			//String index = hit.getId();
			
			String sourceAsString = hit.getSourceAsString();
			Map map = new ObjectMapper().readValue(sourceAsString, Map.class);
			
			maps.add(map);
		}
		
		return maps;
	}
	
	public ArrayList<Map> getPublicMaps(RestHighLevelClient client, int from, int size) throws IOException{
		ArrayList<Map> maps = new ArrayList<Map>();
		
		SearchRequest searchRequest = new SearchRequest("maps"); 
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder(); 
		searchSourceBuilder.query(QueryBuilders.termQuery("isPublic", "true"));
		searchSourceBuilder.from(from); 
		searchSourceBuilder.size(size);
		searchRequest.source(searchSourceBuilder);
																				
		SearchResponse searchResponse = client.search(searchRequest);
		SearchHits hits = searchResponse.getHits();																																																																																									
		long totalHits = hits.getTotalHits();
		float maxScore = hits.getMaxScore();
		
		System.out.println("total : "+totalHits+", maxScore : "+maxScore);
		
		SearchHit[] searchHits = hits.getHits();
		for (SearchHit hit : searchHits) {
		    // do something with the SearchHit
			//String index = hit.getId();
			
			String sourceAsString = hit.getSourceAsString();
			Map map = new ObjectMapper().readValue(sourceAsString, Map.class);
			
			maps.add(map);
		}
		
		return maps;
	}

	
	public ArrayList<Map> getPublicMapsByUsername(RestHighLevelClient client, String username, int from, int size, boolean only_public, boolean only_private) throws IOException{
		ArrayList<Map> maps = new ArrayList<Map>();
		
		SearchRequest searchRequest = new SearchRequest("maps"); 
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder(); 
		searchSourceBuilder.query(QueryBuilders.termQuery("username", username));
		if(only_public && !only_private)
			searchSourceBuilder.query(QueryBuilders.termQuery("isPublic", "true"));
		if(only_private && !only_public)
			searchSourceBuilder.query(QueryBuilders.termQuery("isPublic", "false"));
		searchSourceBuilder.from(from); 
		searchSourceBuilder.size(size);
		searchRequest.source(searchSourceBuilder);
		
		SearchResponse searchResponse = client.search(searchRequest);
		SearchHits hits = searchResponse.getHits();
		long totalHits = hits.getTotalHits();
		float maxScore = hits.getMaxScore();
		System.out.println("total : "+totalHits+", maxScore : "+maxScore);
		
		SearchHit[] searchHits = hits.getHits();
		for (SearchHit hit : searchHits) {
		    // do something with the SearchHit
			//String index = hit.getId();
			
			String sourceAsString = hit.getSourceAsString();
			Map map = new ObjectMapper().readValue(sourceAsString, Map.class);
			if(compareNamesMapUser(map.id, username))
				maps.add(map);
		}
		
		return maps;
	}
	public String concatMapName(String map_name, String username)
	{
		return username+"_"+map_name;
	}
	public String parseMapName(String map_name)//Retourne le vrai nom de la map
	{
		String real_name ="";
		boolean ins = false;
		for(int i =0;i<map_name.length();i++)
		{
			if(map_name.charAt(i) == '_')
				ins = true;
			else if(ins)
				real_name += map_name.charAt(i);
		}
		return real_name;
	}
	private boolean compareNamesMapUser(String map_name, String user_name)
	{
		boolean und = false;
		int c = 0;
		for(int i =0;i<user_name.length();i++)
		{
			if(user_name.charAt(i) != map_name.charAt(i))
				return false;

		}
		return true;
	}

	public ArrayList<ArrayList<Map>> getFriendsMapByUsername(RestHighLevelClient client, String username, int from, int size) throws IOException{
		ArrayList<String> friends = DAO.getActionUser().getOneUser(client, username).friends;
		ArrayList<ArrayList<Map>> maps = new ArrayList<ArrayList<Map>>();
		for(String friend : friends)
		{
			maps.add(getPublicMapsByUsername(client, friend,from, size, true, false));
		}
		return maps;
	}

	public boolean insertMap(RestHighLevelClient client, Map map) throws IOException{
		java.util.Map<String, Object> jsonMap = new HashMap<String, Object>();
		
		jsonMap.put("id", map.id);
		jsonMap.put("name", map.name);
		jsonMap.put("username", map.username);
		jsonMap.put("locations", map.locations);
		jsonMap.put("privateUsers", map.privateUsers);
		jsonMap.put("isPublic", map.isPublic);
		jsonMap.put("isFavorite", map.isFavorite);
		
		
		IndexRequest indexRequest = new IndexRequest("maps", "doc",map.id)
		        .source(jsonMap)
		        .opType(DocWriteRequest.OpType.CREATE);
		
		try {
			IndexResponse indexResponse = client.index(indexRequest);
		} catch(ElasticsearchException e) {
		    if (e.status() == RestStatus.CONFLICT) {
		        System.out.println("insert ne fonctionne pas (la map existe déjà ?)");
		        return false;
		    }
		}
		return true;
	}

	public boolean deleteMap(RestHighLevelClient client, String map_id) throws IOException {
		Map map = this.getOneMap(client, map_id);
		ArrayList<String> locations_id  = map.getLocations();
		DeleteRequest request = new DeleteRequest(
		        "maps",    
		        "doc",     
		        map_id);
		DeleteResponse deleteResponse = client.delete(request);
		 
		for (String location : locations_id) {
			DAO.getActionLocation().deleteLocation(client, location);
		}
		
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

	public boolean updateMap(RestHighLevelClient client, Map map) throws IOException{
		java.util.Map<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("locations", map.locations);
		jsonMap.put("privateUsers", map.privateUsers);
		jsonMap.put("isPublic", map.isPublic);
		jsonMap.put("isFavorite", map.isFavorite);
		UpdateRequest request = new UpdateRequest("maps", 
		        "doc",  
		        map.id)
		        .doc(jsonMap);
		UpdateResponse updateResponse = client.update(request);
		request.docAsUpsert(false);
		
		if (updateResponse.getResult() == DocWriteResponse.Result.UPDATED) {
			System.out.println("La map "+map.id+ " a été mis à jour");
		} else if (updateResponse.getResult() == DocWriteResponse.Result.DELETED) {
			System.out.println("La map "+map.id+ " a été supprimé");
			return false;
		} else if (updateResponse.getResult() == DocWriteResponse.Result.NOOP) {
			System.out.println("La map "+map.id+ " n'a pas pu être mis à jour");
			return false;
		}
		return true;
	}

	public ArrayList<Map> searchMap(RestHighLevelClient client, String nameToSearch, int from, int size, boolean only_public, boolean only_private) throws IOException {
		ArrayList<Map> maps = new ArrayList<Map>();
		
		SearchRequest searchRequest = new SearchRequest("maps"); 
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder(); 
		if(only_public && !only_private)
			searchSourceBuilder.query(QueryBuilders.termQuery("isPublic", "true"));
		if(only_private && !only_public)
			searchSourceBuilder.query(QueryBuilders.termQuery("isPublic", "false"));
		MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder("nameToSearch", nameToSearch);
		matchQueryBuilder.fuzziness(Fuzziness.AUTO); //Pour chercher un username proche 
		matchQueryBuilder.maxExpansions(5);
		searchSourceBuilder.query(matchQueryBuilder); 
		searchSourceBuilder.from(from); 
		searchSourceBuilder.size(size);
		searchRequest.source(searchSourceBuilder);
		
		SearchResponse searchResponse = client.search(searchRequest);
		SearchHits hits = searchResponse.getHits();
		long totalHits = hits.getTotalHits();
		float maxScore = hits.getMaxScore();
		System.out.println("total : "+totalHits+", maxScore : "+maxScore);
		
		SearchHit[] searchHits = hits.getHits();
		for (SearchHit hit : searchHits) {
		    // do something with the SearchHit
			//String index = hit.getId();
			
			String sourceAsString = hit.getSourceAsString();
			Map map = new ObjectMapper().readValue(sourceAsString, Map.class);
			
			maps.add(map);
		}
		
		return maps;
	}

	public void createIndexMap(RestHighLevelClient client) throws IOException {
		
		try
		{
			CreateIndexRequest request = new CreateIndexRequest("maps");
			CreateIndexResponse createIndexResponse = client.indices().create(request);
		}catch(Exception e)
		{
			System.out.println("l'index users existe déjà");
		}
		
		
	}

	public ArrayList<Map> getMapsByTime(LocalDateTime date) {
		// TODO Auto-generated method stub
		return null;
	}

}