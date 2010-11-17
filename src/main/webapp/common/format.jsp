<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<link href="/eXoResources/skin/datepicker/jquery-ui-1.8.2.custom.css" rel="stylesheet" type="text/css"/>

<jsp:directive.include file="../include/taglibs.jsp"/>
<jsp:directive.include file="../include/globalMessage.jsp"/>

<%@page import="com.csc.integral.document.bean.SearchResult" %>

<style type="text/css">
    <style type

    =
    "text/css"
    >

    .firstColumn {
        width: 217px;
        height: auto;
        padding: 0px;
        margin-right: 2px;
    }

    .secondColumn {
        float: left;
        min-width: 7px;
        width: auto;
        right: 0;
        left: 0;
        background-color: white;
        min-height: 430px;
    }

    .thirdColumn {
        width: 700px;
        height: 430px;
    }

    img.floatbottom {
        float: bottom;
        margin: 4px;
    }

    .imgBlock {
        float: left;
        padding-top: 70px;
        padding-left: 110px;
    }

    .imgBlock2 {
        padding-top: 70px;
        padding-left: 470px;
    }

    .imgBlock3 {
        padding-top: 70px;
        padding-left: 470px;
        margin-bottom: 200px;
    }

    p.textBold {
        color: #4D81B1;
        font-weight: bold;
    }


</style>

<style type="text/css">
    #search_content {
        width: 920px;
        margin: 0 auto;
    }

    #search_content div.label {

        line-height: 22px;
        font-weight: bold;
    }

    #search_content .input {
        width: 200px;
    }

    /* 2 cols layout */

    #document_data_column {
        width: 659px;
        margin: 35px 30px 15px;
        float: left;
    }

    #file_data_column {
        width: 659 x;
        margin: 15px 30px 15px;
        float: left;
    }

    table.t {
        width: 609px;
    }

    table td.c1 {
        width: 30%;
    }

    table td.c2 {
        width: 40%;
    }

    table td.c3 {
        width: 30%;
    }

    /* Adjust calendar icon position */
    img.ui-datepicker-trigger {
        margin: 0 3px;
    }

    #fromCreatedDate {
        vertical-align: middle !important;
    }

    div.globalMessage {
        margin: 5px;
        padding-left: 5px;
    }

    table.buttontable {
        margin: 15px auto 15px;
    }

    #document_data_column fieldset {
        height: 110px;
        padding: 15px 25px 25px 25px;
    }

    #file_data_column fieldset {
        height: 55px;
        padding: 15px 25px 25px 25px;
    }

    /* page style - specific for this page */
    div.rb_content {
        width: 719px;
        height: 535px;
    }

    div.rb_content_top {
        width: 719px;
    }

    div.rb_content_top p {
        font-weight: bold;
        font-size: 16px;
        font-style: normal;
    }

    div.rb_content_middle {
        height: 480px;
        width: 100%;
    }

    div.rb_content_bottom {
        width: 102%;
    }

    div.rb_content_bottom_area2 {
        width: 677px;
    }

    /* Style input in popup */

    #search_client_content input {
        background: url("/eXoResources/skin/style/images/bg_input_01.gif") repeat-x scroll left top transparent;
        border: 1px solid #5085B5;
        font-size: 11px;
        font-weight: normal;
        height: 16px;
        padding: 0 2px;
    }

    div.sr-container {
        width: 689px;
        padding: 0 15px 0;
    }

    div.sr {
        width: 100%;
    }
</style>
<script type="text/javascript">
    $(function() {
        $('#fromCreatedDate').focus(function() {
            $(this).parent().find('div.error').hide();
        })
    });

    function submitPortlet(action) {
        var datePattern = /^(0[1-9]|[12][0-9]|3[01])[/](0[1-9]|1[012])[/](19|20)\d\d$/;// check datetime
        var fromCreatedDate = $('#fromCreatedDate').val();
        if (fromCreatedDate.length == 0 || fromCreatedDate.match(datePattern)) { // datetime can be null or must match the date pattern
            document.searchForm.action = action;
            document.searchForm.submit();
        } else {
            $('#fromCreatedDate').parent().find('div.error').show(); // show error message.
        }
    }

    function submitClientPortlet(action) {
        $('#searchClientInputHidden').val($('#searchClientInput').val());

        document.searchForm.action = action;
        document.searchForm.submit();
    }

    function selectClient(clientNum) {
        $('#clientNum').val(clientNum);
    }

    $(function() {
        $('#search_content input').keypress(function(e) { // handle enter keypress.
            if (e.which == 13) {
                $('#searchBtn').trigger('click'); // trigger submit.
                e.preventDefault();
            }
        });

        $("#fromCreatedDate").datepicker({
            dateFormat: 'dd/mm/yy',
            showOn: 'button',
            buttonImage: '/eXoResources/skin/datepicker/images/calendar.gif', // point to the buttom picture.
            buttonImageOnly: true // show button image
        });

        $('#client_search_btn').click(function() {
            showSearchClientPopup(); // show client search popup.
        });
    });

    function showSearchClientPopup() {
        $('#search_client_content').dialog({
            modal: true,
            resizable: false,
            position: 'center',
            title: 'Search Client',
            height: 'auto',
            width: '800px'
        });
        $('.ui-dialog').css('padding', '0px');
        $(".ui-widget-header").css('background', 'url("/eXoResources/screenFiles/roundedBox/bg_title_001.gif") repeat scroll 0 0 transparent');
        $(".ui-widget-header").css('border', 'none');
        $('.ui-corner-all').css('-moz-border-radius', '0px');
        $('.ui-widget-content').css('border', 'none');
        $('.ui-dialog-content').css('border', '1px solid #4d81b1');
        $('.ui-dialog-content').css('border-bottom', '#4d81b1 0px solid');
        $('.ui-dialog').css('border-width', '0px');
        $('.ui-dialog .ui-dialog-titlebar').css('padding', '0.5em 1em 0.2em');
        if ($('.ui-dialog .rb_left').length == 0) {
            $('.ui-dialog').append($(
                    '<div class="rb_left"></div>'
                            + '<div style="width:' + ($('.ui-dialog').width() - 44) + 'px;background-color:white;background-image:none;border-bottom:1px solid #4D81B1;float:left;height:17px;"></div>'
                            + '<div class="rb_right"></div>'
                    ));
        }


    }
</script>
<spring:eval var="isUsingPopup" expression="pageData.usingPopup == true"></spring:eval>
<c:if test="${isUsingPopup }">
    <script type="text/javascript">
        $(function() {
            showSearchClientPopup();
        });
    </script>
</c:if>
<style type="text/css">
    #client_search_btn {
        cursor: pointer;
    }

    #dialog_content {
        position: absolute;
        left: -99999px;
    }

</style>

<!-- Form Content -->
<form:form name="searchForm" modelAttribute="pageData" method="POST" action="">
<!-- Dialog content -->
<div id="dialog_content">
    <form:input path="searchClientCriteria.clientNum" id="searchClientInputHidden" maxlength="8"
                cssStyle="display: none"/>


    <div id="search_client_content">
        <jsp:directive.include file="searchClient.jsp"/>
    </div>
</div>

<div id="portlet" class="mainView">


<div style="float:left;">
    <div style="margin-bottom:5px">
        <img src="/eXoResources/screenFiles/index/img_logo_new.GIF" name="pic1">
    </div>

    <div class="firstColumn" style="float:bottom;">
        <div class="rb_content_top">
            <p>
                <spring:message code="com.csc.integral.portlet.index.links"/>
            </p>
        </div>

        <div class="rb_content_middle">
            <div class="menu">
                <script type="text/javascript" src="/csc-integral-web/js/common.js"></script>
                <ul class="menu_links">
                    <spring:message code="com.csc.integral.url.index.workflow" var="workflowUrl"/>
                    <li OnClick="if(document.getElementById('UIMaskWorkspace')) ajaxGet(eXo.env.server.createPortalURL('UIPortal', 'ChangeLanguage', true));">
                        <spring:message code="com.csc.integral.portlet.header.changelanguage"/>
                    </li>

                    <spring:message code="com.csc.integral.url.index.businessAnalytics" var="baUrl"/>
                    <li OnClick="location.href=/portal/private/Integral/Help">
                        <spring:message code="com.csc.integral.portlet.header.help"/>
                    </li>

                    <spring:message code="com.csc.integral.url.index.polisyAdmin" var="polisyUrl"/>
                    <li OnClick="window.print();">
                        <spring:message code="com.csc.integral.portlet.header.print"/>
                    </li>

                    <spring:message code="com.csc.integral.url.index.lifeAdmin" var="lifeUrl"/>
                    <li OnClick="location.href=/portal/logout">
                        <spring:message code="com.csc.integral.portlet.header.logout"/>
                    </li>
                </ul>
            </div>

        </div>

        <div class="rb_content_bottom" style="width: 110%;">
            <div class="rb_left"></div>
            <div class="rb_content_bottom_area2" style="WIDTH: 175px;"></div>
            <div class="rb_right"></div>
        </div>
    </div>

    <div class="rb_content firstColumn" style="float:bottom; margin-top:5px;">

        <div class="rb_content_top">
            <p>
                <spring:message code="com.csc.integral.portlet.index.quickLinks"/>
            </p>
        </div>

        <div class="rb_content_middle">

            <div class="menu">

                <script type="text/javascript" src="/csc-integral-web/js/common.js"></script>

                <ul class="menu_links">
                    <li><spring:message code="com.csc.integral.portlet.index.clientSearch"/>
                    </li>
                    <li>
                        <spring:message code="com.csc.integral.portlet.index.polisySearch"/>
                    </li>
                    <li>
                        <spring:message code="com.csc.integral.portlet.index.claimSearch"/>
                    </li>
                    <li OnClick="window.open('<spring:message code='com.csc.integral.url.index.clientRegisterReport' />');">
                        <spring:message code="com.csc.integral.portlet.index.clientRegisterReport"/>
                    </li>
                    <li OnClick="location.href=&#39;<c:out value='${SiteURL}'/>/UserManagement&#39;">
                        <spring:message code="com.csc.integral.portlet.index.userMaintenance"/>
                    </li>
                    <li OnClick="location.href=&#39;<c:out value='${SiteURL}'/>/UserImport&#39;">
                        <spring:message code="com.csc.integral.portlet.index.userImport"/>
                    </li>
                    <li OnClick="location.href=&#39;<c:out value='${SiteURL}'/>/UserReImport&#39;">
                        <spring:message code="com.csc.integral.portlet.index.userReImport"/>
                    </li>
                    <li OnClick="location.href=&#39;<c:out value='${SiteURL}'/>/UserRegister&#39;">
                        <spring:message code="com.csc.integral.portlet.index.userRegister"/>
                    </li>
                    <li OnClick="location.href=&#39;<c:out value='${SiteURL}'/>/GroupManagement&#39;">
                        <spring:message code="com.csc.integral.portlet.index.groupManagement"/>
                    </li>
                    <li>
                        <spring:message code="com.csc.integral.portlet.index.changePass"/>
                    </li>
                </ul>

            </div>

        </div>

        <div class="rb_content_bottom" style="width: 110%;">
            <div class="rb_left"></div>
            <div class="rb_content_bottom_area2" style="WIDTH: 175px;"></div>
            <div class="rb_right"></div>
        </div>
    </div>
</div>

<div class="rb_content">

<div class="rb_content_top">
    <p><spring:message code="com.csc.integral.portlet.header.DMS"/></p>
</div>


<div class="rb_content_middle">

    <div id="search_content">
        <div id="document_data_column">
            <fieldset>
                <legend>
                    <spring:message code="document.title.left"/>
                </legend>
                <table class="t">
                    <tr>
                        <td class="c1">
                            <div class="label">
                                <spring:message code="document.client"/>
                            </div>
                            <div>
                                <form:input path="searchCriteria.client" id="clientNum"/><img id="client_search_btn"
                                                                                              src="/eXoResources/skin/datepicker/images/ico_search_01.gif"/>
                            </div>
                        </td>
                        <td class="c2">
                            <div class="label">
                                <spring:message code="document.agent"/>
                            </div>
                            <div>
                                <form:input path="searchCriteria.agent"/>
                            </div>
                        </td>
                        <td class="c3">
                            <div class="label">
                                <spring:message code="document.policy"/>
                            </div>
                            <div>
                                <form:input path="searchCriteria.policy"/>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <td class="c1">
                            <div class="label">
                                <spring:message code="document.user"/>
                            </div>
                            <div>
                                <form:input path="searchCriteria.user"/>
                            </div>
                        </td>
                        <td class="c2">
                            <div class="label">
                                <spring:message code="document.claim"/>
                            </div>
                            <div>
                                <form:input path="searchCriteria.claim"/>
                            </div>
                        </td>
                        <td class="c3">
                            <div class="label">
                                <spring:message code="document.product"/>
                            </div>
                            <div>
                                <form:input path="searchCriteria.product"/>
                            </div>
                        </td>
                    </tr>
                </table>
            </fieldset>
        </div>
        <div id="file_data_column">
            <fieldset>
                <legend>
                    <spring:message code="document.title.right"/>
                </legend>
                <table class="t">
                    <tr>
                        <td class="c1">
                            <div class="label">
                                <spring:message code="document.fileName"/>
                            </div>
                            <div>
                                <form:input path="searchCriteria.title"/>
                            </div>
                        </td>
                        <td class="c2">
                            <div class="label">
                                <spring:message code="document.owner"/>
                            </div>
                            <div>
                                <form:input path="searchCriteria.creator"/>
                            </div>
                        </td>
                        <td class="c3">
                            <div class="label">
                                <spring:message code="document.createdDate"/>
                            </div>
                            <div>
                                <form:input path="searchCriteria.fromCreatedDate" id="fromCreatedDate"/>

                                <div class="error" style="color: red; display: none;"><spring:message
                                        code="search.datetime.validation.fail"/></div>
                            </div>
                        </td>
                    </tr>
                </table>
            </fieldset>
        </div>
        <div style="clear:both;"></div>
        <div class="sr-container">
            <portlet:actionURL var="searchActionURL" escapeXml="false"><portlet:param name="action"
                                                                                      value="search"/></portlet:actionURL>
            <table class="buttontable">
                <tbody>
                <tr>
                    <td>
                        <div class="sectionbutton mr10" style="">
                            <p><a id="searchBtn" href="#"
                                  onclick="javascript:submitPortlet('<c:out value="${searchActionURL}"></c:out>');
                                return false;"><spring:message
                                        code="search.button"/></a></p>
                        </div>
                    </td>
                </tr>
                </tbody>
            </table>

            <c:set var="searchResult" value="${sessionSearchResult}"/>

            <div class="sr">
                <c:if test="${not empty searchResult}">
                    <div class="sBase">
                        <table class="table">
                            <tr>
                                <td>
                                    <table class="sSky">
                                        <thead>
                                        <tr align="center">
                                            <th>
                                                <spring:message code="search.table.result.name"/>
                                            </th>
                                            <th>
                                                <spring:message code="search.table.result.client"/>
                                            </th>
                                            <th>
                                                <spring:message code="search.table.result.policy"/>
                                            </th>
                                            <th>
                                                <spring:message code="search.table.result.createdDate"/>
                                            </th>
                                        </tr>
                                        </thead>
                                        <tbody>
                                        <c:forEach var="item" items="${searchResult}" varStatus="status">
                                            <tr
                                            <c:if test="${status.index % 2 > 0}">class="even"</c:if>
                                            <c:if test="${status.index % 2 == 0}">class="odd"</c:if>
                                            >
                                            <td>
                                                <div>
                                                    <a class="tablelink" target="_blank"
                                                       href="/rest/jcr/repository/collaboration<c:out value='${item.path }' />"><c:out
                                                            value="${item.name}"/></a>
                                                </div>
                                            </td>
                                            <td>
                                                <div>
                                                    <c:out value="${item.client}"/>
                                                </div>
                                            </td>
                                            <td>
                                                <div>
                                                    <c:out value="${item.policy}"/>
                                                </div>
                                            </td>
                                            <td>
                                                <div>
                                                    <c:out value="${item.dateCreated}"/>
                                                </div>
                                            </td>
                                            </tr>
                                        </c:forEach>
                                        </tbody>
                                    </table>
                                </td>
                            </tr>
                            <tr>
                                <td align="right" style="padding: 4px;">
                                    <jsp:include page="/jsp/include/pageIterator.jsp">
                                        <jsp:param name="submitForm" value="document.searchForm"/>
                                        <jsp:param name="submitAction" value="paging"/>
                                    </jsp:include>
                                </td>
                            </tr>
                        </table>
                    </div>
                </c:if>
            </div>
        </div>
    </div>

</div>

<div class="rb_content_bottom">
    <div class="rb_left"></div>
    <div class="rb_content_bottom_area2"></div>
    <div class="rb_right"></div>
</div>
</div>
</div>

<div style="clear: both"/>
</div>
</form:form>
