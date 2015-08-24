/**
 * 
 */
package twitterharvest;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

import com.google.common.collect.Lists;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Client;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.endpoint.Location;
import com.twitter.hbc.core.endpoint.StatusesFilterEndpoint;
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import com.twitter.hbc.twitter4j.handler.StatusStreamHandler;
import com.twitter.hbc.twitter4j.message.DisconnectMessage;

/**
 * @author wyt
 * 
 */
public class TwitterHarvestor {
	public static void oauth(String consumerKey, String consumerSecret,
			String token, String secret) throws InterruptedException {
		// Create an appropriately sized blocking queue
		BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10000);

		// Define our endpoint: By default, delimited=length is set (we need
		// this for our processor)
		// and stall warnings are on.
		StatusesSampleEndpoint endpoint = new StatusesSampleEndpoint();
		endpoint.stallWarnings(false);

		Authentication auth = new OAuth1(consumerKey, consumerSecret, token,
				secret);
		// Authentication auth = new
		// com.twitter.hbc.httpclient.auth.BasicAuth(username, password);

		// Create a new BasicClient. By default gzip is enabled.
		BasicClient client = new ClientBuilder().name("sampleExampleClient")
				.hosts(Constants.STREAM_HOST).endpoint(endpoint)
				.authentication(auth)
				.processor(new StringDelimitedProcessor(queue)).build();

		// Establish a connection
		client.connect();

		// Do whatever needs to be done with messages
		for (int msgRead = 0; msgRead < 1000; msgRead++) {
			if (client.isDone()) {
				System.out.println("Client connection closed unexpectedly: "
						+ client.getExitEvent().getMessage());
				break;
			}

			String msg = queue.poll(5, TimeUnit.SECONDS);
			if (msg == null) {
				System.out.println("Did not receive a message in 5 seconds");
			} else {
				System.out.println(msg);
			}
		}

		client.stop();

		// Print some stats
		System.out.printf("The client read %d messages!\n", client
				.getStatsTracker().getNumMessages());
	}

	public static void main(String[] args) {
		try {
			// TwitterHarvestor.oauth("cfVE9HuIEpnnZu1QluNXeA",
			// TwitterHarvestor.oauthFilter(args[0], args[1], args[2],
			// args[3]);//
			// TwitterHarvestor.oauthFilter("cfVE9HuIEpnnZu1QluNXeA",
			TwitterHarvestor.oauthWithListeners("kzZFKtaU87VAvF0mERzKHg",
					"qxL9NkjhHbsonEUF5xAkoEiKDwYVvwxpcUqSRQJiY44",
					"1897146356-G6kPjJSKjSGS7uZEnRtSMI4295xMxrhPAwkLO9c",
					"6WyFyQRyKVcZHCoZgnOeZNLQGC4NeIvqpxK2X9eGV0");// han
			// TwitterHarvestor.oauthWithListeners("cfVE9HuIEpnnZu1QluNXeA",
			// "6jbZxWTFJnKtsOXSJVMt1izlziEDtKXMybbka9tiM",
			// "1883130967-8ITtv7ppxK1RAN0Pfpnvv72MFcXmQREKVoMBB95",
			// "rxo8mKrPRMfcPDCpUlSsik5lVHZQOm72wsIALhkt8iw");//yutong
		} catch (InterruptedException e) {
			System.out.println(e);
			// System.exit(-1);
		}
		// System.exit(0);
	}

	public static void oauthFilter(String consumerKey, String consumerSecret,
			String token, String secret) throws InterruptedException {
		BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10000);
		StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
		// add some track terms
		// endpoint.trackTerms(Lists.newArrayList("twitterapi", "#yolo"));
		endpoint.locations(Lists.newArrayList(new Location(
				new Location.Coordinate(144.553192, -38.225029),
				new Location.Coordinate(145.549774, -37.540119))));// Melbourn
																	// bund
		Authentication auth = new OAuth1(consumerKey, consumerSecret, token,
				secret);
		// Authentication auth = new BasicAuth(username, password);

		// Create a new BasicClient. By default gzip is enabled.
		Client client = new ClientBuilder().hosts(Constants.STREAM_HOST)
				.endpoint(endpoint).authentication(auth)
				.processor(new StringDelimitedProcessor(queue)).build();

		// Establish a connection
		client.connect();

		// Do whatever needs to be done with messages
		for (int msgRead = 0; msgRead < 1000; msgRead++) {
			String msg = queue.take();
			System.out.println(msg);
		}

		client.stop();

	}

	public static void oauthWithListeners(String consumerKey,
			String consumerSecret, String token, String secret)
			throws InterruptedException {
		// Create an appropriately sized blocking queue
		BlockingQueue<String> queue = new LinkedBlockingQueue<String>(10000);

		// Define our endpoint: By default, delimited=length is set (we need
		// this for our processor)
		// and stall warnings are on.
		StatusesFilterEndpoint endpoint = new StatusesFilterEndpoint();
		// add some track terms
		// endpoint.trackTerms(Lists.newArrayList("twitterapi", "#yolo"));
		endpoint.locations(Lists.newArrayList(new Location(
				new Location.Coordinate(144.553192, -38.225029),
				new Location.Coordinate(145.549774, -37.540119))));// Melbourne
		// bund
		// new Location.Coordinate(144.987305, -37.826599),//richmond

		Authentication auth = new OAuth1(consumerKey, consumerSecret, token,
				secret);

		// Create a new BasicClient. By default gzip is enabled.
		BasicClient client = new ClientBuilder().hosts(Constants.STREAM_HOST)
				.endpoint(endpoint).authentication(auth)
				.processor(new StringDelimitedProcessor(queue)).build();

		// Create an executor service which will spawn threads to do the actual
		// work of parsing the incoming messages and
		// calling the listeners on each message
		int numProcessingThreads = 4;
		ExecutorService service = Executors
				.newFixedThreadPool(numProcessingThreads);

		// Wrap our BasicClient with the twitter4j client
		TwitterTrackingClient t4jClient = new TwitterTrackingClient(client,
				queue, Lists.newArrayList(listener1, listener2), service);

		// Establish a connection
		t4jClient.connect();
		for (int threads = 0; threads < numProcessingThreads; threads++) {
			// This must be called once per processing thread
			t4jClient.process();
		}

		Thread.sleep(64800000);

		client.stop();
		System.exit(0);
	}

	// A bare bones listener
	private static StatusListener listener1 = new StatusListener() {
		@Override
		public void onStatus(Status status) {

		}

		@Override
		public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
		}

		@Override
		public void onTrackLimitationNotice(int limit) {
		}

		@Override
		public void onScrubGeo(long user, long upToStatus) {
		}

		@Override
		public void onStallWarning(StallWarning warning) {
		}

		@Override
		public void onException(Exception e) {
		}
	};

	// A bare bones StatusStreamHandler, which extends listener and gives some
	// extra functionality
	private static StatusListener listener2 = new StatusStreamHandler() {
		@Override
		public void onStatus(Status status) {
		}

		@Override
		public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
		}

		@Override
		public void onTrackLimitationNotice(int limit) {
		}

		@Override
		public void onScrubGeo(long user, long upToStatus) {
		}

		@Override
		public void onStallWarning(StallWarning warning) {
		}

		@Override
		public void onException(Exception e) {
		}

		@Override
		public void onDisconnectMessage(DisconnectMessage message) {

		}

		@Override
		public void onUnknownMessageType(String s) {
		}
	};
}
