<%--
  Created by IntelliJ IDEA.
  User: hoangnk
  Date: Nov 7, 2010
  Time: 4:48:25 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ include file="/common/taglibs.jsp" %>
<html>
<head><title>View Live Chart</title>

    <script type="text/javascript" src="<c:url value='/js/jquery-1.4.2.js'/>"></script>
    <script type="text/javascript" src="<c:url value='/js/chart/FusionCharts-3.2.1.js'/>"></script>
    <script type="text/javascript">
        $(function() {
            renderChart('2010-11-08 01:01','2010-11-08 23:01');
        })


        function renderChart(fromDateStr, toDateStr) {
            var chart = new FusionCharts("<c:url value='/swf/ZoomLine.swf'/>", "ChartId", "1024", "768", "0", "0");
            var xml;
            $.ajax({
                url: '<c:url value="/data/chartData.html" />',
                data: {
                    fromDate: fromDateStr,
                    toDate: toDateStr
                },
                dataType: 'xml',
                type: 'GET',
                success: function(data) {

                    var string = (new XMLSerializer()).serializeToString(data);
                    xml = string.replace(new RegExp("\\n", "g"), '');
                    chart.setDataXML('' + xml);
                    chart.render("chartdiv");
                }
            });
        }

        function renderChartFromForm() {
            var fromDateStr = $('#fromDateInput').val();
            var toDateStr = $('#toDateInput').val();

            renderChart(fromDateStr, toDateStr);
        }
    </script>
</head>
<body>

<div id="chartdiv"></div>

<div>
    <input id="fromDateInput" size="40"/>
    <input id="toDateInput" size="40" />

    <button id="submitBtn" onclick="renderChartFromForm()" value="Update" name="updateBtn" title="Update">Update</button>
</div>

</body>
</html>