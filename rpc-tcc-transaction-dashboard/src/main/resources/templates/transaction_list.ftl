[#ftl]
<!--suppress ALL -->
<!DOCTYPE html>
<html>
<head>
    <style type="text/css">
        .table{
            table-layout: fixed;
        }
    </style>
</head>
<body>
<div class="page-return">
    <div class="ops-form">
        <div class="page-head">
            <form class="form-horizontal tasi-form" method="post" id="params" action="">
                <div class="form-group">
                    <label class="col-sm-1 control-label">业务标识</label>
                    <div class="col-sm-2">
                        <input class="form-control" name="domain" id="domain" value="">
                    </div>
                    <label class="col-sm-1 control-label">重试次数</label>
                    <div class="col-sm-2">
                        <input class="form-control" name="retries" id="retries" value="">
                    </div>
                    <div class="col-sm-3">
                        <button type="button" class="btn btn-info" onclick="" id="modelSearch">搜索</button>
                    </div>
                </div>
            </form>
        </div>
    </div>
    <div class="ops-table" >
        <caption>
            <div class="ops-edit">
                <button type="button" class="btn btn-xs btn-danger" id="modelDeleteBtn"><i class="ace-icon fa fa-trash-o bigger-110" title="删除"></i></button>
            </div>
        </caption>
        <div class="ops-table-content">
            <table id="modelTable"></table>
        </div>
    </div>
</div>
</body>
<script>
    $(function() {
        var modelTable = $("#modelTable").bootstrapTable({ // 对应table标签的id
            url: "/transaction/list", // 获取表格数据的url
            queryParams: function (params) { // 请求服务器数据时发送的参数，可以在这里添加额外的查询参数，返回false则终止请求
                var paramOption = {
                    pageSize: params.limit, // 每页要显示的数据条数
                    pageNumber: (params.offset / params.limit) + 1, // 每页显示数据的开始行号
                    sortName: params.sort, // 要排序的字段
                    searchText: '',//搜索内容
                    sortOrder: params.order, // 排序规则
                    dataId: $("#dataId").val() // 额外添加的参数
                }
                return paramOption;
            },
            columns: [
                {
                    checkbox: true // 显示一个勾选框
                },
                {
                    field: 'transaction.domain',
                    title: '业务标识',
                    align: 'center',
                    width:110,
                    valign: 'middle',
                    halign:'center'
                },
                {
                    field: 'transaction.status',
                    title: '事务状态',
                    align: 'center',
                    width:110,
                    valign: 'middle',
                    halign: 'center'
                },
                {
                    field: 'transaction.retriesCount',
                    title: '重试次数',
                    align: 'center',
                    width:110,
                    valign: 'middle',
                    halign: 'center'
                },
                {
                    field: 'transaction.createTime',
                    title: '创建时间',
                    align: 'center',
                    valign: 'middle',
                    halign: 'center',
                    formatter: function (value, row, index) {
                        return new Date(value).format("yyyy-MM-dd hh:mm:ss");
                    }
                },
                {
                    field: 'transaction.lastUpdateTime',
                    title: '最后更新时间',
                    align: 'center',
                    valign: 'middle',
                    halign: 'center',
                    formatter: function (value, row, index) {
                        return new Date(value).format("yyyy-MM-dd hh:mm:ss");
                    }
                },
                {
                    field: 'transaction.nextProcessTime',
                    title: '下次处理时间',
                    align: 'center',
                    valign: 'middle',
                    halign: 'center',
                    formatter: function (value, row, index) {
                        return new Date(value).format("yyyy-MM-dd hh:mm:ss");
                    }
                },
                {
                    field: 'transaction.version',
                    title: '版本',
                    width:75,
                    align: 'center',
                    valign: 'middle',
                    halign: 'center',
                    formatter: function (value, row, index) {
                        return "v" + value;
                    }
                },
                {
                    title: "操作",
                    width: 100,
                    formatter: function (value, row, index) {
                        return '<button class="btn btn-xs btn-info" onclick="detail(\'' + row.domain + '\',\''+row.globalTransactionId+'\',\''+row.branchQualifier+'\')">详情</button> &nbsp;'
                                + '<button class="btn btn-xs btn-danger" onclick="del(\'' + row.domain + '\',\''+row.globalTransactionId+'\',\''+row.branchQualifier+'\')"><i class="ace-icon fa fa-trash-o bigger-110" title="删除"></i></button>   ';
                    }
                }
            ]
        });
    });

    /**
     * 查询事务详情
     */
    function detail(domain,globalId,branchId){
        layer.open({
            type: 2,
            title: '事务详情',
            shadeClose: true,
            shade: false,
            area: ['800px', '480px'],
            content: '/transaction/detail?domain='+domain+"&globalTransactionId="+globalId+"&branchQualifier="+branchId
        });
    }

    /**
     * 删除
     */
    function del(domain,globalId,branchId){
        layer.confirm('你确定要删除', {
            btn: ['确定','取消'] //按钮
        }, function(){
            var r = {};
            r.domain=domain;
            r.globalTransactionId=globalId;
            r.branchQualifier=branchId;
            var json = [];
            json.push(r)
            $.ajax({
                type:"post",
                url:'/transaction/delete',
                dataType:"json",
                data:JSON.stringify(json),
                contentType : 'application/json;charset=UTF-8',
                success:function(data){
                    if(data.resCode == '200'){
                        parent.$("#modelTable").bootstrapTable('refresh',{silent: true});
                        parent.layer.msg(data.resMessage,{time:1200});
                        return true;
                    }else {
                        parent.layer.msg(data.resMessage);
                        return false;
                    }
                },
                error:function(data){
                    parent.layer.msg("错误："+data.resMessage);
                }
            })
        }, function(){
            layer.msg('取消操作', {
                time: 1500 //1.5s后自动关闭
            });
        });
    }

    /**
     * 批量删除
     */
    $("#modelDeleteBtn").click(function () {
        var objArray = $("#modelTable").bootstrapTable('getSelections');
        if(objArray.length < 1){
            layer.msg('请选择至少一条记录进行删除', function(){});
            return false;
        }
        var json = [];
        for (i = 0; i < objArray.length; i++) {
            var r = {};
            r.domain=objArray[i].domain;
            r.globalTransactionId=objArray[i].globalTransactionId;
            r.branchQualifier=objArray[i].branchQualifier;
            json.push(r)
        }
        layer.confirm('你确定要删除', {
            btn: ['确定','取消'] //按钮
        }, function(){
            $.ajax({
                type:"post",
                url:'/transaction/delete',
                dataType:"json",
                data:JSON.stringify(json),
                contentType : 'application/json;charset=UTF-8',
                success:function(data){
                    if(data.resCode == '200'){
                        parent.$("#modelTable").bootstrapTable('refresh',{silent: true});
                        parent.layer.msg(data.resMessage,{time:1200});
                        return true;
                    }else {
                        parent.layer.msg(data.resMessage);
                        return false;
                    }
                },
                error:function(data){
                    parent.layer.msg("错误："+data.resMessage);
                }
            })
        }, function(){
            layer.msg('取消操作', {
                time: 1500 //1.5s后自动关闭
            });
        });
    })

    //搜索按钮
    $("#modelSearch").click(function () {
        $("#modelTable").bootstrapTable('refresh',{
            silent: true,
            query: {
                domain:$("#domain").val(),
                maxRetries:$("#retries").val()
            }
        });
    });
</script>
</html>