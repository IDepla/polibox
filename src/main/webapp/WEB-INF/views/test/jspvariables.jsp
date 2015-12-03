
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<div>
	name="${requestScope._csrf.parameterName}"
	value="${requestScope._csrf.token}"
	<c:forEach var="aParam" items="${paramValues}">
 parametro=${aParam.key}
 valori associati:
 	<c:forEach var="aValue" items="${aParam.value}">
 		${aValue}
 	</c:forEach>
		<br />
	</c:forEach>
	<c:forEach items='${initParam}' var='parameter'>
		<ul>
			<%-- Display the key of the current item, which
                 corresponds to the name of the init param --%>
			<li>init Name: <c:out value='${parameter.key}' /></li>

			<%-- Display the value of the current item, which
                 corresponds to the value of the init param --%>
			<li>init Value: <c:out value='${parameter.value}' /></li>
		</ul>
	</c:forEach>
	<c:forEach items='${cookie}' var='mapEntry'>
		<ul>
			<%-- The mapEntry's key references the cookie name --%>
			<li>Cookie Name: <c:out value='${mapEntry.key}' /></li>

			<%-- The mapEntry's value references the Cookie
                 object, so we show the cookie's value --%>
			<li>Cookie Value: <c:out value='${mapEntry.value.value}' /></li>
		</ul>
	</c:forEach>
	<c:forEach var="aParam" items="${sessionScope}">
 session parametro=${aParam.key}
 valori associati:${aParam.value}
 	
 	<br />
	</c:forEach>
	<c:forEach var="aParam" items="${requestScope}">
 request parametro=${aParam.key}
 valori associati: ${aParam.value}
 	<br />
	</c:forEach>
	<c:forEach var="aParam" items="${responseScope}">
 response parametro=${aParam.key}
 valori associati:${aParam.value}
 
 	<br />
	</c:forEach>
	<c:forEach var="aParam" items="${pageScope}">
 page parametro=${aParam.key}
 valori associati:${aParam.value}
 	
 	<br />
	</c:forEach>
	<c:forEach items='${header}' var='h'>
		<ul>
			<%-- Display the key of the current item, which
                represents the request header name and the
                current item's value, which represents the
                header value --%>
			<li>Header Name: <c:out value='${h.key}' /></li>
			<li>Header Value: <c:out value='${h.value}' /></li>
		</ul>
	</c:forEach>
</div>