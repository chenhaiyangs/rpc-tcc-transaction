[#ftl]
<!DOCTYPE html>
<!--suppress ALL -->
<html lang="ch">
<head>
    <meta charset="utf-8">
    <link rel="shortcut icon" type="image/ico" href="/public/images/logo/company-favicon-20.png">
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0">
    <meta name="author" content="chenhaiyang"/>
    <meta name="description" content=""/>
    <title>事务管理面板</title>
    <!--common style-->
    <link href="/public/bootstrap3/css/style.css" rel="stylesheet">
    <link href="/public/bootstrap3/css/style-responsive.css" rel="stylesheet">
    <link href="/public/plugins/bootstrap-treeview/css/bootstrap-treeview.css" rel="stylesheet">
    <link href="/public/plugins/bootstrap-tab/css/bootstrap-tab.css" rel="stylesheet">
    <!-- 引入bootstrap-table样式 -->
    <link href="/public/plugins/bootstrap-table/css/bootstrap-table.min.css" rel="stylesheet">
    <!-- 引入自定义样式 -->
    <link href="/public/css/util/util-global.css" rel="stylesheet">
    <link href="/public/css/util/util-center-table.css" rel="stylesheet">
    <!-- HTML5 shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!--[if lt IE 9]>
    <script src="/public/bootstrap3/js/html5shiv.js"></script>
    <script src="/public/bootstrap3/js/respond.min.js"></script>
    <![endif]-->
    <!-- 时间format-->
    <script src="/public/js/dateformat.js"></script>
</head>
<body class="sticky-header ops-homepage-body">
<section style="min-width: 770px;">
    <!-- sidebar left start-->
    <div class="sidebar-left">
        <!--responsive view logo start-->
        <div class="logo dark-logo-bg visible-xs-* visible-sm-*">
        </div>
        <!--responsive view logo end-->
        <div class="sidebar-left-info">
            <!--sidebar nav start-->
            <ul class="nav nav-pills nav-stacked side-navigation">
                <div id="treeview"></div>
            </ul>
            <!--sidebar nav end-->
        </div>
    </div>
    <!-- sidebar left end-->
    <!-- body content start-->
    <div class="body-content">
        <!-- header section start-->
        <div class="header-section">
            <!--logo and logo icon start-->
            <div class="logo dark-logo-bg hidden-xs hidden-sm">
                <a href="index.html">
                    <img src="/public/images/logo/logo.png" alt="" style="height: 27px;width: 27px;">
                    <span class="brand-name">tcc-transaction</span>
                </a>
            </div>
            <div class="icon-logo dark-logo-bg hidden-xs hidden-sm">
                <a href="index.html">
                    <img src="../public/bootstrap3/img/logo-icon.png" alt="">
                </a>
            </div>
            <!--logo and logo icon end-->
            <!--toggle button start-->
            <a class="toggle-btn"><i class="fa fa-outdent"></i></a>
            <!--toggle button end-->
            <div class="notification-wrap">
                <!--left notification start-->
                <div class="left-notification">
                    <div class="page-title">
                        <span class="sub-title">事务管理查询平台</span>
                    </div>
                </div>
                <!--left notification end-->
                <!--right notification start-->
                <div class="right-notification">
                    <ul class="notification-menu">
                        <li>
                            <a href="javascript:;" class="btn btn-default dropdown-toggle" data-toggle="dropdown">
                                <img src="../public/bootstrap3/img/logo-icon.png" alt="">admin
                                <span class=" fa fa-angle-down"></span>
                            </a>
                            <ul class="dropdown-menu dropdown-usermenu purple pull-right">
                                <li><a href="/logout"><i class="fa fa-sign-out pull-right"></i> 退出登录</a></li>
                            </ul>
                        </li>
                    </ul>
                </div>
                <!--right notification end-->
            </div>
        </div>
        <!-- header section end-->

        <!--body wrapper start-->
        <div class="wrapper" >
            <div id="tabContainer" class="ops-homepage-tab"></div>
        </div>
        <!--footer section start-->
        <footer style="position: fixed;">
            2018 © rpc-tcc-transaction Co.Ltd.正式版V1.0.0
        </footer>
        <!--footer section end-->
    </div>
    <!-- body content end-->
</section>
<!-- Placed js at the end of the document so the pages load faster -->
<script src="/public/js/jquery-3.2.1.min.js"></script>
<script src="/public/bootstrap3/js/bootstrap.min.js"></script>
<!--right slidebar-->
<script src="/public/bootstrap3/js/slidebars.min.js"></script>
<!--Nice Scroll-->
<script src="/public/plugins/bootstrap-treeview/js/bootstrap-treeview.js"></script>
<script src="/public/plugins/bootstrap-tab/js/bootstrap-tab.js"></script>

<!-- bootstrap-table.min.js -->
<script src="/public/plugins/bootstrap-table/js/bootstrap-table.min.js"></script>
<!-- 引入中文语言包 -->
<script src="/public/plugins/bootstrap-table/js/bootstrap-table-zh-CN.js"></script>
<script type="text/javascript" src="/public/js/util/util-table-init.js"></script>
<script type="text/javascript" src="/public/js/util/util-window.js"></script>
<script type="text/javascript" src="/public/plugins/layer/layer.js"></script>
<script type="text/javascript" src="/public/plugins/laydate/laydate.js"></script>
<script type="text/javascript">
    function buildDomTree() {
        var data = [
            {
                text: '事务管理',
                tags: ['9'],
                nodes: [
                    {
                        id:'transactionList',
                        text: '事务列表',
                        icon: 'glyphicon glyphicon-certificate'
                    }
                ]
            }
        ];
        return data;
    }

    $(document).ready(function () {
        var options = {
            expandIcon: "glyphicon glyphicon-chevron-right",
            collapseIcon: "glyphicon glyphicon-chevron-down",
            nodeIcon: "glyphicon glyphicon-user",
            color: "#f0f0f1",
            backColor: "#32323a",
            onhoverColor: "#29282f",
            borderColor: "#32323a",
            showBorder: true,
            highlightSelected: true,
            selectedColor: "#f0f0f1",
            selectedBackColor: "#17a2b8",
            bootstrap2: false,
            levels: 1,
            data: buildDomTree()
        };
        var tabObj = $("#tabContainer");
        var treeObj = $("#treeview");
        tabObj.tabs({
            data: [{
                id: 'home',
                text: '事务列表',
                url: "/transaction/list"
            }],
            loadAll: false
        });
        treeObj.treeview(options);
        treeObj.on('nodeSelected', function (event, data) {
            if(data.href==undefined || data.href=="#"){
                return false;
            }
            var $tab = tabObj.data("tabs");
            if(!$tab.isExists(data.id)){
                $tab.addTab({id: data.id, text: data.text, closeable: true, url: data.href})
            }else{
                $tab.showTab(data.id);
            }
        });
    });
    /**
     * 扩展layer,此代码不能删除！
     */
    layer.config({extend: 'extend/layer.ext.js'})
</script>
</body>
</html>