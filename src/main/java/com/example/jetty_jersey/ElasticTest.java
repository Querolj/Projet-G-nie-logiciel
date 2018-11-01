package com.example.jetty_jersey;

import java.io.IOException;
import java.net.InetAddress;
import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionListener;
import org.elasticsearch.action.DocWriteRequest;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.open.OpenIndexRequest;
import org.elasticsearch.action.admin.indices.open.OpenIndexResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.*;
import org.elasticsearch.client.transport.*;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import DAO.DAO;
import DAO.Location;
import DAO.Map;
import DAO.Notification;
import DAO.User;
import DAO.UserDAO;


public class ElasticTest {
	public static void main(String args[]) throws IOException
	{
		
		RestHighLevelClient client = new RestHighLevelClient(
		        RestClient.builder(
		                new HttpHost("localhost", 9200, "http"),
		                new HttpHost("localhost", 9201, "http")));
		
		/*CreateIndexRequest request = new CreateIndexRequest("twitter");
		System.out.println("ah");
		request.mapping("tweet", 
			    "  {\n" +
			    "    \"tweet\": {\n" +
			    "      \"properties\": {\n" +
			    "        \"message\": {\n" +
			    "          \"type\": \"text\"\n" +
			    "        }\n" +
			    "      }\n" +
			    "    }\n" +
			    "  }", 
			    XContentType.JSON);*/
		/*DeleteIndexRequest request = new DeleteIndexRequest("users");
		DeleteIndexResponse deleteIndexResponse = client.indices().delete(request);
		boolean acknowledged = deleteIndexResponse.isAcknowledged(); 
		if(acknowledged)
			System.out.println("itsok");*/
		//CreateIndexRequest create_request = new CreateIndexRequest("notifications");
		//CreateIndexResponse createIndexResponse = client.indices().create(create_request);
		
		//boolean acknowledged = openIndexResponse.isAcknowledged();
		//System.out.println(acknowledged);

		
		// UPDATE_______________________________________
		
		/*Map<String, Object> jsonMap_update = new HashMap<String, Object>();
		jsonMap_update.put("message", "update !!!!cool");
		UpdateRequest request_update = new UpdateRequest("posts", "doc", "1")
		        .doc(jsonMap_update);
		
		UpdateResponse updateResponse = client.update(request_update);*/
		Notification n = new Notification("jean","salut",0);
		Notification n2 = new Notification("jean","he",1);
		Notification n3 = new Notification("jack","oye",0);
		
		insertNotification(client, n);
		insertNotification(client, n2);
		insertNotification(client, n3);
		System.out.println(getNotificationsOfUser(client, "jean",0,10));
		/*
		User user2 = new User("joe@gmail.com", "123");
		user2.username = "joe222";
		User user3 = new User("jony@gmail.com", "kk");
		user3.username = "jony";
		ArrayList<String> f = new ArrayList<String>();
		ArrayList<String> f2 = new ArrayList<String>();
		f.add(user3.username);
		f.add(user2.username);
		f2.add(user2.username);
		
		User user = new User("jeanOknewmail@gmail.com", "abc");
		user.username = "jeanok";
		user.friends = f2;
		user.maps = new ArrayList<String>();
		user2.maps = new ArrayList<String>();
		user3.maps = new ArrayList<String>();
		user2.friends =  new ArrayList<String>();
		user2.friends.add(user3.username);
		user3.friends = f2;                                                            
		DAO.getActionUser().insertUser(client, user2);
		//ArrayList<Map> maps = DAO.getActionUser().getMapsOfUser(client, "jean");
		//DAO.getActionUser().updateUser(client, user);
		
		System.out.println(DAO.getActionUser().getUsers(client));*/
		//System.out.print(DAO.getActionUser().getUsers(client));
		//InsertUser(user, client);
		//User user_get = GetUser("jean", client);
		//System.out.println("get : "+user);
		//System.out.println(GetAllUsers(client));
		client.close();
	
		
	}
	ActionListener<DeleteIndexResponse> listener = new ActionListener<DeleteIndexResponse>() {
	    public void onResponse(DeleteIndexResponse deleteIndexResponse) {
	    	System.out.println("ok?");
	    }

	    public void onFailure(Exception e) {
	    	System.out.println("failed");
	    }
	};
	
	public static boolean insertNotification(RestHighLevelClient client, Notification n) throws IOException
	{
		java.util.Map<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("username", n.username);
		jsonMap.put("message", n.message);
		jsonMap.put("messageType", n.messageType);
		jsonMap.put("readed", n.readed);
		IndexRequest indexRequest = new IndexRequest("notifications", "doc")
		        .source(jsonMap)
		        .opType(DocWriteRequest.OpType.INDEX);
		
		indexRequest.id();
		try {
			IndexResponse indexResponse = client.index(indexRequest);
			if (indexResponse.getResult() == DocWriteResponse.Result.CREATED) {
				System.out.println("not "+n.message+" créé");
			}
		} catch(ElasticsearchException e) {
		    if (e.status() == RestStatus.CONFLICT) {
		        System.out.println("insert ne fonctionne pas (la notif existe déjà ?)");
		        return false;
		    }
		}
		return true;
	}
	
	public static ArrayList<Notification> getNotificationsOfUser(RestHighLevelClient client, String username, int from, int size) throws IOException{
		ArrayList<Notification> notifs = new ArrayList<Notification>();
		
		SearchRequest searchRequest = new SearchRequest("notifications");
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder(); 
		searchSourceBuilder.query(QueryBuilders.matchAllQuery()); 
		searchSourceBuilder.query(QueryBuilders.termQuery("username", username));
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
			Notification n = new ObjectMapper().readValue(sourceAsString, Notification.class);
			
			notifs.add(n);
		}
		
		return notifs;
	}
	
	
	public static User GetUser(String username, RestHighLevelClient client) throws IOException
	{
		User user = null;
		GetRequest getRequest = new GetRequest(
		        "users", 
		        "doc",  
		        username); 
		String sourceAsString = "";
		try {
			
			GetResponse getResponse = client.get(getRequest);
			String index = getResponse.getIndex();
			String type = getResponse.getType();
			String id = getResponse.getId();
			if (getResponse.isExists()) {
			    long version = getResponse.getVersion();
			    sourceAsString = getResponse.getSourceAsString();        
			    java.util.Map<String, Object> sourceAsMap = getResponse.getSourceAsMap(); 
			    byte[] sourceAsBytes = getResponse.getSourceAsBytes();    
			    System.out.println(sourceAsString+"\n"+sourceAsMap.toString());
			} else {
			    
			}
		} catch (ElasticsearchException e) {
		    if (e.status() == RestStatus.NOT_FOUND) {
		        
		    }
		}

		user = new ObjectMapper().readValue(sourceAsString, User.class);


		return user;
	}
	
	public static ArrayList<User> GetAllUsers(RestHighLevelClient client) throws IOException
	{
		ArrayList<User> users = new ArrayList<User>();
		
		SearchRequest searchRequest = new SearchRequest("users"); 
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
			String index = hit.getId();
			
			String sourceAsString = hit.getSourceAsString();
			User user = new ObjectMapper().readValue(sourceAsString, User.class);
			System.out.println(user.username);
			users.add(user);
		}
		
		return users;
	}
	
	
	public static void InsertLocation(Location location, RestHighLevelClient client)
	{
		java.util.Map<String, Object> jsonMap = new HashMap<String, Object>();
		//jsonMap.put("username", user.username);
		
		IndexRequest indexRequest = new IndexRequest("posts", "doc", "2")
		        .source(jsonMap);
		
		try {
			IndexResponse indexResponse = client.index(indexRequest);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
