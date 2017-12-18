<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ page import="util.Crawler.Crawler"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<script type="text/javascript"
	src="https://www.gstatic.com/charts/loader.js"></script>
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
<script type="text/javascript">
	google.charts.load('current', {
		'packages' : [ 'corechart' ]
	});
	google.charts.setOnLoadCallback(drawChart);
<%
	Crawler crawler = new Crawler();
	String[][] chart = crawler.getChart();
	
%>
	function drawChart() {
		var data = google.visualization.arrayToDataTable([
				[ 'day', 'Politics', 'Technology', 'Military' ],
				["<%= chart[3][0]%>",<%=chart[3][1]%>,<%=chart[3][2]%>,<%=chart[3][3]%>], ["<%=chart[2][0]%>",<%=chart[2][1]%>,<%=chart[2][2]%>,<%=chart[2][3]%>],
				["<%= chart[1][0]%>",<%=chart[1][1]%>,<%=chart[1][2]%>,<%=chart[1][3]%>], ["<%=chart[0][0]%>",<%=chart[0][1]%>,<%=chart[0][2]%>,<%=chart[0][3]%>] ]);

		var options = {
			title : 'What\'s New?',
			hAxis : {
				title : '',
				titleTextStyle : {
					color : '#333'
				}
			},
			vAxis : {
				minValue : 0
			}
		};

		var chart = new google.visualization.AreaChart(document
				.getElementById('chart_div'));
		chart.draw(data, options);
	}
</script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Project Insight</title>
<style type="text/css">
.title {
	color: white;
	text-align: center;
	font-size: 50px;
}

.box {
	float: left;
	width: 31.33%;
	padding: 0 20px;
	margin: 1%;
	box-sizing: border-box;
	background: #333;
}

.box h2 {
	width: 100%;
	padding: 20px 0;
	background: #333;
}

a {
	color: white;
	text-style: none;
	text-decoration: none;
}

ul {
	list-style: none;
}
</style>
</head>
<body bgcolor="black">
	<h1 class="title">Project Insight</h1>
	<h2 class="title" style="font-size: 30px">Predict the future by
		analyze the past</h2>
	<div align="center">
		<div id="chart_div" style="width: 1000px; height: 600px;"></div>
	</div>

	<div class="box">
		<h2 align="center" style="color: white">Politics</h2>
		<%
			crawler.getDataSet("https://news.google.com/news/rss/search/section/q/korea/korea?hl=en&ned=us", "item");

			String[] aTags = crawler.makeATags("title", "link");
			out.println("<ul>");
			for (String aTag : aTags) {
				out.println("<li>" + aTag + "</li><br/>");
			}
			out.println("</ul>");
		%>
	</div>
	<div class="box">
		<h2 align="center" style="color: white">Technology</h2>
		<%
			crawler.getDataSet("https://news.google.com/news/rss/headlines/section/topic/TECHNOLOGY?ned=us&hl=en",
					"item");

			aTags = crawler.makeATags("title", "link");
			out.println("<ul>");
			for (String aTag : aTags) {
				out.println("<li>" + aTag + "</li><br/>");
			}
			out.println("</ul>");
		%>
	</div>
	<div class="box">
		<h2 align="center" style="color: white">Military</h2>
		<%
			crawler.getDataSet("https://news.google.com/news/rss/search/section/q/military/military?hl=en&ned=us",
					"item");

			aTags = crawler.makeATags("title", "link");
			out.println("<ul>");
			for (String aTag : aTags) {
				out.println("<li>" + aTag + "</li><br/>");
			}
			out.println("</ul>");
		%>
	</div>
</body>
</html>