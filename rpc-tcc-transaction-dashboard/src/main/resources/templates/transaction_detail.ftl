[#ftl]
<!--suppress ALL -->
<html>
<head>
    <title></title>
    <link href="/public/bootstrap3/css/style.css" rel="stylesheet">
    <link href="/public/bootstrap3/css/style-responsive.css" rel="stylesheet">
    <link href="/public/css/util/util-form.css" rel="stylesheet">
    <style type="text/css">

    </style>
</head>
<body class="sticky-header">
<div class="row">
    <div class="col-xs-12">
        <section class="panel">
            <div class="panel-heading">
                <div align="center">事务详情</div>
            </div>
            <div class="panel-body ops-global-panel" style="text-align:left">
                <form class="form-horizontal tasi-form ops-global-form" id="modelForm" style="text-align: left">
                    <pre>${transaction}</pre>
                </form>
            </div>
            <div class="col-xs-12 ops-global-submit">
                <button class="btn btn-success col-xs-offset-3 col-xs-2" type="button" id = "modelSave">清空重试次数</button>
                <button class="btn btn-default col-xs-offset-2 col-xs-2" type="button" id = "modelCancle">取消</button>
            </div>
        </section>
    </div>
</div>
</body>
<script src="/public/js/jquery-3.2.1.min.js"></script>
<script type="text/javascript" src="/public/js/jquery.tips.js"></script>
<script>
    var frameIndex = parent.layer.getFrameIndex(window.name);
    $('#modelSave').click(function(){
        parent.layer.confirm('你确定要清空重试次数？', {
            btn: ['确定','取消'] //按钮
        }, function(){
            var r = {};
            r.domain='${domain}';
            r.globalTransactionId='${globalTransactionId}';
            r.branchQualifier='${branchQualifier}';
            $.ajax({
                type:"post",
                url:'/transaction/reset',
                dataType:"json",
                data:JSON.stringify(r),
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
            parent.layer.msg('取消操作', {
                time: 1500, //1.5s后自动关闭
            });
        });
    });
    $('#modelCancle').click(function(){
        parent.layer.msg('用户已取消', {
            time: 1200 //20s后自动关闭
        });
        parent.layer.close(frameIndex);
    });
</script>
</html>