/**
 * 
 */
package twitterharvest;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

import org.lightcouch.CouchDbClient;

import twitter4j.Status;
import twitter4j.StatusListener;
import twitter4j.TwitterException;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.internal.json.z_T4JInternalJSONImplFactory;
import twitter4j.internal.org.json.JSONException;
import twitter4j.internal.org.json.JSONObject;
import twitter4j.json.JSONObjectType;

import com.twitter.hbc.core.Client;
import com.twitter.hbc.twitter4j.Twitter4jStatusClient;

/**
 * @author wyt
 * 
 */
public class TwitterTrackingClient extends Twitter4jStatusClient {

	private final CouchDbClient dbClient;
	private final z_T4JInternalJSONImplFactory factory;

	public TwitterTrackingClient(Client client,
			BlockingQueue<String> blockingQueue,
			List<? extends StatusListener> listeners,
			ExecutorService executorService) {
		super(client, blockingQueue, listeners, executorService);
		this.factory = new z_T4JInternalJSONImplFactory(
				new ConfigurationBuilder().build());
		this.dbClient = new CouchDbClient("db-twitters", true, "http",
				"115.146.85.167", 5984, null, null);
	}

	@Override
	protected void parseMessage(String msg) throws JSONException,
			TwitterException, IOException {
		JSONObject json = new JSONObject(msg);
		long sitestreamUser = getSitestreamUser(json);
		JSONObjectType.Type type = JSONObjectType.determine(json);
		if (type == JSONObjectType.Type.STATUS) {
			processStatus(sitestreamUser, json);
		} else {
			onUnknownMessageType(json.toString());
		}
	}

	private void processStatus(long sitestreamUser, JSONObject json)
			throws TwitterException, JSONException {
		Status status = this.factory.createStatus(json);
		onStatus(sitestreamUser, status);
		storeTwitterToCouchDB(json);
	}

	private synchronized void storeTwitterToCouchDB(JSONObject json)
			throws JSONException {
		// String docId = status.getUser().getName() + " "
		// + status.getCreatedAt().toString();
		// JsonObject aJson = new JsonObject();
		// JSONObject aJson;
		// aJson = preprocessMessage(json);
		// aJson.put("_id", docId);
		// aJson.addProperty("_id", docId);
		// aJson.addProperty("rawJson", json.toString());
		dbClient.save(json);

	}
}
