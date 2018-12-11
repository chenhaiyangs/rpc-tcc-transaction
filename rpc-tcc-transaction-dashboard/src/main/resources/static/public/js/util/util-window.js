;(function ($) {
    $.fn.extend({
        "openWindow": function (options) {
            width=options.winth||'600';
            height=options.height||'400';
            layer.open({
                type: 2,
                area: [width, height],
                title: options.title,
                maxmin: true, //开启最大化最小化按钮
                content: options.url,
                btn: ['确定', '关闭'],
                yes: function (index, layero) {
                    console.log("12345");
                    setTimeout(function(){top.layer.close(index)}, 100);
                },
                cancel: function (index) {
                    console.log("56789");
                }
            });
        }
    });
})(jQuery);