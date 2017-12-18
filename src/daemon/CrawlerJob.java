package daemon;

import java.util.TimerTask;
import util.Crawler.Crawler;

public class CrawlerJob extends TimerTask {
	private Crawler crawler;

	public CrawlerJob() {
		crawler = new Crawler();
	}

	public void run() {
		crawler.updateNewsTable();
	}
}
