<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
    String path = request.getContextPath();
    String basePath = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + path + "/";
%>
<!DOCTYPE HTML>
<html>
<head>
    <base href="<%=basePath%>">
    <title>登录</title>
    <!-- 引入bootstrap样式 -->
    <%--<link href="https://cdn.bootcss.com/bootstrap/3.3.6/css/bootstrap.min.css" rel="stylesheet">--%>
    <!-- 引入bootstrap-table样式 -->
    <%--<link href="https://cdn.bootcss.com/bootstrap-table/1.11.1/bootstrap-table.min.css" rel="stylesheet">--%>

    <!-- jquery -->
    <%--<script src="https://cdn.bootcss.com/jquery/2.2.3/jquery.min.js"></script>--%>
    <%--<script src="https://cdn.bootcss.com/bootstrap/3.3.6/js/bootstrap.min.js"></script>--%>

    <!-- bootstrap-table.min.js -->
    <%--<script src="https://cdn.bootcss.com/bootstrap-table/1.11.1/bootstrap-table.min.js"></script>--%>
    <!-- 引入中文语言包 -->
    <%--<script src="https://cdn.bootcss.com/bootstrap-table/1.11.1/locale/bootstrap-table-zh-CN.min.js"></script>--%>
</head>
<body>
<div class="page-return">
    <table id="table"></table>
</div>
<!-- page body end-->
</body>
<script>
    $("#table").bootstrapTable({ // 对应table标签的id
        url: "userWhiteForFAM/listData", // 获取表格数据的url
        method :"post",
        cache: false, // 设置为 false 禁用 AJAX 数据缓存， 默认为true
        striped: true,  //表格显示条纹，默认为false
        pagination: true, // 在表格底部显示分页组件，默认false
        paginationLoop:false,
        pageList: [5 , 10, 20], // 设置页面可以显示的数据条数
        pageSize: 5, // 页面数据条数
        pageNumber: 1, // 首页页码
        sidePagination: 'server', // 设置为服务器端分页
        "queryParamsType": "limit",
//        queryParamsType:'undefined',
        contentType: "application/x-www-form-urlencoded",
//        queryParams:{pageSize: 5},
        queryParams: function (params) { // 请求服务器数据时发送的参数，可以在这里添加额外的查询参数，返回false则终止请求
            console.log(params);
            var param = {
                //1、当前页面
                //2、每页条数
                //3、查询条件
                pageSize: params.limit, // 每页要显示的数据条数
                pageNumber: (params.offset/params.limit)+1, // 每页显示数据的开始行号
                sortName: params.sort, // 要排序的字段
                searchText:'',
                sortOrder: params.order // 排序规则
//                dataId: $("#dataId").val() // 额外添加的参数
            }
            return param;
        },
//        sortName: 'id', // 要排序的字段
//        sortOrder: 'desc', // 排序规则
        columns: [
            {
                checkbox: true, // 显示一个勾选框
                align: 'center' // 居中显示
            }, {
                field: 'id', // 返回json数据中的name
                title: 'id', // 表格表头显示文字
                align: 'center', // 左右居中
                valign: 'middle' // 上下居中
            },{
                field: 'phoneNum', // 返回json数据中的name
                title: '手机号', // 表格表头显示文字
                align: 'center', // 左右居中
                valign: 'middle' // 上下居中
            }, {
                field: 'userId',
                title: '用户ID',
                align: 'center',
                valign: 'middle'
            }, {
                title: "操作",
                align: 'center',
                valign: 'middle',
                width: 160, // 定义列的宽度，单位为像素px
                formatter: function (value, row, index) {
                    return '<button class="btn btn-primary btn-sm" onclick="del(\'' + row.stdId + '\')">删除</button>';
                }
            }
        ],
        onLoadSuccess: function () {  //加载成功时执行
            console.info("加载成功");
        },
        onLoadError: function () {  //加载失败时执行
            console.info("加载数据失败");
        }

    })
</script>
</html>