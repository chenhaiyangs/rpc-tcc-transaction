(function ($) {
    'use strict';
    $.fn.bootstrapTable.columnDefaults = {
        align: 'center', // left, right, center
        halign: 'center', // left, right, center
        valign: 'middle' // top, middle, bottom
    };
    $.extend($.fn.bootstrapTable.columns, $.fn.bootstrapTable.columnDefaults);

})(jQuery);
