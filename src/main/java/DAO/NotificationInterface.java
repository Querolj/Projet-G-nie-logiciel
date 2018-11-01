package DAO;

import java.io.IOException;
import java.util.ArrayList;

import org.elasticsearch.client.RestHighLevelClient;

public interface NotificationInterface {

	public boolean insertNotificationAndUpdateUser(RestHighLevelClient client, Notification n, String username) throws IOException;
	
	public ArrayList<Notification> getNotificationsOfUser(RestHighLevelClient client, String username, int from, int size) throws IOException;
	
	public boolean deleteNotification(RestHighLevelClient client, String id) throws IOException;
	
	public void createIndexNotification(RestHighLevelClient client) throws IOException;
}
