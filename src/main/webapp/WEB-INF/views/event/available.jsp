<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%--
  Created by IntelliJ IDEA.
  User: anyat
  Date: 21.04.2018
  Time: 21:38
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Events</title>
    <link href="${contextPath}/resources/bootstrap3/css/bootstrap.min.css" rel="stylesheet">
    <!-- Custom fonts for this template-->
    <link href="${contextPath}/resources/css/style.css" rel="stylesheet">
    <!-- Custom styles for this template-->
</head>
<body>
<div class="row">
    <jsp:include page="${contextPath}/WEB-INF/views/account/navbar/navbar.jsp"/>
    <div class="col-md-3"
    <jsp:include page="${contextPath}/WEB-INF/views/account/menu/menu.jsp"/>
</div>


<div class="col-md-9 content">
    <p>
        <a class="btn btn-primary" data-toggle="collapse" href="/account/available" role="button">All events</a>
        <a class="btn btn-primary" data-toggle="collapse" href="/account/subscriptions" role="button">Subscriptions</a>
        <a class="btn btn-primary" data-toggle="collapse" href="/account/managed" role="button">Managed events</a>
        <a class="btn btn-primary" data-toggle="collapse" href="/account/draft" role="button">Drafts</a>
    </p>
    <h3>Search for events</h3>
    <form method="POST"
          class="forms_form" action="">

        <div class="form-group">
            <input name="search" class="form-control" style="width: 33%" id="search"  placeholder="Enter name or surname"/>
            <input type="submit" value="Search" class="btn btn-dark" href="/" style="margin-top: 15px; margin-bottom: 15px">
        </div>
    </form>
    <h3>Events feed</h3>
   <div class="row">
       <table class="table">
           <c:forEach var="event" items="${publicEventList}">
               <tbody>
                    <tr>
                        <td><img class="img-circle" style="width: 200px;height: 200px"
                             src="<c:url value="/account/image/${event.photo}.jpg"/>"> </td>
                        <td> <a href="/account/eventList/event-${event.eventId}"> ${event.name} </a></td>
                        <td> Date : ${event.dateStart} - ${event.dateEnd} </td>
                    </tr>
               </tbody>
           </c:forEach>

       </table>
   </div>

    <div class="row">
        <table class="table">
            <c:forEach var="friends" items="${friendsEventList}">
                <tbody>
                <tr>
                    <td><img class="img-circle" style="width: 200px;height: 200px"
                             src="<c:url value="/account/image/${friends.photo}.jpg"/>"> </td>
                    <td> <a href="/account/eventList/event-${friends.eventId}"> ${friends.name} </a></td>
                    <td> Date : ${friends.dateStart} - ${friends.dateEnd} </td>
                </tr>
                </tbody>
            </c:forEach>

        </table>
    </div>
</div>
<script src="${contextPath}/resources/bootstrap3/js/bootstrap.min.js"></script>
<script src="${contextPath}/resources/bootstrap3/js/bootstrap.js"></script>
<script src="${contextPath}/resources/vendor/bootstrap/js/jquery-1.11.1.min.js"></script>

</body>
</html>