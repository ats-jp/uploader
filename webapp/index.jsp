<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<%@ page contentType="text/html;charset=UTF-8"%>
<%@ page language="java"%>
<%@ page import="jp.ats.uploader.*" %>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="ja" lang="ja">
<head>
<meta http-equiv="content-type" content="text/html;charset=UTF-8" />
<style>
th, td {
	border: 1px solid #999;
	padding: 5px;
	white-space: nowrap;
}
</style>
</head>
<body>
<form method="post" action="<%=request.getContextPath()%>/upload" enctype="multipart/form-data">

<table>
<tbody>
<tr>
<th>ファイル</th>
<td>
<input name="file" type="file" />
</td>
</tr>
<tr>
<th>説明</th>
<td>
<input type="text" name="description" style="width: 200px;" />
</td>
</tr>
</tbody>
</table>
<input type="submit" />
</form>

<br />
<br />

<table>
<tbody>
<tr>
<th>登録時刻</th>
<th>No.</th>
<th>ファイル</th>
<th>byte</th>
<th>説明</th>
<th>登録者</th>
<th></th>
</tr>
<%
for (Record record : UploadManager.read()) {
%>
<tr>
<td><%=record.getTimestamp()%></td>
<td style="text-align: right;"><%=record.id%></td>
<td>
<%
	if (record.exists()) {
%>
<a href="download?id=<%=record.id%>"><%=record.fileName%></a>
<%
	} else {
%>
<%=record.fileName%>
<%
	}
%>
<br />
<span style="color: gray;"><%=record.file.getFileName()%></span>
</td>
<td style="text-align: right;"><%=record.getFileSize()%></td>
<td><%=record.description%></td>
<td><%=record.owner%></td>
<td><input type="button" onclick="location.href='delete?id=<%=record.id%>';" value="削除" /></td>
</tr>
<%
}
%>
</tbody>
</table>
</body>
</html>
