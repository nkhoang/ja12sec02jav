<%@ include file="/common/taglibs.jsp" %>

<html >
<head ><title >Test using Map in JSP</title >
    <style >
        body {
            line-height: 2em;
        }
    </style >
    <script type="text/javascript" >
        var mappingList = new Object();

        // update listener based on observer selected value.
        function updateListener(observer, listenerId) {
            if (mappingList[listenerId]) {
                var selectedValue = observer.options[observer.selectedIndex].value;
                // listener possible options.
                var listenerOptions = mappingList[listenerId][selectedValue];
                // get the '<select>' listener.
                if (listenerOptions) {
                    // get the listener element.
                    var listener = $(listenerId);
                    buildSelectOptions(listener, listenerOptions);
                }
            }
        };

        function buildSelectOptions(listener, options) {
            // remove listener childs.
            var firstChild = listener.firstChild;
            while (firstChild) {
                listener.removeChild(firstChild);
                firstChild = listener.firstChild;
            }
            // add options
            // add default option
            listener.appendChild(Builder.node('option'));
            for (var key in options) {
                listener.appendChild(Builder.node('option', {value : key}, options[key] ))
            }
        }
    </script >
</head >
<body >

    Option 1:
    <br >
    <!-- Create support list -->
    <script type="text/javascript" >
        var dataMap = {
            <c:forEach var="item" items="${facilityMap}" varStatus="loop">
                '${item.key}' :  {
<c:forEach var="subitem" items="${item.value}" varStatus="subloop">
                    '${subitem.key}' : '${subitem.value}' ${not subloop.last ? ',' : ''}
                </c:forEach>
            } ${not loop.last ? ',' : ''}
            </c:forEach>
        };

        var listName = '${optionId}';
        mappingList[listName] = dataMap;
    </script >

    <c:if test="${fn:length(facilityList) > 0}" >
        <select id="facilityList" onchange="updateListener(this, '${optionId}');" >
            <option value="" ></option >
            <c:forEach var="item" items="${facilityList}" >
                <option value="${item.key}" >${item.value}</option >
            </c:forEach >
        </select >
    </c:if >
    <br >
    Option 2:
    <br >
    <select id="${optionId}">
        <option value=""></option>
    </select>

</div >


FacilityMapping 1
<br >

<c:forEach var="item" items="${facilityMapping}" >
    Item: key[${item.key}] - size[${fn:length(item.value)}]

    <c:choose >
        <c:when test="${fn:length(item.value) > 0}" >
            <ul >
                <c:forEach var="subitem" items="${item.value}" >
                    <li >${subitem}</li >
                </c:forEach >
            </ul >

        </c:when >
        <c:otherwise >
            <ul >
                <li >Empty List</li >
            </ul >
        </c:otherwise >
    </c:choose >
    <br />
</c:forEach >


<!-- Facility Mapping 2 is empty-->

Facility Mapping 2
<br >

<c:if test="${fn:length(facilityMapping2) == 0}" >
    Empty map.
</c:if >
<c:forEach var="item" items="${facilityMapping2}" >
    Item: key[${item.key}] - size[${fn:length(item.value)}

    <c:choose >
        <c:when test="${fn:length(item.value) > 0}" >
            <ul >
                <c:forEach var="subitem" items="${item.value}" >
                    <li >${subitem}</li >
                </c:forEach >
            </ul >

        </c:when >
        <c:otherwise >Empty List</c:otherwise >
    </c:choose >
    <br />
</c:forEach >

</body >
</html >