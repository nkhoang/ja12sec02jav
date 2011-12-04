<%@ include file="/common/taglibs.jsp" %>
<html>
<head>
    <title><fmt:message key="webapp.title"/></title>
    <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.6.1/jquery.min.js"></script>
    <script src="http://ajax.googleapis.com/ajax/libs/jqueryui/1/jquery-ui.js" type="text/javascript"></script>

    <script type="text/javascript" src="<c:url value='/js/ext-all.js' />"></script>
    <script type="text/javascript" src="<c:url value='/js/accounting.js' />"></script>
    <link href="<c:url value='/resources/css/ext-all-gray.css'/>" rel="stylesheet" media="all"/>
    <link rel="stylesheet" type="text/css" media="all" href="<c:url value='/styles/phonecard-layout.css' />"/>
    <style type="text/css">
        #discount-grid-container {
            width: 600px;
            margin: 0 auto;
        }
    </style>
    <script type="text/javascript">
        var phonecardStore;// define model for Dictionary
        $(function () {
            Ext.define('PhoneCardDiscount', {
                extend:'Ext.data.Model',
                fields:[
                    {
                        name:'type',
                        mapping:'type'
                    },
                    {
                        name:'price',
                        mapping:'price'
                    },
                    {
                        name:'discountType1',
                        mapping:'buyDiscountRates["1"]'
                    },
                    {
                        name:'discountType5',
                        mapping:'buyDiscountRates["5"]'
                    },
                    {
                        name:'discountType10',
                        mapping:'buyDiscountRates["10"]'
                    },
                    {
                        name:'discountType20',
                        mapping:'buyDiscountRates["20"]'
                    }
                ]
            });

            discountStore = Ext.create('Ext.data.Store', {
                model:'PhoneCardDiscount',
                proxy:{
                    type:'ajax',
                    url:'<c:url value="/phonecard/getDiscountData.html" />',
                    reader:{
                        type:'json',
                        root:'data'
                    }
                },
                autoLoad:true,
                autoSync:false,
                listeners:{
                }
            });

            var grid = Ext.create('Ext.grid.Panel', {
                id:'discount-grid',
                store:discountStore,
                enableColumnHide:true,
                columns:[
                    Ext.create('Ext.grid.RowNumberer'),
                    {
                        header:'<fmt:message key="phonecard.grid.col.type" />',
                        dataIndex:'type',
                        flex:1
                    },
                    {
                        header:'<fmt:message key="phonecard.grid.col.price" />',
                        dataIndex:'price',
                        flex:1,
                        renderer:function (value) {
                            return accounting.formatNumber(value);
                        }
                    },
                    {
                        header:'<fmt:message key="phonecard.grid.col.1mil" />',
                        dataIndex:'discountType1',
                        flex:1,
                        hidden:<c:choose><c:when test="${discountShowLimit >= 1}">false</c:when><c:otherwise>true</c:otherwise></c:choose>
                    },
                    {
                        header:'<fmt:message key="phonecard.grid.col.5mil" />',
                        dataIndex:'discountType5',
                        flex:1,
                        hidden:<c:choose><c:when test="${discountShowLimit >= 5}">false</c:when><c:otherwise>true</c:otherwise></c:choose>
                    },
                    {
                        header:'<fmt:message key="phonecard.grid.col.10mil" />',
                        dataIndex:'discountType10',
                        flex:1,
                        hidden:<c:choose><c:when test="${discountShowLimit >= 10}">false</c:when><c:otherwise>true</c:otherwise></c:choose>
                    },
                    {
                        header:'<fmt:message key="phonecard.grid.col.20mil" />',
                        dataIndex:'discountType20',
                        flex:1,
                        hidden:<c:choose><c:when test="${discountShowLimit >= 20}">false</c:when><c:otherwise>true</c:otherwise></c:choose>
                    }
                ],
                renderTo:'discount-grid-container',
                width:700,
                height:670,
                title:'<fmt:message key="phonecard.grid.title" />',
                frame:true,
                listeners:{
                    'selectionchange':function (view, records) {
                    }
                }
            });
        });
    </script>
</head>
<body>
<div id="content">
    <jsp:include page="header.jsp"/>
    <div class="main">

        <div id="discount-grid-container"></div>
    </div>
    <jsp:include page="footer.jsp"/>
</div>
</body>
</html>