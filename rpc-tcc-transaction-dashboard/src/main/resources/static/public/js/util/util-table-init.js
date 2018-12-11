(function ($) {
    'use strict';
    $.fn.bootstrapTable.options = {
        method :"post", //所有分页请求都是post
        cache: false, //设置为 false 禁用 AJAX 数据缓存， 默认为true
        striped: true, //表格显示条纹，默认为false
        pagination: true, // 在表格底部显示分页组件，默认false
        paginationLoop:false, //分页条无限循环的功能
        pageList: [ 10, 20, 30, 50], // 设置页面可以显示的数据条数
        pageSize: 10, // 页面数据条数
        pageNumber: 1, // 首页页码
        sidePagination: 'server', // 设置为服务器端分页
        "queryParamsType": "limit",//设置为 'limit' 则会发送符合 RESTFul 格式的参数
//        queryParamsType:'undefined',
        contentType: "application/x-www-form-urlencoded",
        queryParams: function (params) { // 请求服务器数据时发送的参数，可以在这里添加额外的查询参数，返回false则终止请求
            var paramOption = {
                // pageSize: params.limit, // 每页要显示的数据条数
                // pageNumber: (params.offset/params.limit)+1, // 每页显示数据的开始行号
                // sortName: params.sort, // 要排序的字段
                // searchText:'',//搜索内容
                // sortOrder: params.order // 排序规则
            }
            return paramOption;
        },
        onLoadSuccess: function () {  //加载成功时执行
            console.info("加载成功");
        },
        onLoadError: function () {  //加载失败时执行
            console.info("加载数据失败");
        }

    };
    $.extend($.fn.bootstrapTable.defaults, $.fn.bootstrapTable.options);
})(jQuery);
