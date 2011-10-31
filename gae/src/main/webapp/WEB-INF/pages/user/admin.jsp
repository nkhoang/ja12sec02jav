<%@ include file="/common/taglibs.jsp" %>
<c:set var="isUser" value="false"/>
<security:authorize access="hasRole('ROLE_USER')">
   <c:set var="isUser" value="true"/>
   <c:set var="userName">
      <security:authentication property="principal.username"/>
   </c:set>
</security:authorize>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN">
<html>
<head>
<title><fmt:message key="webapp.title"/>
</title>
<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js"></script>
<script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1/jquery-ui.js" type="text/javascript"></script>
<script src="http://cdn.kendostatic.com/2011.3.1007/js/kendo.all.min.js"></script>

<link href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/themes/redmond/jquery-ui.css" rel="stylesheet"
      type="text/css">

<link href="http://cdn.kendostatic.com/2011.3.1007/styles/kendo.common.min.css" rel="stylesheet"/>
<link href="http://cdn.kendostatic.com/2011.3.1007/styles/kendo.blueopal.min.css" rel="stylesheet"/>

<c:if test="${isUser}">
   <style type="text/css">
      .grid-container {
         width: 400px;
         font: 75% arial, helvetica, sans-serif;
         font-family: Arial, Helvetica, sans-serif;
      }

      .handler {
         cursor: pointer;
      }

      .grid-container table th {
         background-color: #EF652A;
         border-color: #D75B26 #D75B26 #D75B26 #F28455;
         border-style: solid;
         border-width: 1px;
         color: #FFFFFF;
         line-height: 14px;
         padding: 9px 6px;
         text-align: left;
      }
   </style>
   <script type="text/javascript">
      var $container;// global dict grid.
      var dictGrid, dictDataSource, appConfigDataSource, appConfigGrid;
      $(function() {
         $('#tabstrip').kendoTabStrip();
         renderDictGrid();
         rederAppConfig();
      });

      function refreshDictGrid() {
         dictDataSource.read();
         dictGrid.refresh();
      }

      function refreshAppConfigGrid() {
         appConfigDataSource.read();
         appConfigGrid.refresh();
      }

      function deleteAppConfig(element, id, label) {
         $.ajax({
            url : '<c:url value="/user/deleteAppConfig.html" />',
            data : {
               'id' : id
            },
            type : 'GET',
            dataType : 'json',
            beforeSend : function() {
               $(element).data('loading', true);
            },
            success : function(response) {
               if (response.error) {
                  showMessage({
                     title : 'AppConfig',
                     text : response.error
                  });
               } else {
                  showMessage({
                     title : 'AppConfig',
                     text : 'Remove AppConfig: "' + label + '" successfully.'
                  });

               }
               refreshAppConfigGrid();
            },
            error : function() {
            },
            complete : function() {
               $(element).data('loading', false);
            }
         })
      }

      function deleteDict(element, dictId, dictName) {
         $.ajax({
            url : '<c:url value="/user/deleteDict.html" />',
            data : {
               'dictId' : dictId
            },
            type : 'GET',
            dataType : 'json',
            beforeSend : function() {
               $(element).data('loading', true);
            },
            success : function(response) {
               if (response.data) {
                  showMessage({
                     title : 'Dictionary',
                     text : 'Remove dictionary: "' + dictName + '" successfully.'
                  });
               } else {
                  showMessage({
                     title : 'Dictionary',
                     text : 'Failed to remove Dictionary: "' + dictName + '".'
                  });
               }
               refreshDictGrid();
            },
            error : function() {
            },
            complete : function() {
               $(element).data('loading', false);
            }
         });
      }

      function addNewDictionary(element) {
         if ($(element).data('loading')) {
            showMessage({
               title : 'Dictionary',
               text : 'Working... please wait!!!'
            });
            return false;
         }
         $.ajax({
            url : '<c:url value="/user/addDictionary.html" />',
            data : {
               'dictName' : $('#dictionary-name').val(),
               'dictDescription' : $('#dictionary-des').val()
            },
            type : 'POST',
            dataType : 'json',
            beforeSend : function() {
               $(element).data('loading', true);
            },
            success : function(response) {
               $('#dictionary-name').val('');
               $('#dictionary-des').val('');
               if (response.data) {
                  if (response.data.error) {
                     showMessage({
                        title : 'Dictionary',
                        text : response.data.error
                     });
                  } else {
                     showMessage({
                        title : 'Dictionary',
                        text : 'Added successfully!'
                     });
                     refreshDictGrid();
                  }
               }
            },
            error : function() {
            },
            complete : function() {
               $(element).data('loading', false);
            }
         });
      }

      function addNewAppConfig(element) {
         if ($(element).data('loading')) {
            showMessage({
               title : 'AppConfig',
               text : 'Working... please wait!!!'
            });
            return false;
         }
         $.ajax({
            url : '<c:url value="/user/saveAppConfig.html" />',
            data : {
               'label' : $('#property-name').val(),
               'value' : $('#property-values').val()
            },
            type : 'GET',
            dataType : 'json',
            beforeSend : function() {
               $(element).data('loading', true);
            },
            success : function(response) {
               $('#property-name').val('');
               if (response.error) {
                  showMessage({
                     title : 'AppConfig',
                     text : response.error
                  });
               } else {
                  showMessage({
                     title : 'AppConfig',
                     text : 'Added successfully!'
                  });
                  refreshAppConfigGrid();
               }

            },
            error : function() {
            },
            complete : function() {
               $(element).data('loading', false);
            }
         });
      }
   </script>
</c:if>
</head>
<body>
<c:choose>
   <c:when test="${isUser}">
      Welcome to admin page.
      <div id="tabstrip">
         <ul>
            <li class="k-state-active">
               Dictionary
            </li>
            <li>
               Application
            </li>
         </ul>
         <div>
            <div class="panel">
               <div>
                  <div>Register a new dictionary service:</div>
                  <table>
                     <tbody>
                     <tr>
                        <td><label><b>Name</b></label> <input id="dictionary-name" name="dictionary-name"
                                                              class="k-input"/></td>
                        <td><label><b>Description</b></label> <input id="dictionary-des" name="dictionary-des"
                                                                     class="k-input"/>
                        </td>
                     </tr>
                     <tr>
                        <td colspan="2"><input type="button" value="Submit" name="Submit"
                                               onclick="addNewDictionary(this);"/>
                        </td>
                     </tr>
                     </tbody>
                  </table>
                  <div>Dictionary:</div>
                  <div class="grid-container">
                     <script type="text/javascript">
                        function renderDictGrid() {
                           dictDataSource = new kendo.data.DataSource({
                              transport : {
                                 read : {
                                    url : '<c:url value="/user/getAllDicts.html" />',
                                    dataType : 'json'
                                 }
                              },
                              serverSorting : false,
                              pageSize : 10,
                              sort : {
                                 field : "name",
                                 dir : "asc"
                              },
                              schema : {
                                 data : function(response) {
                                    return response.data;
                                 }
                              }
                           });

                           $("#dictGrid").kendoGrid({
                              dataSource : dictDataSource,
                              rowTemplate : kendo.template($("#dictTemplate").html()),
                              height : 250,
                              scrollable : {
                                 // enable vertical scrolling.
                                 virtual : false
                              },
                              selectable: true,
                              pageable : true
                           });
                           dictGrid = $("#dictGrid").data("kendoGrid");
                        }
                     </script>
                     <%@ include file="/common/dictionary-template.jsp" %>
                     <table id="dictGrid">
                        <thead>
                        <tr>
                           <th width="22px" style="text-align: center"> X</th>
                           <th width="92px">Name</th>
                           <th>Description</th>
                        </tr>
                        </thead>
                        <tbody>
                        <tr>
                           <td colspan="2"></td>
                        </tr>
                        </tbody>
                     </table>
                  </div>
               </div>
            </div>
         </div>
         <div>
            <div>Register a new dictionary service:</div>

            <script type="text/javascript">
               function rederAppConfig() {
                  appConfigDataSource = new kendo.data.DataSource({
                     transport : {
                        read : {
                           url : '<c:url value="/user/getAppConfig.html" />',
                           dataType : 'json'
                        }
                     },
                     serverSorting : false,
                     pageSize : 10,
                     sort : {
                        field : "name",
                        dir : "asc"
                     },
                     schema : {
                        data : function(response) {
                           return response.data;
                        }
                     }
                  });

                  $("#appConfigGrid").kendoGrid({
                     dataSource : appConfigDataSource,
                     rowTemplate : kendo.template($("#appconfigTemplate").html()),
                     height : 250,
                     scrollable : {
                        // enable vertical scrolling.
                        virtual : false
                     },
                     selectable: true,
                     pageable : true
                  });
                  appConfigGrid = $("#appConfigGrid").data("kendoGrid");
               }

               function renderDictionaryBox() {
                  $("#property-values").kendoComboBox({
                     index: 0,
                     dataTextField: "name",
                     dataValueField: "name",
                     dataSource: dictDataSource
                  });
               }

               $(function() {
                  renderDictionaryBox();
               });
            </script>
            <table>
               <tbody>
               <tr>
                  <td><label><b>Label</b></label> <input id="property-name" name="property-name"
                                                         class="k-input"/></td>
                  <td><label><b>Value</b></label> <input id="property-values"/>
                  </td>
               </tr>
               <tr>
                  <td colspan="2"><input type="button" value="Submit" name="Submit"
                                         onclick="addNewAppConfig(this);"/>
                  </td>
               </tr>
               </tbody>
            </table>
            <%@ include file="/common/appconfig-template.jsp" %>
            AppConfig properties:
            <div class="grid-container">
               <table id="appConfigGrid">
                  <thead>
                  <tr>
                     <th width="22px" style="text-align: center"> X</th>
                     <th width="92px">Label</th>
                     <th>Values</th>
                  </tr>
                  </thead>
                  <tbody>
                  <tr>
                     <td colspan="2"></td>
                  </tr>
                  </tbody>
               </table>
            </div>
         </div>
      </div>
      <%@ include file="/common/notify-template.jsp" %>
   </c:when>
   <c:otherwise>
      Are you misschara user ? If yes,you can <a href="#" onclick="openLoginDialog();"> here </a>.
   </c:otherwise>
</c:choose>
</body>
</html>