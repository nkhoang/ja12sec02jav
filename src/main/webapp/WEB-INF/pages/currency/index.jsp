<%--
  Created by IntelliJ IDEA.
  User: hoangnk
  Date: Nov 7, 2010
  Time: 4:48:25 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ include file="/common/taglibs.jsp" %>
<html >
<head ><title >Currency Monitor</title >

    <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.4.4/jquery.min.js" ></script >
    <script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8.7/jquery-ui.min.js" ></script >
    <script type="text/javascript" src="<c:url value='/js/chart/FusionCharts-3.2.1.js'/>" ></script >
    <script type="text/javascript" >
        $(function() {
            $( "vcb-startDate" ).datepicker({ dateFormat: 'dd/mm/yy' });
            $( "vcb-endDate" ).datepicker({ dateFormat: 'dd//mm/yy' });
                    renderChart(
                            '2010-11-08 01:01', '2010-11-08 23:01');
                });


        function renderChart(
                fromDateStr, toDateStr) {
            var chart = new FusionCharts(
                    "<c:url value='/swf/ZoomLine.swf'/>", "ChartId", "1024", "768", "0", "0");
            var xml;
            $.ajax(
                    {
                        url: '<c:url value="/data/chartData.html" />',
                        data: {
                            fromDate: fromDateStr,
                            toDate: toDateStr
                        },
                        dataType: 'xml',
                        type: 'GET',
                        success: function(
                                data) {

                            var string = (new XMLSerializer()).serializeToString(
                                    data);
                            xml = string.replace(
                                    new RegExp(
                                            "\\n", "g"), '');
                            chart.setDataXML(
                                    '' + xml);
                            chart.render(
                                    "chartdiv");
                        }
                    });
        }

        function renderChartFromForm() {
            var fromDateStr = $(
                    '#fromDateInput').val();
            var toDateStr = $(
                    '#toDateInput').val();

            renderChart(
                    fromDateStr, toDateStr);
        }
    </script >
</head >
<body >

<h1 >Welcome to Currency Monitor!!</h1 >

<label >Pick the start date</label >
<input id="vcb-startDate" />
<label >Pick the end date</label >
<input id="vcb-endDate" />

<div id="chartdiv" ></div >

<div >
    From <input id="fromDateInput" size="20" /> to <input id="toDateInput" size="20" />
    <button id="submitBtn" onclick="renderChartFromForm()" value="Update" name="updateBtn" title="Update" >Update
    </button >
</div >

</body >
</html >