package DAO;

import java.io.IOException;
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
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

public class NotificationDAO implements NotificationInterface{
	
	public Notification getOneNotification(RestHighLevelClient client, String id_notif) throws IOException{
		GetRequest getRequest = new GetRequest(
		        "notifications", 
		        "doc",  
		        id_notif); 
		String sourceAsString = "";
		try {
			
			GetResponse getResponse = client.get(getRequest);

			if (getResponse.isExists()) {
			    sourceAsString = getResponse.getSourceAsString();        
			    //System.out.println(sourceAsString+"\n");
			} else {
			    System.out.println("Impossible de trouver la notification "+id_notif);
			    return null;
			}
		} catch (ElasticsearchException e) {
		    if (e.status() == RestStatus.NOT_FOUND) {
		        
		    }
		}

		return new ObjectMapper().readValue(sourceAsString, Notification.class);
	}
	
	public boolean insertNotificationAndUpdateUser(RestHighLevelClient client, Notification n, String username) throws IOException
	{
		java.util.Map<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("username", n.username);
		jsonMap.put("message", n.message);
		jsonMap.put("messageType", n.messageType);
		jsonMap.put("readed", n.readed);
		IndexRequest indexRequest = new IndexRequest("notifications", "doc")
		        .source(jsonMap)
		        .opType(DocWriteRequest.OpType.INDEX);
		String id = indexRequest.id();
		User user= DAO.getActionUser().getOneUser(client, username);
		user.addNotifications(id);
		
		indexRequest.id();
		try {
			IndexResponse indexResponse = client.index(indexRequest);
			DAO.getActionUser().updateUser(client, user);
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
	
	public ArrayList<Notification> getNotificationsOfUser(RestHighLevelClient client, String username, int from, int size) throws IOException{
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
			hit.getId();
			String sourceAsString = hit.getSourceAsString();
			Notification n = new ObjectMapper().readValue(sourceAsString, Notification.class);
			
			notifs.add(n);
		}
		
		return notifs;
	}
	
	public boolean deleteNotification(RestHighLevelClient client, String id_notif) throws IOException {
		Notification notif = this.getOneNotification(client, id_notif);
		DeleteRequest request = new DeleteRequest(
		        "notifications",    
		        "doc",     
		        id_notif);
		
		User user = DAO.getActionUser().getOneUser(client, notif.username);
		user.removeNotifications(id_notif);
		DAO.getActionUser().updateUser(client, user);
		
		DeleteResponse deleteResponse = client.delete(request);
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
	public void createIndexNotification(RestHighLevelClient client) throws IOException {
		try {
			CreateIndexRequest request = new CreateIndexRequest("notifications");
			CreateIndexResponse createIndexResponse = client.indices().create(request);
		}catch(Exception e)
		{
			System.out.println("l'index notification existe déjà");
		}
		
	}
}
