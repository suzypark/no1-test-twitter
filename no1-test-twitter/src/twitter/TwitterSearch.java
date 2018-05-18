package twitter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.logging.Logger;

import twitter4j.MediaEntity;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterSearch {

	static final Logger logger = Logger.getLogger(TwitterSearch.class.getName());
	// 取得件数
	private static final int TWEET_COUNT = 10;
	// Twitter API関連情報
	private static final String CONSUMER_KEY = "";
	private static final String CONSUMER_SECRET = "";
	private static final String ACCESS_TOKEN = "";
	private static final String ACCESS_TOKEN_SECRET = "";
	// 検索キーワード
	private static final String SEARCH_KEYWORD = "JustinBieber";

	/**
	 * 画像検索
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws TwitterException, IOException {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
			.setOAuthConsumerKey(CONSUMER_KEY)
			.setOAuthConsumerSecret(CONSUMER_SECRET)
			.setOAuthAccessToken(ACCESS_TOKEN)
			.setOAuthAccessTokenSecret(ACCESS_TOKEN_SECRET);
		TwitterFactory tf = new TwitterFactory(cb.build());
		Twitter twitter = tf.getInstance();
		// 検索を行う
		Query query = new Query(SEARCH_KEYWORD);
		query.setCount(TWEET_COUNT);
		QueryResult result = null;
		try {
			result = twitter.search(query);
			//logger.info(result.toString());
		} catch (TwitterException e) {
			e.printStackTrace();
		}

		for (Status status : result.getTweets()) {
			// リツイートに含まれる画像は含まないようにする
			if (status.isRetweeted()) {
				continue;
			}
			MediaEntity[] medias = status.getMediaEntities();
			for (MediaEntity me : medias) {
				try {
					URL url = new URL(me.getMediaURL());
					ReadableByteChannel rbc = Channels.newChannel(url.openStream());
					// 保存する画像名に検索キーワードとツイートの作成日を付与する
					DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
					FileOutputStream fos = new FileOutputStream(
							SEARCH_KEYWORD + df.format(status.getCreatedAt()) + setExtension(me.getType(), url));
					fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
					logger.info("file created");
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * 保存画像の拡張子はオリジナル画像と同じ拡張子を付与する
	 * 
	 * @param　type MediaEntityのタイプ
	 * @param　url　MediaEntityのurl
	 * @return 拡張子
	 */
	private static String setExtension(String type, URL url) {
		if (type.equals("photo")) {
			// TODO
			return getUrlFileExtension(url.getFile());
		} else if (type.equals("video")) {
			return ".mp4";
		} else if (type.equals("animated_gif")) {
			return ".gif";
		}
		return ".err";
	}

	/**
	 * 画像の拡張子をチェッし、対象拡張子を返却
	 * 
	 * @param URLのファイル情報
	 * @return 拡張子
	 */
	private static String getUrlFileExtension(String fileStr) {
		String checkExtension = fileStr.substring(fileStr.lastIndexOf(".") + 1, fileStr.length());
		if (checkExtension.equals("jpeg")) {
			return ".jpeg";
		} else if (checkExtension.equals("bmp")) {
			return ".bmp";
		} else if (checkExtension.equals("gif")) {
			return ".gif";
		} else if (checkExtension.equals("png")) {
			return ".png";
		}
		return ".jpg";
	}

}
