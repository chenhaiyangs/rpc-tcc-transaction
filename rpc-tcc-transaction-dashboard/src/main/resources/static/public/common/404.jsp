<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">
<title>工作流管理平台</title>
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="cache-control" content="no-cache">
<meta http-equiv="expires" content="0">
<meta http-equiv="description" content="主页">

<link rel="stylesheet" type="text/css" href="public/easyui/themes/default/easyui.css">
<link rel="stylesheet" type="text/css" href="public/easyui/themes/icon.css">
<link rel="stylesheet" type="text/css" href="public/easyui/themes/demo.css">
<script type="text/javascript" src="public/easyui/jquery.min.js"></script>
<script type="text/javascript" src="public/easyui/jquery.easyui.min.js"></script>
<script type="text/javascript" src="globle/demo.js"></script>
</head>
<div align="center" style="top: 30%;">
	<h1 align="center">404</h1>
	<h3>the page is not found!</h3>
</div>
</html>