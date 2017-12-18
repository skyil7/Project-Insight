package util.Crawler;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mysql.cj.api.jdbc.Statement;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import jdbc_connection.JdbcUtil;

public class Crawler {
	private Elements newsItem;

	public Elements getDataSet(String url, String selector) {
		try {
			Document doc = Jsoup.connect(url).get();
			this.newsItem = doc.select(selector);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String[] makeATags(String title, String href) {
		String[] aTags = new String[newsItem.size()];
		for (int i = 0; i < newsItem.size(); i++) {
			Element item = newsItem.get(i);
			String content = item.select(title).text();
			String link = item.select(href).text();
			aTags[i] = "<a href='" + link + "'>" + content + "</a>";
		}
		return aTags;
	}

	public int updateNewsTable() {
		Connection conn = JdbcUtil.getConnection();
		int cnt[] = new int[3];
		if (conn == null) {
			return -1;
		}
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		ResultSet insertKeyRs = null;

		try {
			pstmt = conn.prepareStatement("select * from record_log order by time desc limit 0, 1");
			rs = pstmt.executeQuery();

			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			if (rs.next()) {
				Timestamp lastInsertTime = rs.getTimestamp("time");
				long delta = timestamp.getTime() - lastInsertTime.getTime();

				if (delta < 1000 * 60 * 59) {// 1 hour
					return 0;
				}
			}

			pstmt.close();
			pstmt = conn.prepareStatement("insert into record_log(time) values(?)", Statement.RETURN_GENERATED_KEYS);
			// record update time
			pstmt.setTimestamp(1, timestamp);
			pstmt.executeUpdate();
			insertKeyRs = pstmt.getGeneratedKeys();
			// insert A.I key to all News before update
			int timeId;
			if (insertKeyRs.next()) {
				timeId = insertKeyRs.getInt(1);
			} else {
				return -1;
			}
			String url[] = { "https://news.google.com/news/rss/search/section/q/korea/korea?hl=en&ned=us",
					"https://news.google.com/news/rss/headlines/section/topic/TECHNOLOGY?ned=us&hl=en",
					"https://news.google.com/news/rss/search/section/q/military/military?hl=en&ned=us" };
			String category[] = { "poli", "tech", "military" };
			for (int d = 0; d < url.length; d++) {
				getDataSet(url[d], "item");

				for (int i = 0; i < newsItem.size(); i++) {
					Element item = newsItem.get(i);
					String title = item.select("title").text();
					String link = item.select("link").text();
					String pubDate = item.select("pubDate").text();
					cnt[d] += insertNews(timeId, title, link, pubDate, conn, category[d]);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return -1;
		} finally {
			try {
				pstmt = conn.prepareStatement("update record_log set poli=" + cnt[0] + ", tech=" + cnt[1] + ", mili="
						+ cnt[2] + " where poli is null");
				pstmt.executeUpdate();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// count the changed column for chart
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e) {
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e) {
				}
			}
			if (insertKeyRs != null) {
				try {
					insertKeyRs.close();
				} catch (Exception e) {
				}
			}
		}
		return cnt[0];
	}

	private int insertNews(int id, String title, String href, String date, Connection conn, String category) {
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement("insert into news(id,link,title,pubDate,category) values(?,?,?,?,?)");
			pstmt.setInt(1, id);
			pstmt.setString(2, href);
			pstmt.setString(3, title);
			pstmt.setString(4, date);
			pstmt.setString(5, category);

			pstmt.executeUpdate();
		} catch (SQLException ex) {
			// Duplicate inspection
			ex.printStackTrace();
			return 0;
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (Exception e) {
				}
			}
		}
		return 1;
	}

	public String[][] getChart() {
		String[][] chart = new String[4][4];
		Connection conn = JdbcUtil.getConnection();
		ResultSet rs = null;
		int id = 0;
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement("select max(id) id from record_log");
			rs = pstmt.executeQuery();
			rs.next();
			id = rs.getInt("id");
		} catch (SQLException ex) {
			ex.printStackTrace();
		} finally {
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		for (int i = 0; i < 4; i++) {
			try {
				pstmt = conn.prepareStatement("select time, poli, tech, mili from record_log where id = ?");
				pstmt.setInt(1, id - i);
				rs = pstmt.executeQuery();
				rs.next();
				chart[i][0] = rs.getString("time");
				chart[i][1] = Integer.toString(rs.getInt("poli"));
				chart[i][2] = Integer.toString(rs.getInt("tech"));
				chart[i][3] = Integer.toString(rs.getInt("mili"));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				if (pstmt != null)
					try {
						pstmt.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
				if (rs != null)
					try {
						rs.close();
					} catch (SQLException e) {
						e.printStackTrace();
					}
			}
		}
		if (conn != null)
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		return chart;
	}
}
