<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <link href="${contextPath}/resources/bootstrap3/css/bootstrap.min.css" rel="stylesheet">
    <link href="${contextPath}/resources/css/style.css" rel="stylesheet">
</head>
<body>

<div class="row">
    <jsp:include page="${contextPath}/WEB-INF/views/account/navbar/navbar.jsp"/>
    <div class="col-md-3"
    <jsp:include page="${contextPath}/WEB-INF/views/account/menu/menu.jsp"/>
</div>

<div class="col-md-6 content">
    <div class="col-md-6">
        <a class="btn btn-primary" href="<c:url value='/account/user-${auth_user.id}/wishList'/>"> < Back</a>
        <div class="panel panel-success">
            <div class="panel-heading">
                <h3 align="center" class="panel-title"> ${getItem.name}</h3>
            </div>
            <div class="panel-body viewItem">
                <ul class="list-unstyled mt-3 mb-4">

                    <li><b>Item Name:</b> ${getItem.name}</li>
                    <li><b>Booker:</b> ${getItem.booker}</li>
                    <li><b>Description:</b> ${getItem.description}</li>
                    <li><b>Link:</b> ${getItem.link}</li>
                    <li><b>Date:</b> ${getItem.dueDate}</li>
                    <li><b>Priority:</b> ${getItem.priority}</li>
                    <li><b>Root:</b> ${getItem.root}</li>
                    <c:choose>
                        <c:when  test="${auth_user.id.equals(getItem.personId)}">
                            <li>
                                <a class="btn btn-success" type="submit" data-toggle="collapse"
                                 href="/account/update-${getItem.itemId}" role="button">Edit</a>
                                <a href="/account/wishList/deleteItem-${getItem.itemId}">
                                    <input type="submit" class="btn btn-danger text-center" value="Delete"></a>
                            </li>
                        </c:when>
                        <c:otherwise>
                            <a href="/account/copy-${getItem.itemId}">
                                <input type="submit" class="btn btn-success text-center" value="Copy to my wish list"></a>
                        </c:otherwise>
                    </c:choose>
                </ul>
            </div>
        </div>
    </div>
    <div class="container-fluid">
    </div>
</div>

</body>

</html>