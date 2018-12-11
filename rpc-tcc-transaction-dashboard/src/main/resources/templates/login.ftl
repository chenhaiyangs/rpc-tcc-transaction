[#ftl]
<!--suppress ALL -->
[#import 'common.ftl' as c]
<html lang="en">

<head>
    [@c.head/]
    <link href="/css/floating-labels.css" rel="stylesheet">
    <link href="/css/styles.css" rel="stylesheet">
    <script src="/js/jquery.autocomplete.js"></script>
    <title>事务管理面板</title>
</head>

<body>
    <form class="form-signin" method="post">
        <div class="text-center mb-4">
            <h1 class="h3 mb-3 font-weight-normal" id="configToolkitAdmin">rpc-tcc-transaction</h1>
        </div>

        <div class="form-label-group">
            <input id="username" name="username" class="form-control"
                   placeholder="Root Node" required autofocus spellcheck="false" autocomplete="true">
            <label for="username">请输入用户名</label>
        </div>
        <div id="selction-ajax"></div>

        <div class="form-label-group">
            <input type="password" id="password" name="password" class="form-control" placeholder="Password" required="">
            <label for="password">请输入密码</label>
        </div>
        [#if RequestParameters['error']??]
            <div style="color: red;margin-bottom: 10px">
                用户名或密码错误，请重试！
            </div>
        [/#if]
        <button class="btn btn-lg btn-outline-dark btn-block">登录</button>
    </form>
</body>
<script type="text/javascript">
    $.ajax({
        url: "/tips",
        method: "post",
        success: function (data) {
            $('#username').autocomplete({
                lookup: data
            });
        }
    });
</script>
</html>